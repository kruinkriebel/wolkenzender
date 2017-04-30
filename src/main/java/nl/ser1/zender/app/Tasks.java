package nl.ser1.zender.app;

import nl.ser1.zender.app.managers.Managers;
import nl.ser1.zender.app.state.Action;
import nl.ser1.zender.app.state.State;
import nl.ser1.zender.tasks.CreateMovieTask;
import nl.ser1.zender.tasks.CheckStatusTask;
import nl.ser1.zender.tasks.TakePictureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static nl.ser1.zender.app.Settings.*;

/**
 * Created by Robbert on 17-04-17.
 */
public class Tasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tasks.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private ScheduledFuture<?> takePictureSchedule;
    private ScheduledFuture<?> checkStatusSchedule;

    public Tasks() {
        if (Settings.CHECK_STATE) {
            startCheckStatusTaskSchedule();
        }
    }

    public void startCreateMovieTask() {
        State state = Managers.STATE_MAN.performAction(Action.START_CREATE_MOVIE);
        if (state == State.CREATING_MOVIE) {
            new Thread(new CreateMovieTask()).start();
        }
    }

    public void stopTakePictureSchedule() {
        LOGGER.info("End takePictureSchedule...");
        takePictureSchedule.cancel(false);
        LOGGER.info("End takePictureSchedule ok.");
    }

    public void startTakePictureSchedule() {

        LOGGER.info("Start takePictureSchedule...");

        takePictureSchedule = scheduler.scheduleAtFixedRate(
                new TakePictureTask(),
                0,
                CAPTURE_INTERVAL_SECONDS,
                TimeUnit.SECONDS);

        LOGGER.info("Start takePictureSchedule ok.");

    }

    private void startCheckStatusTaskSchedule() {

        LOGGER.info("Start checkStatusSchedule...");

        checkStatusSchedule = scheduler.scheduleAtFixedRate(
                new CheckStatusTask(),
                Settings.CAPTURE_INTERVAL_SECONDS * CHECK_STATE_EVERY_X_SHOTS,
                Settings.CAPTURE_INTERVAL_SECONDS * CHECK_STATE_EVERY_X_SHOTS,
                TimeUnit.SECONDS);

        LOGGER.info("Start checkStatusSchedule ok.");

    }

}
