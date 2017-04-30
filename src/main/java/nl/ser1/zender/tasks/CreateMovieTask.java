package nl.ser1.zender.tasks;

import nl.ser1.zender.app.managers.Managers;
import nl.ser1.zender.app.Settings;
import nl.ser1.zender.app.state.Action;
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

    public CreateMovieTask() {
    }

    @Override
    public void run() {

        Managers.USERLOG_MAN.sendUserLog("Starting movie creation");
        String filename = createFilename();

        try {


            JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
            MediaLocator oml;
            if ((oml = imageToMovie.createMediaLocator(filename)) == null) {
                LOGGER.error("Cannot build media locator from: " + filename);
            }
            imageToMovie.doIt(Settings.CAPTURE_RESOLUTION.width, Settings.CAPTURE_RESOLUTION.height, 30, new Vector(Managers.IMAGES_MAN.getImages()), oml);

        } catch (MalformedURLException e) {
            LOGGER.error("URL shit", e);
        } finally {
            Managers.STATE_MAN.performAction(Action.STOP_CREATING_MOVIE);
        }

        Managers.USERLOG_MAN.sendUserLog("Movie created at " + filename);

    }

    private String createFilename() {
        return String.format(Settings.DIRECTORY_OUTPUT_MOVIES + "/wolk-%s.mov", LocalDateTime.now());
    }
}
