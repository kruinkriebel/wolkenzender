package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.images.ImageEvent;
import nl.ser1.zender.app.images.ImageEventListener;
import nl.ser1.zender.app.images.ImagesManager;
import nl.ser1.zender.app.state.State;
import nl.ser1.zender.app.state.StateListener;

import java.awt.*;

/**
 * Created by Robbert on 22-04-17.
 */
public class StatusTextArea extends HeadBlockTextArea implements StateListener, ImageEventListener {

    private String state, images;
    private ImagesManager imagesManager;

    public StatusTextArea(ImagesManager imagesManager) {
        super();
        setFont(getFont().deriveFont(Font.BOLD));
        this.imagesManager = imagesManager;
        imagesManager.registerImageEventListener(this);
    }

    @Override
    public void stateChange(State state) {
        this.state = state.name();
        renderText();
    }

    private void renderText() {
        setText(state + "\n" + "Images in buffer: " + imagesManager.size());
    }


    @Override
    public void newImageEvent(ImageEvent imageEvent) {
        renderText();
    }
}
