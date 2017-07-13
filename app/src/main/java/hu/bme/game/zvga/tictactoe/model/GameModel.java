package hu.bme.game.zvga.tictactoe.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.bme.game.zvga.tictactoe.helpclass.GridState;
import hu.bme.game.zvga.tictactoe.helpclass.IntPair;
import hu.bme.game.zvga.tictactoe.helpclass.Turn;

public class GameModel
        implements Serializable {
    private Map<IntPair, GridState> gridStates;

    private int gridCount;
    private int currentUnitCount;


    public GameModel() {
    }

    public GameModel(int gridCount) {
        gridStates = new HashMap<IntPair, GridState>();
        this.gridCount = gridCount;
        initializeGridStates();
    }

    public Set<IntPair> getIntPairs() {
        return gridStates.keySet();
    }

    public int getGridCount() {
        return gridCount;
    }

    public int getCurrentUnitCount() {
        return currentUnitCount;
    }

    public GridState getGridState(int i, int j) {
        return gridStates.get(new IntPair(i, j));
    }

    public GridState getGridState(IntPair p) {
        return gridStates.get(p);
    }

    public void initializeGridStates() {
        if(!gridStates.isEmpty()) {
            gridStates.clear();
        }
        for(int i = 0; i < gridCount; i++) {
            for(int j = 0; j < gridCount; j++) {
                gridStates.put(new IntPair(i, j), GridState.EMPTY);
            }
        }
        currentUnitCount = 0;
    }

    public void resetGrid() {
        List<IntPair> tempList = new ArrayList<IntPair>();
        for(IntPair pair : gridStates.keySet()) {
            tempList.add(pair);
        }
        for(IntPair pair : tempList) {
            gridStates.remove(pair);
            gridStates.put(pair, GridState.EMPTY);
        }
        currentUnitCount = 0;
    }

    public void incrementUnitCount() {
        currentUnitCount = currentUnitCount + 1;
    }

    public void replaceGridState(Turn turn, int i, int j) {
        IntPair pcheck = new IntPair(i, j);
        incrementUnitCount();
        gridStates.remove(pcheck);
        if(turn == Turn.X) {
            gridStates.put(pcheck, GridState.X);
        } else if(turn == Turn.O) {
            gridStates.put(pcheck, GridState.O);
        }
    }

    public boolean checkForEmpty(int i, int j) {
        IntPair pcheck = new IntPair(i, j);
        if(gridStates.get(pcheck) == GridState.EMPTY) {
            return true;
        } else {
            return false;
        }
    }
}
