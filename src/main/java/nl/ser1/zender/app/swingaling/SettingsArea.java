package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.Settings;

/**
 * Created by Robbert on 22-04-17.
 */
public class SettingsArea extends HeadBlockTextArea {

    public SettingsArea() {
        append("- Capture interval: " + Settings.CAPTURE_INTERVAL_SECONDS + " seconds" + "\n");
        append("- Capture size: " + Settings.CAPTURE_RESOLUTION.width + " x " + Settings.CAPTURE_RESOLUTION.height + "\n");
        append("- Capture format: " + Settings.CAPTURE_FORMAT_EXTENSION + "\n");
        if (Settings.USE_TIMEWINDOW) {
            append("- Capture window from: " + Settings.CAPTURE_WINDOW_START_TIME + "\n");
            append("- Capture window to: " + Settings.CAPTURE_WINDOW_END_TIME + "\n");
        } else {
            append("- Capture window OFF\n");
        }
        append("- State checks: " + (Settings.CHECK_STATE ? ("Yes, every " + Settings.CHECK_STATE_EVERY_X_SHOTS + " pictures ") : "No") + "\n");

    }
}
