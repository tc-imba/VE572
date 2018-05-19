import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


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
        private String fileName = "server-test";
        private String fileDir = "/home/zzhou612/Documents/IdeaProjects/socket-playground/data/";

        public Handler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
        }

        public void run() {
            try (BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
                 PrintWriter output = new PrintWriter(
                         new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)) {
                while (true) {
                    String command = input.readLine();
                    output.println("OK");
                    if (command.startsWith("BEGIN")) {
                        log("Link starts.");
                    } else if (command.startsWith("SIZE")) {
                        String args[] = command.split("\\s|;");
                        String type = args[1];
                        int size = Integer.parseInt(args[2]);
                        saveFile(size, type);
                    } else if (command.startsWith("QUERY")) {
                        log("Query...");
                    } else if (command.startsWith("END")) {
                        log("Link ends.");
                        return;
                    }
                }
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
                FileOutputStream fos = new FileOutputStream(fileDir + fileName + "." + type);
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
