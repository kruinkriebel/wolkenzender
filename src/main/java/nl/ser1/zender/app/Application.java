package nl.ser1.zender.app;

import nl.ser1.zender.app.images.ImagesManager;
import nl.ser1.zender.app.state.State;
import nl.ser1.zender.app.state.StateManager;
import nl.ser1.zender.app.userlog.UserLogManager;
import nl.ser1.zender.tasks.CreateMovieTask;
import nl.ser1.zender.tasks.TakePictureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static nl.ser1.zender.app.Settings.*;

/**
 * Created by Robbert on 17-04-17.
 */
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> takePictureSchedule;

    private StateManager stateManager;
    private UserLogManager userLogManager;
    private final ImagesManager imagesManager;


    public Application() {
        imagesManager = new ImagesManager();
        stateManager = new StateManager(State.STOPPED);
        userLogManager = new UserLogManager();
    }

    public void startCreateMovieTask() {
        stateManager.toState(State.CREATING_MOVIE);
        // TODO this is now just running in the same thread, no Runnable needed
        new CreateMovieTask(new Vector<>(imagesManager.getImages()), CAPTURE_WIDTH, CAPTURE_HEIGHT).run();
        stateManager.toState(State.STOPPED);
    }

    public void stopTakePictureSchedule() {
        LOGGER.info("End takePictureSchedule...");
        takePictureSchedule.cancel(false);
        LOGGER.info("End takePictureSchedule ok.");
        stateManager.toState(State.STOPPED);
    }

    public void startTakePictureSchedule() {

        LOGGER.info("Start takePictureSchedule...");

        takePictureSchedule = scheduler.scheduleAtFixedRate(
                new TakePictureTask(CAPTURE_WIDTH, CAPTURE_HEIGHT, userLogManager, imagesManager),
                0,
                CAPTURE_INTERVAL_SECONDS,
                TimeUnit.SECONDS);

        LOGGER.info("Start takePictureSchedule ok.");

        stateManager.toState(State.TAKING_PICTURES);

    }

    public State state() {
        return stateManager.getState();
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public UserLogManager getUserLogManager() {
        return userLogManager;
    }

    public ImagesManager getImagesManager() {
        return imagesManager;
    }

}
