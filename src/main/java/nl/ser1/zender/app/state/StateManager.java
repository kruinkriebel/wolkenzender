package nl.ser1.zender.app.state;

import nl.ser1.zender.app.managers.Managers;

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

    public boolean actionAllowed(Action action) {
        return state.actionAllowed(action);
    }

    public State performAction(Action action) {
        state = state.performAction(action);
        stateListeners.forEach(l->l.stateChange(state));
        return state;
    }

    public void registerStateListener(StateListener newStateListener) {
        stateListeners.add(newStateListener);
    }

    public State getState() {
        return state;
    }
}
