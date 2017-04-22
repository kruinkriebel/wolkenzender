package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.Settings;

/**
 * Created by Robbert on 22-04-17.
 */
public class SettingsArea extends HeadBlockTextArea {

    public SettingsArea() {
        append("- Delete images on exit: " + Settings.DELETE_CAPTURED_PICTURES_ON_EXIT+"\n");
        append("- Capture interval: " + Settings.CAPTURE_INTERVAL_SECONDS + " seconds" +
                "\n");
        append("- Capture size: " + Settings.CAPTURE_WIDTH + " x " + Settings.CAPTURE_HEIGHT+"\n");
    }
}
