
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer implements Closeable
{

    private static final String TERMINATE_SIGNAL = String.valueOf('\0');

    private static final int PORT = 2020;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ServerSocket serverSocket;

    private final Object runningLock = new Object();
    private boolean running = false;

    private PacketReceivedEventHandler onPacketReceived = null;

    public MyServer()
    {
        try
        {
            serverSocket = new ServerSocket(PORT);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public boolean isRunning()
    {
        synchronized (runningLock)
        {
            return running;
        }
    }

    private void setRunning(boolean running)
    {
        synchronized (runningLock)
        {
            this.running = running;
        }
    }

    public PacketReceivedEventHandler getOnPacketReceived()
    {
        return onPacketReceived;
    }

    public void setOnPacketReceived(PacketReceivedEventHandler onPacketReceived)
    {
        this.onPacketReceived = onPacketReceived;
    }

    private String reverse(String text)
    {
        return new StringBuilder(text)
                .reverse()
                .toString();
    }

    private void handleReceivedSocket(Socket socket)
    {
        final String ipAddress = socket
                .getInetAddress()
                .toString();

        try (DataInputStream dis = new DataInputStream(socket.getInputStream()))
        {
            final String data = dis.readUTF();

            if (!TERMINATE_SIGNAL.equals(data))
            {
                final String reversed = reverse(data);

                if (onPacketReceived != null)
                {
                    onPacketReceived.received(ipAddress, data, reversed);
                }
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private void receive()
    {
        try
        {
            Socket receivedSocket = serverSocket.accept();
            executorService.submit(() -> handleReceivedSocket(receivedSocket));
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private void listenForNewConnections()
    {
        while (isRunning())
        {
            receive();
        }
    }

    private void startInNewThread()
    {
        Thread thread = new Thread(this::listenForNewConnections);
        thread.setName("my-server");
        thread.setDaemon(true);
        thread.start();
    }

    public void start()
    {
        if (isRunning())
        {
            return;
        }

        setRunning(true);
        startInNewThread();
    }

    private void sendTerminateSignal()
    {
        new MyClient().sendMessage(TERMINATE_SIGNAL);
    }

    public void stop()
    {
        if (!isRunning())
        {
            return;
        }

        setRunning(false);
        sendTerminateSignal();
    }

    @Override
    public void close() throws IOException
    {
        executorService.shutdownNow();
        serverSocket.close();
    }

}
