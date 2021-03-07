package pers.adlered.lute.monitor;

import com.arronlong.httpclientutil.common.HttpHeader;
import org.apache.http.Header;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Processor implements Runnable {

    private final Socket socket;

    public Processor(Socket socket) {
        this.socket = socket;
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
            String request = stringBuilder.toString();

            String response = requestToServer(request);

            /*String body = "hello lutehttp~~你好世界";
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat greenwichDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            String response = "" +
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: text/plain; charset=utf-8\r\n" +
                    "Date: " + greenwichDate.format(calendar.getTime()) + "\r\n" +
                    "Server: fasthttp\r\n" +
                    "\r\n" +
                    body;*/
            byte[] responseByte = response.getBytes(StandardCharsets.UTF_8);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(responseByte);
            outputStream.flush();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String requestToServer(String request) throws IOException {
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
        System.out.print(content);

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

}
