package nl.ser1.zender.tasks;

import nl.ser1.zender.app.Settings;
import nl.ser1.zender.scooped.moviemaker.JpegImagesToMovie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.media.MediaLocator;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Vector;

/**
 * Created by Robbert on 19-04-17.
 */
public class CreateMovieTask implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(CreateMovieTask.class);

    private Vector<String> images;
    private String fileName;
    private int screenWidth, screenHeight;

    public CreateMovieTask(Vector<String> images, int screenWidth, int screenHeight) {
        this.images = images;
        this.fileName = createFilename();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void run() {

        LOGGER.info("Starting movie creation");

        JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
        MediaLocator oml;

        try {
            if ((oml = imageToMovie.createMediaLocator(fileName)) == null) {
                LOGGER.error("Cannot build media locator from: " + fileName);
            }
            int interval = 50;

            imageToMovie.doIt(screenWidth, screenHeight, (1000 / interval), images, oml);
        } catch (MalformedURLException e) {
            LOGGER.error("URL shit", e);
        }

        LOGGER.info("Movie created at " + fileName);

    }

    private String createFilename() {
        return String.format(Settings.DIRECTORY_OUTPUT_MOVIES + "/wolk-%s.mov", LocalDateTime.now());
    }
}
