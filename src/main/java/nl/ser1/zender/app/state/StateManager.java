package nl.ser1.zender.app.state;

import java.util.ArrayList;
import java.util.List;

// TODO I would like this manager to also manage state changes. (i.e. whether a state change is allowed, this logic is now hidden in FileMenu, of all places!)
// TODO So MenuItems can request the state manager to know whether their associated new state is allowed, they don't have to know current state.
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
