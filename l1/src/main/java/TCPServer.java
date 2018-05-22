import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;


public class TCPServer {
    public static void main(String[] args) throws Exception {
        int clientNumber = 0;
        try (ServerSocket listener = new ServerSocket(7650)) {
            while (true) {
                new Handler(listener.accept(), clientNumber++).start();
            }
        }
    }

    public static class Handler extends Thread {
        private Socket socket;
        private int clientNumber;
        private String fileName = "test";
        private Path fileDir = Paths.get(System.getProperty("user.dir"), "l1", "data");
        private String inputBuffer;
        private DataExtractor dataExtractor;

        private enum State {
            BEGIN, XML, BIN, QUERY, END
        }

        private State state = State.BEGIN;

        public Handler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
        }

        public boolean runCommand(String[] args, PrintWriter output) {
            switch (args[0]) {
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
                    if (state == State.XML && args.length > 2 && args[1].equals("xml")) {
                        state = State.BIN;
                        // read xml
                        int size = Integer.parseInt(args[2]);
                        this.saveFile(size, "xml");
                        output.println("OK");
                    } else if (state == State.BIN && args.length > 2 && args[1].equals("bin")) {
                        state = State.QUERY;
                        // read bin
                        int size = Integer.parseInt(args[2]);
                        this.saveFile(size, "bin");
                        output.println("OK");
                    } else {
                        output.println("Error: command unexpected");
                    }
                    break;
                case "query":
                    if (state == State.QUERY && args.length > 2) {
                        String Op = args[1];
                        String Name = args[2];
                        output.println("OK");
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

        public boolean readLine(BufferedReader input, PrintWriter output) throws IOException {
            this.inputBuffer += input.readLine().toLowerCase();
            int pos = this.inputBuffer.indexOf(';');
            if (pos < 0) return true;
            String args[] = this.inputBuffer.substring(0, pos).split("\\s");
            this.inputBuffer = this.inputBuffer.substring(pos + 1);
            for (String arg : args) {
                System.out.println(arg);
            }
            if (args.length > 0) {
                return this.runCommand(args, output);
            }
            return true;
        }

        public void run() {
            try {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter output = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                while (this.readLine(input, output)) ;
                    /*String command = input.readLine();
                    output.println("OK");
                    if (command.startsWith("BEGIN")) {
                        log("Link starts.");
                    } else if (command.startsWith("SIZE")) {
                        String args[] = command.split("\\s|;");
                        String type = args[1].toLowerCase();
                        int size = Integer.parseInt(args[2]);
                        saveFile(size, type);
                        output.println("OK");
                    } else if (command.startsWith("QUERY")) {
                        String args[] = command.split("\\s|;");
                        String Op = args[1];
                        String Name = args[2];
//                        String result = new DataExtractor(Name).run();
//                        log("Query: " + result);
//                        output.println("RESULT "+result);
                    } else if (command.startsWith("END")) {
                        log("Link ends.");
                        return;
                    }
                }*/
            } catch (IOException e) {
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

        private void saveFile(int size, String type) {
            log("File transfer starts.");
            log("Size: " + Integer.toString(size));
            log("Type: " + type);
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                FileOutputStream fos = new FileOutputStream(fileDir.resolve(fileName + "." + type).toFile());
                byte[] buffer = new byte[size];
                int length;
                int remaining = size;
                while ((length = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                    fos.write(buffer, 0, length);
                    remaining -= length;
                }
                fos.close();
                log("File transfer end.");
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + " in saveFile(): " + e);
            }
        }

        private void log(String message) {
            System.out.println(message);
        }
    }

}
