package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.Settings;
import nl.ser1.zender.app.WolkenZender;
import nl.ser1.zender.app.userlog.UserLogReceiver;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Robbert on 22-04-17.
 */
public class LogTextPanel extends JPanel implements UserLogReceiver {

    private JTextArea logArea;

    public LogTextPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Settings.COLOR_BORDER, 2));
        logArea = new JTextArea();
        logArea.setBackground(Settings.COLOR_LOG_AREA);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, getFont().getSize()));
        logArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WolkenZender.APPLICATION_WIDTH, 480);
    }

    @Override
    public void receive(String log) {
        int lineCount = logArea.getLineCount();
        if (lineCount >= 1000) {
            logArea.setText("<screen logging reset>");
        }
        logArea.append(StringUtils.rightPad(lineCount+"",3) + " " + log);
    }

}
