package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.Settings;

/**
 * Created by Robbert on 22-04-17.
 */
public class SettingsArea extends HeadBlockTextArea {

    public SettingsArea() {
        append("- Capture interval: " + Settings.CAPTURE_INTERVAL_SECONDS + " seconds" + "\n");
        append("- Capture size: " + Settings.LOGITECH_C920_MAX_CAPTURE_WIDTH + " x " + Settings.LOGITECH_C920_MAX_CAPTURE_HEIGHT +"\n");
        append("- Capture format: " + Settings.CAPTURE_FORMAT_EXTENSION + "\n");
        append("- Capture window from: " + Settings.CAPTURE_WINDOW_START_TIME + "\n");
        append("- Capture window to: " + Settings.CAPTURE_WINDOW_END_TIME + "\n");
    }
}
