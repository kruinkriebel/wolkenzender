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
import java.time.LocalTime;

import static nl.ser1.zender.app.Settings.CAMERA_IDENTIFIER;
import static nl.ser1.zender.app.Settings.CAPTURE_FORMAT_EXTENSION;

/**
 * Created by Robbert on 19-04-17.
 */
public class TakePictureTask implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(TakePictureTask.class);
    public static final Dimension DIMENSION_LOGITECH_C920_MAX = new Dimension(Settings.LOGITECH_C920_MAX_CAPTURE_WIDTH, Settings.LOGITECH_C920_MAX_CAPTURE_HEIGHT);

    private Webcam webcam;
    private ImagesManager imagesManager;
    private UserLogManager userLogManager;

    public TakePictureTask(UserLogManager userLogManager, ImagesManager imagesManager) {
        this.userLogManager = userLogManager;
        this.imagesManager = imagesManager;

        fetchWebcam();

        setWhopperResolution();

    }

    private void setWhopperResolution() {
        Dimension[] nonStandardResolutions = new Dimension[]{
                DIMENSION_LOGITECH_C920_MAX
        };

        webcam.setCustomViewSizes(nonStandardResolutions);
        webcam.setViewSize(DIMENSION_LOGITECH_C920_MAX.getSize());

    }

    private void fetchWebcam() {
        Webcam.getWebcams().forEach(w -> userLogManager.sendUserLog("Camera found: " + w.getName()));
        webcam = Webcam.getWebcams().stream().filter(w -> w.getName().contains(CAMERA_IDENTIFIER)).findFirst().orElse(Webcam.getDefault());
        userLogManager.sendUserLog("Using webcam: " + webcam.getName());
    }

    @Override
    public void run() {

        if (withinTimeWindow()) {
            //TODO opening and closing webcam can be too much overhead? (for short intervals)
            webcam.open();
            takeAndSavePicture();
            webcam.close();
        } else {
            userLogManager.sendUserLog("Not taking picture; we're not within time window");
        }

    }

    private boolean withinTimeWindow() {
        LocalTime now = LocalTime.now();
        return now.isAfter(Settings.CAPTURE_WINDOW_START_TIME) && now.isBefore(Settings.CAPTURE_WINDOW_END_TIME);
    }

    private void takeAndSavePicture() {
        try {

            File file = imagesManager.createFile(CAPTURE_FORMAT_EXTENSION);

            ImageIO.write(webcam.getImage(), CAPTURE_FORMAT_EXTENSION, file);
            imagesManager.add(file.getCanonicalPath());

            String log = "Webcam image taken. Written at: " + file.getCanonicalPath();
            LOGGER.info(log);
            userLogManager.sendUserLog("Webcam image taken and saved: " + file.getName());

        } catch (IOException e) {
            LOGGER.error("Woopsydaisy.", e);
        }
    }

}
