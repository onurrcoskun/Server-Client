
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MyClient
{
    private static final String HOST = "localhost";

    private static final int PORT = 2020;

    public void sendMessage(String message)
    {
        try (
                Socket socket = new Socket(HOST, PORT);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dos.writeUTF(message);
            dos.flush();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

}
