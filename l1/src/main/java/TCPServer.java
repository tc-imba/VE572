import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Hex;


public class TCPServer {
    private static Path fileDir = Paths.get(System.getProperty("user.dir"), "data");

    public static void main(String[] args) throws Exception {
        int clientNumber = 0;
        if (Files.notExists(fileDir)) {
            Files.createDirectory(fileDir);
        }
        try (ServerSocket listener = new ServerSocket(7650)) {
            while (true) {
                new Handler(listener.accept(), clientNumber++).start();
            }
        }
    }

    public static class Handler extends Thread {
        private Socket socket;
        private int clientNumber;
        private String date;
        private String inputBuffer = "";
        private String xmlFileName = "";
        private String binFileName = "";
        private DataExtractor dataExtractor = null;

        private enum State {
            BEGIN, XML, BIN, QUERY, END
        }

        private State state = State.BEGIN;

        Handler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            this.date = df.format(new Date());
        }

        private boolean runCommand(String[] args, PrintWriter output) throws Exception {
            switch (args[0].toLowerCase()) {
                case "begin":
                    if (state == State.BEGIN) {
                        state = State.XML;
                        log("Link starts.");
                        output.println("OK");
                    } else {
                        output.println("Error: command unexpected");
                    }
                    break;
                case "size":
                    if (state == State.XML && args.length > 2 && args[1].toLowerCase().equals("xml")) {
                        int size = Integer.parseInt(args[2]);
                        output.println("OK");
                        this.xmlFileName = this.saveFile(size, "xml");
                        if (xmlFileName.length() > 0) {
                            state = State.BIN;
                            String fileName = fileDir.resolve(this.xmlFileName).toString();
                            this.dataExtractor = new DataExtractor(fileName);
                            this.dataExtractor.parse();
                            output.println("OK");
                        } else {
                            output.println("Error: file error");
                        }
                    } else if (state == State.BIN && args.length > 2 && args[1].toLowerCase().equals("bin")) {
                        int size = Integer.parseInt(args[2]);
                        output.println("OK");
                        this.binFileName = this.saveFile(size, "bin");
                        if (binFileName.length() > 0) {
                            state = State.QUERY;
                            String fileName = fileDir.resolve(this.binFileName).toString();
                            this.dataExtractor.readBinary(fileName);
                            output.println("OK");
                        } else {
                            output.println("Error: file error");
                        }
                    } else {
                        output.println("Error: command unexpected");
                    }
                    break;
                case "query":
                    if (state == State.QUERY && args.length > 2) {
                        String op = args[1];
                        String name = args[2];
                        log("query " + name + " " + op);
                        output.println("OK");
                        output.println(this.dataExtractor.query(name, op));
                    } else {
                        output.println("Error: command unexpected");
                    }
                    break;
                case "end":
                    state = State.END;
                    output.println("OK");
                    log("Link ends.");
                    return false;
                default:
                    output.println("Error: command unexpected");
                    break;
            }
            return true;
        }

        private boolean readLine(BufferedReader input, PrintWriter output) throws Exception {
            this.inputBuffer += input.readLine();
            boolean flag = true;
            int pos;
            while ((pos = this.inputBuffer.indexOf(';')) >= 0) {
                String args[] = this.inputBuffer.substring(0, pos).split("\\s+");
                this.inputBuffer = this.inputBuffer.substring(pos + 1);
                if (args.length > 0 && args[0].length() > 0) {
                    flag &= this.runCommand(args, output);
                }
            }
            return flag;
        }

        public void run() {
            try {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter output = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                while (this.readLine(input, output)) ;
            } catch (Exception e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket.");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        private String saveFile(int size, String type) {
            log("File transfer starts.");
            log("Size: " + Integer.toString(size));
            log("Type: " + type);
            String fileName = "";
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                byte[] buffer = new byte[size];
                int length;
                int offset = 0;
                while ((length = dis.read(buffer, offset, Math.min(buffer.length, size - offset))) > 0) {
                    offset += length;
                }
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                String hash = new String(Hex.encodeHex(md5.digest(buffer))).substring(0, 6);


                fileName = this.date + "-" + hash + "." + type;
                log(fileName);

                FileOutputStream fos = new FileOutputStream(fileDir.resolve(fileName).toFile());
                fos.write(buffer);
                fos.close();
                log("File transfer end.");
            } catch (Exception e) {
                log("Error handling client# " + clientNumber + " in saveFile(): " + e);
            }
            return fileName;
        }

        private void log(String message) {
            System.out.println(message);
        }
    }

}
