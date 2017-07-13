package hu.bme.game.zvga.tictactoe.helpclass;

import java.io.Serializable;

public class IntPair
        implements Serializable {
    int x;
    int y;

    public IntPair() {

    }

    public IntPair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setParams(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(o.getClass() != IntPair.class) {
            return false;
        }
        IntPair p = (IntPair) o;
        if(this.x == p.x && this.y == p.y) {
            return true;
        } else {
            return false;
        }
    }
}