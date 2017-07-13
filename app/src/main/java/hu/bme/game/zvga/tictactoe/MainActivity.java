package hu.bme.game.zvga.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import hu.bme.game.zvga.tictactoe.controller.GameController;
import hu.bme.game.zvga.tictactoe.helpclass.GameProgress;
import hu.bme.game.zvga.tictactoe.model.GameModel;
import hu.bme.game.zvga.tictactoe.view.GameView;
import hu.bme.game.zvga.tictactoe.view.GameView.ITouchListener;

public class MainActivity
        extends Activity
        implements ITouchListener {
    private GameView gameView;
    private TextView textView;

    private GameModel gameModel;
    private GameController gameController;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean eraseLastGameEnabled = getIntent().getExtras().getBoolean("EraseLastGameEnabled");

        gameModel = null;
        gameController = null;

        try {
            FileInputStream out = openFileInput("GameModelBackup");
            FileInputStream out2 = openFileInput("GameControllerBackup");
            ObjectInputStream oos = new ObjectInputStream(out);
            ObjectInputStream oos2 = new ObjectInputStream(out2);
            try {
                gameModel = (GameModel) oos.readObject();
                gameController = (GameController) oos2.readObject();
            } catch(ClassNotFoundException e) {
                gameModel = null;
                gameController = null;
            } finally {
                if(out != null) {
                    out.close();
                }
                if(out2 != null) {
                    out2.close();
                }
                if(oos != null) {
                    oos.close();
                }
                if(oos2 != null) {
                    oos2.close();
                }
            }
        } catch(IOException e) {
            Log.d(this.getClass().toString(), e.getMessage());
        }

        boolean reconstructionNeeded = getIntent().getExtras().getBoolean("ReconstructionNeeded", false);

        if(reconstructionNeeded == true || gameModel == null || gameController == null) {
            getIntent().removeExtra("ReconstructionNeeded");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            String gridCountString = sp.getString("gridCount", "3");
            String winUnitCountString = sp.getString("winUnitCount", "3");
            int gridCount = Integer.parseInt(gridCountString);
            int winUnitCount = Integer.parseInt(winUnitCountString);
            if(gridCount < 3 || gridCount > 25) {
                gridCount = 3;
                Toast.makeText(getApplicationContext(), "Grid Count invalid. Default value used.", Toast.LENGTH_LONG).show();
            }
            if((winUnitCount > gridCount) || (winUnitCount < 3)) {
                winUnitCount = gridCount;
                Toast.makeText(getApplicationContext(), "Win Unit Count invalid. Value reset.", Toast.LENGTH_LONG).show();
            }
            gameModel = new GameModel(gridCount);
            gameController = new GameController(gameModel, winUnitCount);
            gameController.setGameModel(gameModel);
        }
        if(eraseLastGameEnabled == true) {
            getIntent().removeExtra("EraseLastGameEnabled");
            gameController.resetGame();
        }

        gameView = (GameView) findViewById(R.id.game_view);
        gameController.setGameView(gameView);
        gameController.setViewGameModel();
        textView = (TextView) findViewById(R.id.game_view_textview);

        if(gameController.getLastGameEvent() != GameProgress.NOEVENT) {
            updateNotificationText(gameController.getLastGameEvent());
        }

        gameView.setTouchListener(this);
        gameView.setFocusable(true);
        gameView.setFocusableInTouchMode(true);
        gameView.invalidate();
    }

    private void updateNotificationText(GameProgress gs) {
        switch(gs) {
            case TURNTOGGLE:
                switch(gameController.getTurn()) {
                    case O:
                        textView.setText(getString(R.string.o_goes_next_text));
                        break;
                    case X:
                        textView.setText(getString(R.string.x_goes_next_text));
                        break;
                    default:
                        break;
                }
                break;
            case DRAW:
                textView.setText(getString(R.string.draw_text));
                break;
            case VICTORY:
                switch(gameController.getTurn()) {
                    case O:
                        textView.setText(getString(R.string.victory_o_text));
                        break;
                    case X:
                        textView.setText(getString(R.string.victory_x_text));
                        break;
                    default:
                        break;
                }
                break;
            case RESTART:
                textView.setText(getString(R.string.welcome_text));
                break;
            case NOEVENT:
            default:
                break;
        }
    }

    public void launchToastNotification(GameProgress gs) {
        switch(gs) {
            case DRAW:
                Toast.makeText(getApplicationContext(), getString(R.string.draw_text), Toast.LENGTH_LONG).show();
                break;
            case VICTORY:
                switch(gameController.getTurn()) {
                    case O:
                        Toast.makeText(getApplicationContext(), getString(R.string.victory_o_text), Toast.LENGTH_LONG).show();
                        break;
                    case X:
                        Toast.makeText(getApplicationContext(), getString(R.string.victory_x_text), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
                break;
            case TURNTOGGLE:
            case RESTART:
            case NOEVENT:
            default:
                break;
        }
    }

    @Override
    public void onTouchSelected() {
        int squareWidth = ((Float) gameView.getSquareWidth()).intValue();
        int squareHeight = ((Float) gameView.getSquareHeight()).intValue();
        if(squareWidth != 0 && squareHeight != 0) {
            int i = (int) (gameView.getTouchX() / squareWidth);
            int j = (int) (gameView.getTouchY() / squareHeight);
            GameProgress gs = gameController.handleGameUserInteraction(i, j);
            updateNotificationText(gs);
            launchToastNotification(gs);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        FileOutputStream out = null;
        FileOutputStream out2 = null;
        try {
            out = openFileOutput("GameModelBackup", Context.MODE_PRIVATE);
            out2 = openFileOutput("GameControllerBackup", Context.MODE_PRIVATE);

            try {
                ObjectOutputStream oos = new ObjectOutputStream(out);
                ObjectOutputStream oos2 = new ObjectOutputStream(out2);
                oos.writeObject(gameModel);
                oos2.writeObject(gameController);
            } catch(IOException e) {
                Log.d(this.getClass().toString(), e.getMessage());
            }
        } catch(FileNotFoundException e) {
            Log.d(this.getClass().toString(), e.getMessage());
        } finally {
            try {
                if(out != null) {
                    out.close();
                }
                if(out2 != null) {
                    out2.close();
                }
            } catch(IOException e) {
                Log.d(this.getClass().toString(), e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.mainActivityBackItem:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
