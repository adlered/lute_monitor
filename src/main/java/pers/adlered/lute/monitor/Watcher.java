package pers.adlered.lute.monitor;

import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

import java.net.ServerSocket;
import java.net.Socket;

public class Watcher implements Runnable {

    private boolean isRunning = true;

    public void stopTheProxy() {
        isRunning = false;
    }

    @Override
    public void run() {
        try {
            int access = Integer.parseInt(Vals.ipLimitOptions.split("/")[0]);
            int second = Integer.parseInt(Vals.ipLimitOptions.split("/")[1]);
            SimpleCurrentLimiter simpleCurrentLimiter = new SimpleCurrentLimiter(second, access);
            System.out.println("[LuteMonitor] Limit Setting: " + access + " access in " + second + " second");
            ServerSocket serverSocket = new ServerSocket(Vals.monitorProxyPort);
            System.out.println("[LuteMonitor] Listening " + Vals.monitorProxyPort + " ...");
            while (isRunning) {
                Socket socket = serverSocket.accept();
                // IP 频率审核与记录
                String ip = socket.getInetAddress().getHostAddress();
                if (simpleCurrentLimiter.access(ip)) {
                    // 核验通过，允许使用 Lute 服务
                    Processor processor = new Processor(socket);
                    new Thread(processor).start();
                } else {
                    socket.close();
                    System.out.println("[Denied] " + ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
