
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public abstract class MyWindow extends Application
{

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    protected final TextArea txtInfo;

    protected synchronized final void appendInfoLine(String line)
    {
        final String text = String.format("%s - %s\n", dateFormat.format(new Date()), line);
        Platform.runLater(() -> txtInfo.appendText(text));
    }

    protected MyWindow()
    {
        txtInfo = new TextArea();
        txtInfo.setFont(new Font(15d));
        txtInfo.setEditable(false);
        txtInfo.setWrapText(false);
    }

}
