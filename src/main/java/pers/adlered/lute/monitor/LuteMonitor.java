package pers.adlered.lute.monitor;

public class LuteMonitor {

    public static void main(String[] args) {
        if (!argsAvailable(args)) {
            System.out.println("Usage: java -jar LuteMonitor.jar [monitorProxyPort] [luteServer] [ipLimitOptions]\n" +
                    "luteServer      ||  Example: localhost:8249\n" +
                    "ipLimitOptions  ||  Example (1 IP 10 Access In 30 Second): 10/30");
            System.exit(-1);
        }

        Vals.monitorProxyPort = Integer.parseInt(args[0]);
        String luteServer = args[1];
        Vals.luteServerIP = luteServer.split(":")[0];
        Vals.luteServerPort = Integer.parseInt(luteServer.split(":")[1]);
        Vals.ipLimitOptions = args[2];

        Watcher watcherThread = new Watcher();
        new Thread(watcherThread).start();

    }

    private static boolean argsAvailable(String[] args) {
        for (int i = 0; i < 3; i++) {
            try {
                String arg = args[i];
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
