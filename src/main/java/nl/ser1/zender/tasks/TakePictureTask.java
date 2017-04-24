package nl.ser1.zender.tasks;

import com.github.sarxos.webcam.Webcam;
import nl.ser1.zender.app.Settings;
import nl.ser1.zender.app.managers.Managers;
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

    private Webcam webcam;

    public TakePictureTask() {
        fetchWebcam();
        setResolution();
    }

    private void setResolution() {
        Dimension[] nonStandardResolutions = new Dimension[]{
                Settings.CAPTURE_RESOLUTION
        };

        webcam.setCustomViewSizes(nonStandardResolutions);
        webcam.setViewSize(Settings.CAPTURE_RESOLUTION);

    }

    private void fetchWebcam() {
        Webcam.getWebcams().forEach(w -> Managers.userLogManager.sendUserLog("Camera found: " + w.getName()));
        webcam = Webcam.getWebcams().stream().filter(w -> w.getName().contains(CAMERA_IDENTIFIER)).findFirst().orElse(Webcam.getDefault());
        Managers.userLogManager.sendUserLog("Using webcam: " + webcam.getName());
    }

    @Override
    public void run() {

        if (!Settings.USE_TIMEWINDOW || withinTimeWindow()) {
            //TODO opening and closing webcam can be too much overhead? (for short intervals)
            webcam.open();
            takeAndSavePicture();
            webcam.close();
        } else {
            Managers.userLogManager.sendUserLog("Not taking picture; we're not within time window");
        }

    }

    private boolean withinTimeWindow() {
        LocalTime now = LocalTime.now();
        return now.isAfter(Settings.CAPTURE_WINDOW_START_TIME) && now.isBefore(Settings.CAPTURE_WINDOW_END_TIME);
    }

    private void takeAndSavePicture() {
        try {

            File file = Managers.imagesManager.createFile(CAPTURE_FORMAT_EXTENSION);

            ImageIO.write(webcam.getImage(), CAPTURE_FORMAT_EXTENSION, file);
            Managers.imagesManager.add(file.getCanonicalPath());

            String log = "Webcam image taken. Written at: " + file.getCanonicalPath();
            LOGGER.info(log);
            Managers.userLogManager.sendUserLog("Webcam image taken and saved: " + file.getName());

        } catch (IOException e) {
            LOGGER.error("Woopsydaisy.", e);
        }
    }

}
