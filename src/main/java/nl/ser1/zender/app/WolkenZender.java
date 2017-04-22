package nl.ser1.zender.app;

import nl.ser1.zender.app.state.State;
import nl.ser1.zender.app.swingaling.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class WolkenZender {

    public static final int APPLICATION_WIDTH = 800;
    private static final Logger LOGGER = LoggerFactory.getLogger(WolkenZender.class);

    private static Application application = new Application();

    public static void main(String[] args) {
        setAppleProperties();

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                schwinggg();
                initialAppState();
            }
        });
    }

    private static void initialAppState() {
        application.getStateManager().toState(State.STOPPED);
        application.getUserLogManager().sendUserLog("Welcome! Pick an action from the menu");
    }

    private static void schwinggg() {
        final JFrame frame = createJFrame();

        JPanel mainPanel = createMainPanel();
        frame.add(mainPanel);

        JPanel headPanel = new JPanel();
        headPanel.setOpaque(true);
        headPanel.setBackground(Settings.COLOR_HEAD_PANEL);
        mainPanel.add(headPanel);

        headPanel.add(new HeadBlockPanel("Settings", new SettingsArea()));

        StatusTextArea statusTextArea = new StatusTextArea(application.getImagesManager());
        headPanel.add(new HeadBlockPanel("Status", statusTextArea));
        application.getStateManager().registerStateListener(statusTextArea);

        LogTextArea logPane = new LogTextArea();
        application.getUserLogManager().registerUserLogReceiver(logPane);

        mainPanel.add(logPane);

        frame.setJMenuBar(createJMenuBar());

        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        return panel;
    }

    private static void setAppleProperties() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Name");
    }

    private static JMenuBar createJMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new FileMenu(application);

        menuBar.add(fileMenu);


        return menuBar;


    }

    private static JFrame createJFrame() {
        JFrame frame = new JFrame("Wolkenzender");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLocationByPlatform(true);

        return frame;
    }


}



