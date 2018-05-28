import java.io.*;
import java.net.*;

class TCPClient {
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        if (args.length > 0) {
            host = args[0];
        }
        System.out.println(args.length);
        Socket socket = new Socket(host, 7650);
        BufferedReader input = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), "UTF-8"));
        PrintWriter output = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        output.println("BEGIN;;;;;;;");
        System.out.println("BEGIN;");
        System.out.println(input.readLine());

        File file = new File("./specs/validate.xml");
        FileInputStream fis = new FileInputStream(file);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        output.format("SIZE    XML    %d;\n", file.length());
        System.out.println("SIZE XML " + file.length() + ";");
        System.out.println(input.readLine());
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
            dos.write(bytes, 0, length);
        }
        fis.close();
        System.out.println(input.readLine());

        file = new File("./specs/validate.bin");
        fis = new FileInputStream(file);
        dos = new DataOutputStream(socket.getOutputStream());
        output.format("SIZE BIN %d;\n", file.length());
        System.out.println("SIZE BIN " + file.length() + ";");
        System.out.println(input.readLine());
        while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
            dos.write(bytes, 0, length);
        }
        fis.close();
        System.out.println(input.readLine());


        output.println("QUERY MAX 😂😏😏😏🙄;");
        System.out.println("QUERY MAX 😂😏😏😏🙄;");
        System.out.println(input.readLine());
        System.out.println(input.readLine());

        output.println("END;");
        System.out.println("END;");
        System.out.println(input.readLine());

        socket.close();
        System.exit(0);
    }
}
