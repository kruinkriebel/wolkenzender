package nl.ser1.zender.tasks;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import nl.ser1.zender.app.Settings;
import nl.ser1.zender.app.managers.Managers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
        Webcam.getWebcams().forEach(w -> Managers.USERLOG_MAN.sendUserLog("Camera found: " + w.getName()));
        webcam = Webcam.getWebcams().stream().filter(w -> w.getName().contains(CAMERA_IDENTIFIER)).findFirst().orElse(Webcam.getDefault());
        Managers.USERLOG_MAN.sendUserLog("Using webcam: " + webcam.getName());
    }

    @Override
    public void run() {

        if (!Settings.USE_TIMEWINDOW || withinTimeWindow()) {
            //TODO opening and closing webcam can be too much overhead? (for short intervals)
            try {
                if (!webcam.isOpen()) {
                    LOGGER.info("Open webcam");
                    webcam.open();
                }
                LOGGER.info("Taking picture");
                takeAndSavePicture();
            } catch (WebcamException we) {
                Managers.USERLOG_MAN.sendUserLog("Fans and shit! Doing hitty, hitty! Cannot take picture.");
                LOGGER.error("While opening webcam and taking/saving picture", we);
            } finally {
                try {
                    if (webcam.isOpen()) {
                        LOGGER.info("Close webcam");
                        webcam.close();
                    }
                } catch (WebcamException closingWE) {
                    Managers.USERLOG_MAN.sendUserLog("Fans and shit! Doing hitty, hitty! Cannot close webcam.");
                    LOGGER.error("While closing webcam", closingWE);
                }
            }
        } else {
            Managers.USERLOG_MAN.sendUserLog("Not taking picture; we're not within time window");
        }

    }

    private boolean withinTimeWindow() {
        LocalTime now = LocalTime.now();
        return now.isAfter(Settings.CAPTURE_WINDOW_START_TIME) && now.isBefore(Settings.CAPTURE_WINDOW_END_TIME);
    }

    private void takeAndSavePicture() {
        try {
            File file = Managers.IMAGES_MAN.createFile(CAPTURE_FORMAT_EXTENSION);

            BufferedImage image = webcam.getImage();
            tagWithDatetime(image);

            ImageIO.write(image, CAPTURE_FORMAT_EXTENSION, file);
            Managers.IMAGES_MAN.add(file.getCanonicalPath());

            String log = "Webcam image taken. Written at: " + file.getCanonicalPath();
            LOGGER.info(log);
            Managers.USERLOG_MAN.sendUserLog("Webcam image taken and saved: " + file.getName());

        } catch (IOException e) {
            LOGGER.error("Woopsydaisy.", e);
        }
    }

    private void tagWithDatetime(BufferedImage image) {

        Graphics graphics = image.createGraphics();
        graphics.setFont(Font.getFont(Font.MONOSPACED));

        String dateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());

        graphics.setColor(Color.BLUE);
        graphics.drawChars(dateTime.toCharArray(), 0, dateTime.length(), 10, 20);
        graphics.setColor(Color.WHITE);
        graphics.drawChars(dateTime.toCharArray(), 0, dateTime.length(), 11, 21);
    }

}
