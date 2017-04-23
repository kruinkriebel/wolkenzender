package nl.ser1.zender.tasks;

import com.github.sarxos.webcam.Webcam;
import nl.ser1.zender.app.Settings;
import nl.ser1.zender.app.images.ImagesManager;
import nl.ser1.zender.app.userlog.UserLogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by Robbert on 19-04-17.
 */
public class TakePictureTask implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(TakePictureTask.class);
    public static final Dimension DIMENSION_LOGITECH_C920_MAX = new Dimension(Settings.LOGITECH_C920_MAX_CAPTURE_WIDTH, Settings.LOGITECH_C920_MAX_CAPTURE_HEIGHT);

    public static final String FORMAT_EXTENSION = "JPEG";
    private Webcam webcam;
    private ImagesManager imagesManager;
    private UserLogManager userLogManager;

    public TakePictureTask(UserLogManager userLogManager, ImagesManager imagesManager) {
        this.userLogManager=userLogManager;
        this.imagesManager=imagesManager;

        fetchWebcam(userLogManager);

        setWhopperResolution();
    }

    private void setWhopperResolution() {
        Dimension[] nonStandardResolutions = new Dimension[] {
                DIMENSION_LOGITECH_C920_MAX
        };

        webcam.setCustomViewSizes(nonStandardResolutions);
        webcam.setViewSize(DIMENSION_LOGITECH_C920_MAX.getSize());
    }

    private void fetchWebcam(UserLogManager userLogManager) {
        Webcam.getWebcams().forEach(w -> userLogManager.sendUserLog("Available camera's: " + w.getName()));
        webcam = Webcam.getDefault();
        userLogManager.sendUserLog("Using default webcam: " + webcam.getName());
    }

    @Override
    public void run() {

        //TODO opening and closing webcam can be too much overhead (for short intervals)

        webcam.open();

        try {
            String fileName = createFilename();
            File file = createFile(fileName);

            ImageIO.write(webcam.getImage(), FORMAT_EXTENSION, file);
            imagesManager.add(file.getCanonicalPath());

            String log = "Webcam image taken. Written at: " + file.getCanonicalPath();
            LOGGER.info(log);
            userLogManager.sendUserLog("Webcam image taken: " + file.getName());

        } catch (IOException e) {
            LOGGER.error("Woopsydaisy.", e);
        }

        webcam.close();

    }

    private File createFile(String fileName) {
        File file = new File(fileName);
        if (Settings.DELETE_CAPTURED_PICTURES_ON_EXIT) {
            file.deleteOnExit();
        }
        return file;
    }

    private String createFilename() {
        return String.format("output/pictures/wolk-%s."+FORMAT_EXTENSION.toLowerCase(), LocalDateTime.now());
    }
}
