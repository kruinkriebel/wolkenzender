package nl.ser1.zender.app.state;

/**
 * Created by Robbert on 22-04-17.
 */
public enum State {

     TAKING_PICTURES {
          @Override
          public boolean actionAllowed(Action action) {
               switch (action) {
                    case STOP_TAKING_PICTURES:
                         return true;
                    default:
                         return false;
               }
          }

          @Override
          public State performAction(Action action) {
               switch (action) {
                    case STOP_TAKING_PICTURES:
                         return State.STOPPED;
                    default:
                         return this;
               }
          }


     },
     STOPPED {
          @Override
          public boolean actionAllowed(Action action) {
               switch (action) {
                    case START_CREATE_MOVIE:
                    case START_TAKING_PICTURES:
                    case CLEAR_BUFFER:
                         return true;
                    default:
                         return false;

               }
          }

          @Override
          public State performAction(Action action) {
               switch (action) {
                    case START_CREATE_MOVIE:
                         return CREATING_MOVIE;
                    case START_TAKING_PICTURES:
                         return TAKING_PICTURES;
                    case CLEAR_BUFFER:
                    default:
                         return this;
               }
          }
     },
     CREATING_MOVIE {
          @Override
          public boolean actionAllowed(Action action) {
               switch (action) {
                    case STOP_CREATING_MOVIE:
                         return true;
                    default:
                         return false;
               }
          }

          @Override
          public State performAction(Action action) {
               switch (action) {
                    case STOP_CREATING_MOVIE:
                         return STOPPED;
                    default:
                    return this;
               }
          }
     };

     public abstract boolean actionAllowed(Action action);
     public abstract State performAction(Action action);

}
