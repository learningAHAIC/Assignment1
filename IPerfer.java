import java.io.*;
import java.net.*;
// import java.util.*;

public class IPerfer {

    static class Flags {
        int mode = -1; // -1 for uninitialized, 0 for client, 1 for server
        // client
        String server_hostname = "";
        int server_port = -1;
        int time = -1;
        // server;
        int listen_port = -1;
    }

    private static void runClient(Flags f) {
        try {
            Socket socket = new Socket(f.server_hostname, f.server_port);
            OutputStream out = socket.getOutputStream();

            byte[] data = new byte[1024];
            long startTime = System.currentTimeMillis();
            long endTime = startTime + f.time * 1000;
            long totalBytesSent = 0;

            while (System.currentTimeMillis() < endTime) {
                out.write(data);
                totalBytesSent += data.length;
            }

            long duration = System.currentTimeMillis() - startTime;
            double rate = (totalBytesSent * 8.0) / (duration * 1000.0);

            System.out.printf("sent=%d KB rate=%.3f Mbps%n", totalBytesSent / 1024, rate);

            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runServer(Flags f) {
        try {
            ServerSocket serverSocket = new ServerSocket(f.listen_port);
            Socket clientSocket = serverSocket.accept();
            InputStream in = clientSocket.getInputStream();

            byte[] data = new byte[1024];
            long totalBytesReceived = 0;
            long startTime = System.currentTimeMillis();

            while (true) {
                int bytesRead = in.read(data);
                if (bytesRead == -1) {
                    break;
                }
                totalBytesReceived += bytesRead;
            }

            long duration = System.currentTimeMillis() - startTime;
            double rate = (totalBytesReceived * 8.0) / (duration * 1000); // Mbps
            
            System.out.printf("received=%d KB rate=%.3f Mbps%n", totalBytesReceived / 1024, rate);

            in.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Flags f = new Flags();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                    f.mode = 0;
                    break;
                case "-s":
                    f.mode = 1;
                    break;
                case "-h":
                    if (i + 1 >= args.length || args[i + 1].charAt(0) == '-') {
                        System.err.println("Error: missing or additional arguments");
                        return;
                    }
                    if (f.mode == 0) {
                        f.server_hostname = args[++i];
                        break;
                    } else {
                        System.err.println("Error: -h flag must be used with -c flag");
                        return;
                    }
                case "-p":
                    if (i + 1 >= args.length || args[i + 1].charAt(0) == '-') {
                        System.err.println("Error: missing or additional arguments");
                        return;
                    }
                    if (f.mode == 0) {
                        f.server_port = Integer.parseInt(args[++i]);
                    } else if (f.mode == 1) {
                        f.listen_port = Integer.parseInt(args[++i]);
                    } else {
                        System.err.println("Error: -p flag must be used with -c or -s flag");
                        return;
                    }
                    break;
                case "-t":
                    if (i + 1 >= args.length || args[i + 1].charAt(0) == '-') {
                        System.err.println("Error: missing or additional arguments");
                        return;
                    }
                    if (f.mode == 0) {
                        f.time = Integer.parseInt(args[++i]);
                        break;
                    } else {
                        System.err.println("Error: -t flag must be used with -c flag");
                        return;
                    }
                default:
                    System.err.println("Error: missing or additional arguments");
                    return;
            }
        }
        
        if (f.mode == -1) {
            System.err.println("Error: missing or additional arguments");
            return;
        } else if (f.mode == 0) {
            if ((f.server_hostname == "") || (f.server_port == -1) || (f.time == -1)) {
                System.err.println("Error: missing or additional arguments");
                return;
            }
            if (f.server_port < 1024 || f.server_port > 65535  ) {
                System.err.println("Error: port number must be in the range 1024 to 65535");
                return;
            }
            // run client
            runClient(f);
        } else {
            if (f.listen_port == -1) {
                System.err.println("Error: missing or additional arguments");
                return;
            }
            if (f.listen_port < 1024 || f.listen_port > 65535  ) {
                System.err.println("Error: port number must be in the range 1024 to 65535");
                return;
            }
            // run server
            runServer(f);
        }

    }
}