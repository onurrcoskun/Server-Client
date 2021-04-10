
import java.text.SimpleDateFormat;
import java.util.Date;
public class Main
{

    public static void main(String[] args)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        Date now = new Date();
        System.out.println(dateFormat.format(now));
    }

}
