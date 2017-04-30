package nl.ser1.zender.app;

import com.github.sarxos.webcam.WebcamResolution;

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

    // schedules
    public static final boolean CHECK_STATE = true;
    public static final int CHECK_STATE_EVERY_X_SHOTS = 10;
    public static final boolean USE_TIMEWINDOW = false;
    public static final LocalTime CAPTURE_WINDOW_START_TIME = LocalTime.of(5, 30, 0);

    public static final LocalTime CAPTURE_WINDOW_END_TIME = LocalTime.of(21, 0, 0);
    // picture & video
    public static final int CAPTURE_WIDTH = 1920;
    public static final int CAPTURE_HEIGHT = 1080;
    public static final Dimension DIMENSION_LOGITECH_C920_MAX = new Dimension(CAPTURE_WIDTH, CAPTURE_HEIGHT);
    //public static final Dimension CAPTURE_RESOLUTION = WebcamResolution.HD720.getSize();
    public static final Dimension CAPTURE_RESOLUTION = DIMENSION_LOGITECH_C920_MAX;
    public static final int CAPTURE_INTERVAL_SECONDS = 20;
    public static final String DIRECTORY_OUTPUT_PICTURES = "output/pictures";
    public static final String DIRECTORY_OUTPUT_MOVIES = "output/movies";
    public static final String CAMERA_IDENTIFIER = "C920";
    public static final String CAPTURE_FORMAT_EXTENSION = "JPEG"; // this will actually also set the image format that's saved

    // audio


}
