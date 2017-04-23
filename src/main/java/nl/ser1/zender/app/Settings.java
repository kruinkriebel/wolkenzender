package nl.ser1.zender.app;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by Robbert on 19-04-17.
 */
public class Settings {

    // visuals
    public static final Color COLOR_BORDER = new Color(50, 150, 255);
    public static final Color COLOR_HEAD_PANEL = new Color(100, 200, 255);
    public static final Color COLOR_HEAD_BLOCK = new Color(75, 175, 255);
    public static final Color COLOR_LOG_AREA = new Color(0, 125, 200);

    // schedule
    // TODO NTF would be nice to auto-link to sunrise/fall
    public static final LocalTime CAPTURE_WINDOW_START_TIME = LocalTime.of(6, 0, 0);
    public static final LocalTime CAPTURE_WINDOW_END_TIME = LocalTime.of(21, 0, 0);

    // picture & video
    public static final int LOGITECH_C920_MAX_CAPTURE_WIDTH = 1920;
    public static final int LOGITECH_C920_MAX_CAPTURE_HEIGHT = 1080;
    public static final int CAPTURE_INTERVAL_SECONDS = 20;
    public static final String DIRECTORY_OUTPUT_PICTURES = "output/pictures";
    public static final String DIRECTORY_OUTPUT_MOVIES = "output/movies";
    public static final String CAMERA_IDENTIFIER = "C920";
    public static final String CAPTURE_FORMAT_EXTENSION = "JPEG"; // this will actually also set the image format that's saved

    // audio


}
