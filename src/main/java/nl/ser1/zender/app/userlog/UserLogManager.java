package nl.ser1.zender.app.userlog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbert on 22-04-17.
 */
public class UserLogManager {

    private List<UserLogReceiver> userLogReceivers = new ArrayList<>();

    public void registerUserLogReceiver(UserLogReceiver userLogReceiver) {
        userLogReceivers.add(userLogReceiver);
    }

    public void sendUserLog(String userLog) {
        userLogReceivers.forEach(ulr -> ulr.receive(createUserLogTimestamp() + " " + userLog + "\n"));
    }

    private String createUserLogTimestamp() {
        return "["+ DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(LocalDateTime.now())+"]";
    }






}
