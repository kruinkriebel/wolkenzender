package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.Settings;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Robbert on 22-04-17.
 */
public class HeadBlockTextArea extends JTextArea {

    @Override
    public Color getBackground() {
        return Settings.COLOR_HEAD_BLOCK;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 120);
    }

}
