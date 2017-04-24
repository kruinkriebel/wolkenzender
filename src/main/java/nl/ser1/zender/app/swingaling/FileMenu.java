package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.Application;
import nl.ser1.zender.app.managers.Managers;
import nl.ser1.zender.app.state.*;
import nl.ser1.zender.app.state.Action;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Robbert on 22-04-17.
 */
public class FileMenu extends JMenu {

    Application application;

    public FileMenu(Application application) {
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
                return actionAllowed(Action.CLEAR_BUFFER) && Managers.imagesManager.isBufferFilled();
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                performAction(Action.CLEAR_BUFFER);
                Managers.imagesManager.clearBuffer();
            }
        });
        return item;
    }



    private JMenuItem createStatusItem() {
        JMenuItem item = new JMenuItem() {
            @Override
            public String getText() {
                return "State: " + Managers.stateManager.getState();
            }
        };
        item.setFocusable(false);

        return item;
    }

    private JMenuItem createBufferStatusItem() {
        JMenuItem item = new JMenuItem() {
            @Override
            public String getText() {
                return "Buffer: " + (!Managers.imagesManager.isBufferFilled() ? "EMPTY" : Managers.imagesManager.size() + " PICS");
            }
        };
        item.setFocusable(false);

        return item;
    }

    private JMenuItem createMovieItem() {
        JMenuItem item = new JMenuItem("Create movie") {
            @Override
            public boolean isEnabled() {
                return actionAllowed(Action.START_CREATE_MOVIE) && Managers.imagesManager.isBufferFilled();
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
                return (Managers.imagesManager.isBufferFilled() ? "Continue" : "Start") + " taking pictures";
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
        return Managers.stateManager.actionAllowed(action);
    }

    private void performAction(Action action) {
        Managers.stateManager.performAction(action);
    }


}
