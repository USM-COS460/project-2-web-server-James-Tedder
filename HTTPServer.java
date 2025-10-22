package http.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;

/**
 *
 * @author jlt10
 */
public class HTTPServer {

    private final int port;
    private static final int THREAD_POOL_SIZE = 10;
    private final String documentRoot;
    
    
    public HTTPServer (int port, String documentRoot) {
        this.port = port;
        this.documentRoot = documentRoot;
    }
    void handleClient(Socket clientSocket) {
        try ( BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));  OutputStream out =
                clientSocket.getOutputStream() ){

            String line;
            String filePath = "";
            while (true) {
                line = in.readLine();
                if (line != null && line.equals("")) {
                    break;
                }
                if (line != null && line.contains("GET")) {
                    int start = line.indexOf("/");
                    int end = line.indexOf(" HTTP");
                    filePath = line.substring(start, end);
                }
            }
            try {
                if (filePath.equals("/")) {
                    filePath = "/index.html";
                }
                File file = new File(documentRoot, filePath);
                byte[] data = Files.readAllBytes(file.toPath());
                out.write(("HTTP/1.1 200 OK\r\n").getBytes());
                out.write(("Date: " + getServerTime() + "\r\n").getBytes());
                out.write(("Server: SimpleHTTPServer\r\n").getBytes());
                out.write(("Content-Type: ").getBytes());
                int start = filePath.indexOf(".");
                if (start != -1) {
                    String fileType = filePath.substring(start + 1);
                    switch (fileType) {
                        case "html":
                            out.write(("text/html").getBytes());
                            break;
                        case "jpeg":
                            out.write(("image/jpeg").getBytes());
                            break;
                        case "css":
                            out.write(("text/css").getBytes());
                            break;
                    }
                } 
                out.write(("\r\n").getBytes());
                
                out.write(("Content-Length: " + file.length() + "\r\n\r\n").getBytes());
                out.write(data);
                out.flush();

            } catch (IOException e) {
                out.write(("HTTP/1.1 404 \r\n").getBytes());
                out.write(("Date: "+ getServerTime() + "\r\n").getBytes());
                out.write(("Server: SimpleHTTPServer"+"\r\n").getBytes());
                out.write(("Content-Type: text/html"+"\r\n").getBytes());
                String content = """
                      <html>
                      <head>
                          <title>404 not found</title>
                      </head>
                      <body>
                            <p> 404 file not found
                      </body>
                      </html>
                      """;
                out.write(("Content-Length: " + content.length() + "\r\n\r\n").getBytes());
                out.write((content).getBytes());
                out.flush();
            }

        } catch (IOException e) {
        } 
    }
    
    private String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }
    
    void start() throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> handleClient(clientSocket));
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        int portNumber = Integer.parseInt(args[0]);
        String documentRoot = args[1];
        HTTPServer server = new HTTPServer(portNumber, documentRoot);
        server.start();
    }
    
}
