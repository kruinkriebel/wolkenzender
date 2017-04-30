package nl.ser1.zender.tasks;

import nl.ser1.zender.app.managers.Managers;
import nl.ser1.zender.app.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Robbert on 30-04-17.
 */
public class CheckStatusTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckStatusTask.class);

    private int lastHourOFHourlyRoundup;
    private int lastCheckBufferSize;
    private LocalTime lastBufferCheckTime;

    public CheckStatusTask() {
        lastHourOFHourlyRoundup = -1; // get a roundup immediatelly
        lastCheckBufferSize = Managers.IMAGES_MAN.size();
        lastBufferCheckTime = LocalTime.now();
    }

    @Override
    public void run() {

        Managers.USERLOG_MAN.sendUserLog("Checking state...");

        if (Managers.STATE_MAN.getState() == State.TAKING_PICTURES) {
            if (lastCheckBufferSize == Managers.IMAGES_MAN.size()) {
                notifyOfHangingState();
            } else {
                Managers.USERLOG_MAN.sendUserLog("State Ok! Buffer increased with " + (Managers.IMAGES_MAN.size() - lastCheckBufferSize) + " images since last check.");
            }

            int hour = LocalTime.now().getHour();
            if (hour != lastHourOFHourlyRoundup) {
                lastHourOFHourlyRoundup = hour;
                notifyWithHourlyRoundUp();
            }
        }

        lastCheckBufferSize = Managers.IMAGES_MAN.size();
        lastBufferCheckTime = LocalTime.now();

    }

    private void notifyWithHourlyRoundUp() {
        Managers.USERLOG_MAN.sendUserLog("Going to notify with hourly roundup");

        String subject = "WolkenZender.CheckStatusTask: uurlijk overzicht.";
        String body = String.format("De laatste keer dat ik keek, om %s, waren er %s foto's in de buffer.\n", lastBufferCheckTime, lastCheckBufferSize);
        body += String.format("Nu, om %s, zijn dat er %s.\n", LocalTime.now(), Managers.IMAGES_MAN.size());
        body += "\n";
        body += "Met vriendelijke groet, \n";
        body += "WolkenZender.CheckStatusTask\n";

        Managers.NOTIFICATION_MAN.sendEmail(subject, body, true);

    }

    private void notifyOfHangingState() {
        LOGGER.info("Going to notify 'hanging' state");
        Managers.USERLOG_MAN.sendUserLog("The application appears to be hanging! Going to notify.");

        String subject = "WolkenZender.CheckStatusTask: de boel hangt.";
        String body = String.format("De laatste keer dat ik keek, om %s, waren er %s foto's in de buffer.\n", lastBufferCheckTime, lastCheckBufferSize);
        body += String.format("Nu, om %s, zijn dat er %s.\n", LocalTime.now(), Managers.IMAGES_MAN.size());
        body += "\n";
        body += "Met vriendelijke groet, \n";
        body += "WolkenZender.CheckStatusTask\n";

        Managers.NOTIFICATION_MAN.sendEmail(subject, body, false);

    }
}
