package hu.bme.game.zvga.tictactoe.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hu.bme.game.zvga.tictactoe.helpclass.GameProgress;
import hu.bme.game.zvga.tictactoe.helpclass.GridState;
import hu.bme.game.zvga.tictactoe.helpclass.IntPair;
import hu.bme.game.zvga.tictactoe.helpclass.Turn;
import hu.bme.game.zvga.tictactoe.model.GameModel;
import hu.bme.game.zvga.tictactoe.view.GameView;

public class GameController
        implements Serializable {
    private GameModel gameModel;
    private transient GameView gameView;

    private Turn turn;
    private int winUnitCount;

    private List<IntPair> victoryCheckList;

    private GameProgress lastGameEvent;

    private boolean victoryState;

    public GameController() {
    }

    public GameController(GameModel gameModel, int winUnitCount) {
        this.gameModel = gameModel;
        turn = Turn.X;
        victoryCheckList = new ArrayList<IntPair>();
        victoryState = false;
        this.winUnitCount = winUnitCount;
        lastGameEvent = GameProgress.NOEVENT;
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public void setViewGameModel() {
        gameView.setGameModel(gameModel);
    }

    public void toggleTurn() {
        if(turn == Turn.X) {
            turn = Turn.O;
        } else {
            turn = Turn.X;
        }
    }

    public Turn getTurn() {
        return turn;
    }

    public GameProgress getLastGameEvent() {
        return lastGameEvent;
    }

    private void modelResetGrid() {
        gameModel.resetGrid();
    }

    private void onGameEnd() {
        victoryState = true;
    }

    public void resetGame() {
        modelResetGrid();
        victoryState = false;
        lastGameEvent = GameProgress.NOEVENT;
    }

    private boolean checkForDraw() {
        if(gameModel.getCurrentUnitCount() == ((gameModel.getGridCount()) * (gameModel.getGridCount()))) {
            onGameEnd();
            return true;
        }
        return false;
    }

    private boolean victoryCheckStates(List<IntPair> intpairs) {
        boolean victoryX = true;
        boolean victoryO = true;
        GridState gridState;
        for(IntPair pair : intpairs) {
            gridState = gameModel.getGridState(pair);
            if(gridState != GridState.X) {
                victoryX = false;
            }
            if(gridState != GridState.O) {
                victoryO = false;
            }
            if(!victoryX && !victoryO) {
                break;
            }
        }
        return victoryX || victoryO;
    }

    private boolean victoryCheckHorizontal(int x, int y) {
        boolean victory = false;
        boolean successful;
        for(int j = 0; j < winUnitCount; j++) {
            successful = true;
            if(!victoryCheckList.isEmpty()) {
                victoryCheckList.clear();
            }
            for(int i = x - (winUnitCount - 1) + j; i <= x + j; i++) {
                if(i < 0 || i >= gameModel.getGridCount()) {
                    successful = false;
                    break;
                }
                victoryCheckList.add(new IntPair(i, y));
            }
            if(successful) {
                victory = victoryCheckStates(victoryCheckList);
                if(victory) {
                    onGameEnd();
                    break;
                }
            }
        }
        return victory;
    }

    private boolean victoryCheckVertical(int x, int y) {
        boolean victory = false;
        boolean successful;
        for(int j = 0; j < winUnitCount; j++) {
            successful = true;
            if(!victoryCheckList.isEmpty()) {
                victoryCheckList.clear();
            }
            for(int i = y - (winUnitCount - 1) + j; i <= y + j; i++) {
                if(i < 0 || i >= gameModel.getGridCount()) {
                    successful = false;
                    break;
                }
                victoryCheckList.add(new IntPair(x, i));
            }
            if(successful) {
                victory = victoryCheckStates(victoryCheckList);
                if(victory) {
                    onGameEnd();
                    break;
                }
            }
        }
        return victory;
    }

    private boolean victoryCheckDiagonalLeft(int x, int y) {
        boolean victory = false;
        boolean successful;
        for(int j = 0; j < winUnitCount; j++) {
            successful = true;
            if(!victoryCheckList.isEmpty()) {
                victoryCheckList.clear();
            }
            for(int i = x - (winUnitCount - 1) + j, k = y - (winUnitCount - 1) + j; i <= x + j; i++, k++) {
                if(i < 0 || i >= gameModel.getGridCount() || k < 0 || k >= gameModel.getGridCount()) {
                    successful = false;
                    break;
                }
                victoryCheckList.add(new IntPair(i, k));
            }
            if(successful) {
                victory = victoryCheckStates(victoryCheckList);
                if(victory) {
                    onGameEnd();
                    break;
                }
            }
        }
        return victory;
    }

    private boolean victoryCheckDiagonalRight(int x, int y) {
        boolean victory = false;
        boolean successful;
        for(int j = 0; j < winUnitCount; j++) {
            successful = true;
            if(!victoryCheckList.isEmpty()) {
                victoryCheckList.clear();
            }
            for(int i = x + (winUnitCount - 1) - j, k = y - (winUnitCount - 1) + j; i >= x - j; i--, k++) {
                if(i < 0 || i >= gameModel.getGridCount() || k < 0 || k >= gameModel.getGridCount()) {
                    successful = false;
                    break;
                }
                victoryCheckList.add(new IntPair(i, k));
            }
            if(successful) {
                victory = victoryCheckStates(victoryCheckList);
                if(victory) {
                    onGameEnd();
                    break;
                }
            }
        }
        return victory;
    }

    public boolean checkForVictory(int x, int y) {
        boolean victory;
        victory = victoryCheckHorizontal(x, y);
        if(victory) {
            return true;
        }
        victory = victoryCheckVertical(x, y);
        if(victory) {
            return true;
        }
        victory = victoryCheckDiagonalLeft(x, y);
        if(victory) {
            return true;
        }
        victory = victoryCheckDiagonalRight(x, y);
        return victory;
    }

    public GameProgress handleGameUserInteraction(int i, int j) {
        GameProgress gameProgress = GameProgress.NOEVENT;
        if(!victoryState) {
            if(gameModel.checkForEmpty(i, j)) {
                gameModel.replaceGridState(turn, i, j);
                if(!checkForVictory(i, j)) {
                    if(!checkForDraw()) {
                        toggleTurn();
                        gameProgress = GameProgress.TURNTOGGLE;
                    } else {
                        gameProgress = GameProgress.DRAW;
                    }
                } else {
                    gameProgress = GameProgress.VICTORY;
                }
            }
        } else {
            gameProgress = GameProgress.RESTART;
            gameModel.resetGrid();
            victoryState = false;
        }
        gameView.invalidate();
        if(gameProgress != GameProgress.NOEVENT) {
            lastGameEvent = gameProgress;
        }
        return gameProgress;
    }
}