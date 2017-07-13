package hu.bme.game.zvga.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity
        extends Activity
        implements OnSharedPreferenceChangeListener {
    private Button startGameButton;
    private Button eraseLastGameButton;
    private Button settingsButton;

    private boolean eraseLastGameEnabled;
    private boolean reconstructionNeeded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        eraseLastGameEnabled = false;
        reconstructionNeeded = false;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        settings.registerOnSharedPreferenceChangeListener(this);

        startGameButton = (Button) findViewById(R.id.button_start_game);
        startGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(StartActivity.this, MainActivity.class);
                i.putExtra("EraseLastGameEnabled", eraseLastGameEnabled);
                i.putExtra("ReconstructionNeeded", reconstructionNeeded);
                eraseLastGameEnabled = false;
                reconstructionNeeded = false;
                startActivity(i);
            }

        });
        eraseLastGameButton = (Button) findViewById(R.id.button_erase_game);
        eraseLastGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(eraseLastGameEnabled == false) {
                    eraseLastGameEnabled = true;
                    Toast.makeText(StartActivity.this, getString(R.string.erase_game_enabled_toast), Toast.LENGTH_SHORT).show();
                } else {
                    eraseLastGameEnabled = false;
                    Toast.makeText(StartActivity.this, getString(R.string.erase_game_disabled_toast), Toast.LENGTH_SHORT).show();
                }
            }

        });
        settingsButton = (Button) findViewById(R.id.button_preferences);
        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(StartActivity.this, PreferencesActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        reconstructionNeeded = true;
        Toast.makeText(getApplicationContext(), getString(R.string.settings_applied_text), Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.startActivityBackItem:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
