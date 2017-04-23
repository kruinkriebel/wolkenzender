package nl.ser1.zender.app.images;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static nl.ser1.zender.app.Settings.DIRECTORY_OUTPUT_PICTURES;

// TODO the conceptually different "image buffer" (on disk) and "file-reference buffer" are starting to blur
/**
 * Created by Robbert on 22-04-17.
 */
public class ImagesManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagesManager.class);

    private List<ImageEventListener> imageEventListeners;
    private List<String> images;

    public ImagesManager() {
        imageEventListeners = new ArrayList<>();
        newBuffer();
    }

    public List<String> getImages() {
        return images;
    }

    public boolean isBufferFilled() {
        return !images.isEmpty();
    }

    public void newBuffer() {
        images = new ArrayList<>();
        imageEventListeners.forEach(l -> l.newImageEvent(ImageEvent.NEW_BUFFER_CREATED));
    }


    public int size() {
        return images.size();
    }

    public void add(String canonicalPath) {
        images.add(canonicalPath);
        imageEventListeners.forEach(l -> l.newImageEvent(ImageEvent.IMAGE_ADDED));
    }

    public void registerImageEventListener(ImageEventListener imageEventListener) {
        imageEventListeners.add(imageEventListener);
    }

    public File createFile(String formatExtension) {
        return new File(createFilename(formatExtension));
    }

    public void loadBufferFromOutputDirectory() {
        newBuffer();
        try {
            diskBufferFilesStream()
                    .forEach(path -> {
                        try {
                            images.add(path.toFile().getCanonicalPath());
                        } catch (IOException e) {
                            LOGGER.error(String.format("while loading image [%s] from output directory", path.getFileName()), e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("while loading images buffer from output directory", e);
        }
        imageEventListeners.forEach(l -> l.newImageEvent(ImageEvent.EXISTING_BUFFER_LOADED));
    }

    public void clearBuffer() {
        try {
            diskBufferFilesStream().forEach(path->path.toFile().delete());
            imageEventListeners.forEach(l -> l.newImageEvent(ImageEvent.BUFFER_CLEARED));
            newBuffer();
        } catch (IOException e) {
            LOGGER.error("While clearing images buffer", e);
        }

    }

    private DirectoryStream<Path> diskBufferFilesStream() throws IOException {
        return Files.newDirectoryStream(Paths.get(DIRECTORY_OUTPUT_PICTURES), path -> path.toFile().isFile());
    }

    private String createFilename(String formatExtension) {
        return String.format(DIRECTORY_OUTPUT_PICTURES + "/wolk-%s." + formatExtension.toLowerCase(), LocalDateTime.now());
    }


    public boolean bufferExistsOnDisk() {
        try {
            return diskBufferFilesStream().iterator().hasNext();
        } catch (IOException e) {
            LOGGER.error("While trying to find out whether there is an images buffer on disk", e);
        }
        return false;
    }
}
