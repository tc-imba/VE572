import java.io.*;
import java.net.*;

class TCPClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 7650);
        BufferedReader input = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), "UTF-8"));
        PrintWriter output = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        output.println("BEGIN;");
        output.println("QUERY;");
        File file = new File("/home/zzhou612/Documents/IdeaProjects/socket-playground/data/client-test.txt");
        FileInputStream fis = new FileInputStream(file);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        output.format("SIZE txt %d;\n", file.length());
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
            dos.write(bytes, 0, length);
        }
        output.println("QUERY;");
        output.println("END;");
        socket.close();
        System.exit(0);
    }
}