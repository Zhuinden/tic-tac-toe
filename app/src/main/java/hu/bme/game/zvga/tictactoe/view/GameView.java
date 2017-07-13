package hu.bme.game.zvga.tictactoe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import hu.bme.game.zvga.tictactoe.R;
import hu.bme.game.zvga.tictactoe.helpclass.GridState;
import hu.bme.game.zvga.tictactoe.helpclass.IntPair;
import hu.bme.game.zvga.tictactoe.model.GameModel;


public class GameView
        extends View {
    public interface ITouchListener {
        abstract void onTouchSelected();
    }


    private Drawable mDrawableBg;
    private int touchX;
    private int touchY;
    private ITouchListener touchListener = null;
    private GameModel gm;

    private int squareWidth;
    private int squareHeight;

    private Paint blackPaint;
    private Paint mLinePaint;

    private boolean gridCountChanged;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        requestFocus();
        mDrawableBg = getResources().getDrawable(R.drawable.woodtexturepattern);
        setBackgroundDrawable(mDrawableBg);

        squareWidth = 0;
        squareHeight = 0;

        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(4);
        blackPaint.setStyle(Style.STROKE);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStyle(Style.STROKE);

        gridCountChanged = true;

        gm = null;
    }

    public void setGameModel(GameModel gm) {
        this.gm = gm;
    }

    public void setTouchListener(ITouchListener aTouchListener) {
        touchListener = aTouchListener;
    }

    private void touchSelected() {
        if(touchListener != null) {
            touchListener.onTouchSelected();
        }
    }

    public int getTouchX() {
        return touchX;
    }

    public int getTouchY() {
        return touchY;
    }

    public float getSquareWidth() {
        return squareWidth;
    }

    public float getSquareHeight() {
        return squareHeight;
    }


    private void DrawX(Canvas c, int x, int y) {
        c.drawLine(x * squareWidth + (squareWidth / 10),
                y * squareHeight + (squareHeight / 10),
                (x + 1) * squareWidth - (squareWidth / 10),
                (y + 1) * squareHeight - (squareHeight / 10),
                blackPaint);
        c.drawLine((x + 1) * squareWidth - (squareWidth / 10),
                y * squareHeight + (squareHeight / 10),
                x * squareWidth + (squareWidth / 10),
                (y + 1) * squareHeight - (squareHeight / 10),
                blackPaint);
    }

    private void DrawO(Canvas c, int x, int y) {
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(4);
        blackPaint.setStyle(Style.STROKE);
        c.drawCircle(x * squareWidth + (squareWidth / 2), y * squareWidth + (squareHeight / 2), squareWidth / 3, blackPaint);
    }


    @Override
    protected void onDraw(Canvas canvas) //rajzolni a Canvas-re lehet, sajat View-ban
    {
        super.onDraw(canvas);
        if(gm != null) {
            int gridCount = gm.getGridCount();
            if(gridCountChanged == true) {
                squareWidth = getWidth() / gridCount;
                squareHeight = getHeight() / gridCount;
                gridCountChanged = false;
            }

            for(int i = 0; i <= gridCount; i++) {
                canvas.drawLine(i * squareWidth, 0, i * squareWidth, getHeight(), mLinePaint);
                canvas.drawLine(0, i * squareHeight, getWidth(), i * squareHeight, mLinePaint);
            }

            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), mLinePaint);
            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), mLinePaint);

            GridState gs;
            int i, j;
            for(IntPair pair : gm.getIntPairs()) {
                i = pair.getX();
                j = pair.getY();
                gs = gm.getGridState(i, j);
                switch(gs) {
                    case X:
                        DrawX(canvas, i, j);
                        break;
                    case O:
                        DrawO(canvas, i, j);
                        break;
                    case EMPTY:
                        break;
                    default:
                        break;
                }
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
        setMeasuredDimension(d, d);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN) {
            return true;
        } else if(action == MotionEvent.ACTION_UP) {
            touchX = (int) event.getX();
            touchY = (int) event.getY();
            touchSelected();
            return true;
        } else {
            return false;
        }
    }
}