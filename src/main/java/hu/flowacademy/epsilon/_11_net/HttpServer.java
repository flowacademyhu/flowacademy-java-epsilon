package hu.flowacademy.epsilon._11_net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newCachedThreadPool();

        ServerSocket ss = new ServerSocket(8080);
        for (;;) {
            System.out.println("Waiting on 8080");
            final Socket s = ss.accept();

            es.execute(() -> {
                try {
                    try {
                        System.out.println("Got connection from " + s.getInetAddress());
                        InputStream in = s.getInputStream();

                        BufferedReader r = new BufferedReader(
                            new InputStreamReader(in, StandardCharsets.ISO_8859_1));

                        String requestLine = r.readLine();
                        System.out.println(requestLine);
                        StringTokenizer tok = new StringTokenizer(requestLine, " ");
                        String action = tok.nextToken();
                        String url = tok.nextToken();
                        String protocol = tok.nextToken();

                        String name = "World";
                        int qm = url.indexOf('?');
                        if (qm != -1) {
                            String params = url.substring(qm + 1);
                            StringTokenizer paramTok = new StringTokenizer(params, "&");
                            while (paramTok.hasMoreTokens()) {
                                String paramSpec = paramTok.nextToken();
                                StringTokenizer paramTok2 = new StringTokenizer(paramSpec, "=");
                                String paramName = paramTok2.nextToken();
                                String paramValue = paramTok2.nextToken();

                                if ("name".equals(paramName)) {
                                    name = paramValue;
                                }
                            }
                        }

                        for (;;) {
                            String l = r.readLine();
                            if ("".equals(l)) {
                                break;
                            }
                            System.out.println(l);
                        }

                        OutputStream out = s.getOutputStream();
                        PrintWriter pw = new PrintWriter(
                            new OutputStreamWriter(out, StandardCharsets.ISO_8859_1));
                        pw.println("HTTP/1.1 200 OK");
                        pw.println("Content-Type: text/html; charset=ISO-8859-1");
                        pw.println("");
                        pw.println("<html><body><h1>Hello " + name + "</h1></body></html>");
                        pw.flush();
                        out.close();
                        Thread.sleep(10000);
                    } finally {
                        s.close();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
