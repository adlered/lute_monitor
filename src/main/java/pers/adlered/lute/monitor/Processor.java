package pers.adlered.lute.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Processor implements Runnable {

    private Socket socket;

    public Processor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            /**
             * step = 0 正在接收 head 部分请求
             * step = 1 正在接收请求体
             * step = 2 接收完毕
             */
            int step = 0;
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), false);
            for (String line = null; (line = bufferedReader.readLine()) != null; ) {
                stringBuilder.append(line).append("\n");
                if (line.isEmpty()) {
                    step++;
                }
                if (step == 2) {
                    break;
                }
            }
            System.out.println(stringBuilder.toString());
            printWriter.print("HTTP/1.1 200 OK\r\n" +
                    "Content-Length: 981\r\n" +
                    "Connection: keep-alive\r\n" +
                    "Content-Type: text/plain; charset=utf-8\r\n" +
                    "Date: Sat, 06 Mar 2021 14:57:24 GMT\r\n" +
                    "Keep-Alive: timeout=4\r\n" +
                    "Proxy-Connection: keep-alive\r\n" +
                    "Server: fasthttp\r\n\r\n" +
                    "hello lute!\r\n\r\n");
            printWriter.flush();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
