package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.Settings;

import javax.swing.*;

/**
 * Created by Robbert on 22-04-17.
 */
public class HeadBlockPanel extends JPanel {

    public HeadBlockPanel(String title, HeadBlockTextArea textArea) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JPanel labelPanel = new JPanel();
        setBorder(BorderFactory.createLineBorder(Settings.COLOR_BORDER, 2));
        add(labelPanel);
        labelPanel.add(new JLabel(title));
        add(textArea);
    }



}
