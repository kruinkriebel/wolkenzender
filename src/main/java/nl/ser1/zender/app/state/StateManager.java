package nl.ser1.zender.app.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbert on 22-04-17.
 */
public class StateManager {

    private State state;

    private List<StateListener> stateListeners;

    public StateManager(State initialState) {
        this.state=initialState;
        stateListeners = new ArrayList<>();
    }

    public void toState(State newState) {
        state=newState;
        stateListeners.forEach(sl -> sl.stateChange(newState));
    }

    public State getState() {
        return state;
    }

    public void registerStateListener(StateListener newStateListener) {
        stateListeners.add(newStateListener);
    }
}
