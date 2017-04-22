package nl.ser1.zender.app.images;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbert on 22-04-17.
 */
public class ImagesManager {

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
        // TODO clear out the files in old buffer on filesystem too?
        images = new ArrayList<>();
        imageEventListeners.forEach(l->l.newImageEvent(ImageEvent.NEW_BUFFER_CREATED));
    }


    public int size() {
        return images.size();
    }

    public void add(String canonicalPath) {
        images.add(canonicalPath);
        imageEventListeners.forEach(l->l.newImageEvent(ImageEvent.IMAGE_ADDED));
    }

    public void registerImageEventListener(ImageEventListener imageEventListener) {
        imageEventListeners.add(imageEventListener);
    }


}
