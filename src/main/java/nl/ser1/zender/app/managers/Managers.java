package nl.ser1.zender.app.managers;

import nl.ser1.zender.app.images.ImagesManager;
import nl.ser1.zender.app.notification.NotificationManager;
import nl.ser1.zender.app.state.State;
import nl.ser1.zender.app.state.StateManager;
import nl.ser1.zender.app.userlog.UserLogManager;

// TODO make this an interface that delivers Manager implementations of Manager interfaces...
// TODO (If you like to DI some day, and/or write some proper unit tests with mocks...)
public class Managers {

    public static final UserLogManager USERLOG_MAN =  new UserLogManager();
    public static final StateManager STATE_MAN = new StateManager(State.STOPPED);
    public static final ImagesManager IMAGES_MAN = new ImagesManager();
    public static final NotificationManager NOTIFICATION_MAN = new NotificationManager();

}
