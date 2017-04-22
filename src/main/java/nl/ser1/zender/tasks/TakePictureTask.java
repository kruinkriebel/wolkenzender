package nl.ser1.zender.tasks;

import com.github.sarxos.webcam.Webcam;
import nl.ser1.zender.app.Application;
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
import java.util.List;

/**
 * Created by Robbert on 19-04-17.
 */
public class TakePictureTask implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(TakePictureTask.class);
    public static final String FORMAT_EXTENSION = "JPEG";

    private Webcam webcam;
    private ImagesManager imagesManager;
    private UserLogManager userLogManager;

    public TakePictureTask(int width, int height, UserLogManager userLogManager, ImagesManager imagesManager) {
        this.userLogManager=userLogManager;
        this.imagesManager=imagesManager;

        webcam = Webcam.getDefault();
        LOGGER.info("Default webcam found: " + webcam.getName());
        webcam.setViewSize(new Dimension(width, height));
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
