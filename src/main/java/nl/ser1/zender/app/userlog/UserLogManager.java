package nl.ser1.zender.app.userlog;

import nl.ser1.zender.scooped.moviemaker.JpegImagesToMovie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbert on 22-04-17.
 */
public class UserLogManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLogManager.class);

    private List<UserLogReceiver> userLogReceivers = new ArrayList<>();

    public void registerUserLogReceiver(UserLogReceiver userLogReceiver) {
        userLogReceivers.add(userLogReceiver);
    }

    public void sendUserLog(String userLog) {
        LOGGER.info("User log: " + userLog);
        userLogReceivers.forEach(ulr -> {
            String completeUserLog = createUserLogTimestamp() + " " + userLog + "\n";
            ulr.receive(userLog);
        });
    }

    private String createUserLogTimestamp() {
        return "["+ DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(LocalDateTime.now())+"]";
    }






}
