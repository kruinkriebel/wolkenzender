package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.images.ImageEvent;
import nl.ser1.zender.app.images.ImageEventListener;
import nl.ser1.zender.app.managers.Managers;
import nl.ser1.zender.app.state.State;
import nl.ser1.zender.app.state.StateListener;

import java.awt.*;

/**
 * Created by Robbert on 22-04-17.
 */
public class StatusTextArea extends HeadBlockTextArea implements ImageEventListener, StateListener {

    private String images;

    public StatusTextArea() {
        super();
        setFont(getFont().deriveFont(Font.BOLD));
        Managers.IMAGES_MAN.registerImageEventListener(this);
        Managers.STATE_MAN.registerStateListener(this);
    }

    private void renderText() {
        setText(Managers.STATE_MAN.getState().name() + "\n" + "Images in buffer: " + Managers.IMAGES_MAN.size());
    }

    @Override
    public Color getBackground() {
        State state = Managers.STATE_MAN.getState();
        if (state != null) {
            switch (state) {
                case TAKING_PICTURES:
                    return new Color(50, 200, 50);
                case CREATING_MOVIE:
                    return Color.orange;
            }
        }

        return super.getBackground();



    }

    @Override
    public void newImageEvent(ImageEvent imageEvent) {
        renderText();
    }

    @Override
    public void stateChange(State state) {
        renderText();
    }
}
