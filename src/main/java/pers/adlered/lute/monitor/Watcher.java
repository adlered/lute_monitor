package pers.adlered.lute.monitor;

import java.net.ServerSocket;
import java.net.Socket;

public class Watcher implements Runnable {

    final private int monitorProxyPort;
    private boolean isRunning = true;

    public Watcher(int monitorProxyPort) {
        this.monitorProxyPort = monitorProxyPort;
    }

    public void stopTheProxy() {
        isRunning = false;
    }

    @Override
    public void run() {
        try {
            System.out.println("[LuteMonitor] Listening " + monitorProxyPort + " ...");
            ServerSocket serverSocket = new ServerSocket(monitorProxyPort);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                // IP 频率审核与记录

                // 核验通过，允许使用 Lute 服务
                Processor processor = new Processor(socket);
                new Thread(processor).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
