import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) {
        // 定义端口
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port + "...");

            // while (true) {
                // 等待客户端连接
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected by " + clientSocket.getRemoteSocketAddress());

                // 处理客户端连接
                try (InputStream input = clientSocket.getInputStream();
                     OutputStream output = clientSocket.getOutputStream()) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    // 读取客户端发送的数据
                    while ((bytesRead = input.read(buffer)) != -1) {
                        String receivedData = new String(buffer, 0, bytesRead);
                        System.out.println("Received: " + receivedData);

                        // 将数据返回给客户端
                        output.write(buffer, 0, bytesRead);
                        output.flush();
                    }
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            // }
        } catch (IOException e) {
            System.err.println("Could not start server on port " + port + ": " + e.getMessage());
        }
    }
}