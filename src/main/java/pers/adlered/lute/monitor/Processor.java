package pers.adlered.lute.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Processor implements Runnable {

    private final Socket socket;

    public Processor(Socket socket) {
        this.socket = socket;
    }

    private String requestToServer(String request) throws IOException {
        Socket socket = new Socket(Vals.luteServerIP, Vals.luteServerPort);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(request.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = socket.getInputStream();
        String line;
        int contentLength = 0;
        do {
            line = readLine(inputStream, 0);
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
            stringBuilder.append(line);
        } while (!line.equals("\r\n"));
        String content = readLine(inputStream, contentLength);
        stringBuilder.append(content);
        // 消除 content 的换行符，方便打印到日志
        content = content.replaceAll("\r|\n", "");
        if (content.length() <= 30) {
            System.out.println("[Process] " + System.currentTimeMillis() + " | " + this.socket.getInetAddress().getHostAddress() +
                    " | " + content);
        } else {
            System.out.println("[Process] " + System.currentTimeMillis() + " | " + this.socket.getInetAddress().getHostAddress() +
                    " | " + content.substring(0, 30) + " ......");
        }
        socket.close();
        return stringBuilder.toString();
    }

    private static String readLine(InputStream inputStream, int contentLength) throws IOException {
        ArrayList<Byte> lineByteList = new ArrayList<>();
        byte readByte;
        int total = 0;
        if (contentLength != 0) {
            byte[] byteArr = new byte[contentLength];
            do {
                readByte = (byte) inputStream.read();
                byteArr[total] = readByte;
                total++;
            } while (total < contentLength);
            return new String(byteArr, StandardCharsets.UTF_8);
        } else {
            do {
                readByte = (byte) inputStream.read();
                lineByteList.add(readByte);
            } while (readByte != 10);
        }

        byte[] tmpByteArr = new byte[lineByteList.size()];
        for (int i = 0; i < lineByteList.size(); i++) {
            tmpByteArr[i] = lineByteList.get(i);
        }
        lineByteList.clear();

        return new String(tmpByteArr, StandardCharsets.UTF_8);
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            int contentLength = 0;
            do {
                line = readLine(inputStream, 0);
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
                stringBuilder.append(line);
            } while (!line.equals("\r\n"));
            String content = readLine(inputStream, contentLength);
            stringBuilder.append(content);
            // 消除 content 的换行符，方便打印到日志
            content = content.replaceAll("\r|\n", "");
            if (content.length() <= 30) {
                System.out.println("[Request] " + System.currentTimeMillis() + " | " + this.socket.getInetAddress().getHostAddress() +
                        " | " + content);
            } else {
                System.out.println("[Request] " + System.currentTimeMillis() + " | " + this.socket.getInetAddress().getHostAddress() +
                        " | " + content.substring(0, 30) + " ......");
            }
            String request = stringBuilder.toString();
            // 缓存
            String response;
            try {
                response = Cache.read(request);
                content = response.replaceAll("\r|\n", "");
                if (content.length() <= 30) {
                    System.out.println("[Cache] " + System.currentTimeMillis() + " | " + this.socket.getInetAddress().getHostAddress() +
                            " | " + content);
                } else {
                    System.out.println("[Cache] " + System.currentTimeMillis() + " | " + this.socket.getInetAddress().getHostAddress() +
                            " | " + content.substring(0, 30) + " ......");
                }
            } catch (NullPointerException e) {
                response = requestToServer(request);
                Cache.write(request, response);
            }
            byte[] responseByte = response.getBytes(StandardCharsets.UTF_8);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(responseByte);
            outputStream.flush();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
