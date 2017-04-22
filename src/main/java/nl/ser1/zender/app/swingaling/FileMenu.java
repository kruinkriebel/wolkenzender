package nl.ser1.zender.app.swingaling;

import nl.ser1.zender.app.Application;
import nl.ser1.zender.app.state.State;

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
            public boolean isVisible() {
                return application.state() == State.STOPPED && application.getImagesManager().isBufferFilled();
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                application.getImagesManager().newBuffer();
            }
        });
        return item;
    }

    private JMenuItem createStatusItem() {
        JMenuItem item = new JMenuItem() {
            @Override
            public String getText() {
                return "State: " + application.state();
            }
        };
        item.setFocusable(false);

        return item;
    }

    private JMenuItem createBufferStatusItem() {
        JMenuItem item = new JMenuItem() {
            @Override
            public String getText() {
                return "Buffer: " + (!application.getImagesManager().isBufferFilled() ? "EMPTY" : application.getImagesManager().size() + " PICS");
            }
        };
        item.setFocusable(false);

        return item;
    }

    private JMenuItem createMovieItem() {
        JMenuItem item = new JMenuItem("Create movie") {
            @Override
            public boolean isVisible() {
                return application.state() == State.STOPPED && application.getImagesManager().isBufferFilled();
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                application.startCreateMovieTask();
            }
        });
        return item;
    }

    private JMenuItem createStopTakingPicturesItem() {

        JMenuItem item = new JMenuItem("Stop taking pictures") {
            @Override
            public boolean isVisible() {
                return application.state() == State.TAKING_PICTURES;
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                application.stopTakePictureSchedule();
            }
        });
        return item;
    }

    private JMenuItem createStartTakingPicturesItem() {
        JMenuItem item = new JMenuItem("Start taking pictures") {
            @Override
            public boolean isVisible() {
                return application.state() == State.STOPPED && !application.getImagesManager().isBufferFilled();
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                application.startTakePictureSchedule();
            }


        });
        return item;
    }

    private JMenuItem createQuitItem() {
        JMenuItem item = new JMenuItem("Quit");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return item;
    }


}
