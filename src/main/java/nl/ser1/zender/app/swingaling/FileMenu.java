package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.Tasks;
import nl.ser1.zender.app.managers.Managers;
import nl.ser1.zender.app.state.Action;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Robbert on 22-04-17.
 */
public class FileMenu extends JMenu {

    Tasks application;

    public FileMenu(Tasks application) {
        super("File");
        this.application = application;
        addMenuItems();

    }

    private void addMenuItems() {

        add(createStatusItem());
        addSeparator();
        add(createBufferStatusItem());
        add(createClearBufferItem());
        addSeparator();
        add(createStartTakingPicturesItem());
        add(createStopTakingPicturesItem());
        addSeparator();
        add(createMovieItem());
        addSeparator();
        add(createQuitItem());
    }

    private JMenuItem createClearBufferItem() {
        JMenuItem item = new JMenuItem("Clear buffer") {
            @Override
            public boolean isEnabled() {
                return actionAllowed(Action.CLEAR_BUFFER) && Managers.IMAGES_MAN.isBufferFilled();
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                performAction(Action.CLEAR_BUFFER);
                Managers.IMAGES_MAN.clearBuffer();
            }
        });
        return item;
    }



    private JMenuItem createStatusItem() {
        JMenuItem item = new JMenuItem() {
            @Override
            public String getText() {
                return "State: " + Managers.STATE_MAN.getState();
            }
        };
        item.setFocusable(false);

        return item;
    }

    private JMenuItem createBufferStatusItem() {
        JMenuItem item = new JMenuItem() {
            @Override
            public String getText() {
                return "Buffer: " + (!Managers.IMAGES_MAN.isBufferFilled() ? "EMPTY" : Managers.IMAGES_MAN.size() + " PICS");
            }
        };
        item.setFocusable(false);

        return item;
    }

    private JMenuItem createMovieItem() {
        JMenuItem item = new JMenuItem("Create movie") {
            @Override
            public boolean isEnabled() {
                return actionAllowed(Action.START_CREATE_MOVIE) && Managers.IMAGES_MAN.isBufferFilled();
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                performAction(Action.START_CREATE_MOVIE);
                application.startCreateMovieTask();
            }
        });
        return item;
    }

    private JMenuItem createStopTakingPicturesItem() {

        JMenuItem item = new JMenuItem("Stop taking pictures") {
            @Override
            public boolean isEnabled() {
                return actionAllowed(Action.STOP_TAKING_PICTURES);
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                performAction(Action.STOP_TAKING_PICTURES);
                application.stopTakePictureSchedule();
            }
        });
        return item;
    }

    private JMenuItem createStartTakingPicturesItem() {
        JMenuItem item = new JMenuItem() {
            @Override
            public boolean isEnabled() {
                return actionAllowed(Action.START_TAKING_PICTURES);
            }

            @Override
            public String getText() {
                return (Managers.IMAGES_MAN.isBufferFilled() ? "Continue" : "Start") + " taking pictures";
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                performAction(Action.START_TAKING_PICTURES);
                application.startTakePictureSchedule();
            }


        });
        return item;
    }

    private JMenuItem createQuitItem() {
        JMenuItem item = new JMenuItem("Quit");

        //TODO is quiting always allowed?
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return item;
    }

    private boolean actionAllowed(Action action) {
        return Managers.STATE_MAN.actionAllowed(action);
    }

    private void performAction(Action action) {
        Managers.STATE_MAN.performAction(action);
    }


}
