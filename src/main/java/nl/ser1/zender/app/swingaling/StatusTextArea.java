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

    private State latestState;
    private String images;
    private ImagesManager imagesManager;

    public StatusTextArea(ImagesManager imagesManager) {
        super();
        setFont(getFont().deriveFont(Font.BOLD));
        this.imagesManager = imagesManager;
        imagesManager.registerImageEventListener(this);
    }

    @Override
    public void stateChange(State state) {
        this.latestState = state;
        renderText();
    }

    private void renderText() {
        setText(latestState.name() + "\n" + "Images in buffer: " + imagesManager.size());
    }

    @Override
    public Color getBackground() {
        return latestState == State.TAKING_PICTURES ? new Color(50,200,50) : super.getBackground();
    }

    @Override
    public void newImageEvent(ImageEvent imageEvent) {
        renderText();
    }
}
