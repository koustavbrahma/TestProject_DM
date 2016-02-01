package koustav.duelmasters.main.androidgameduelmasterscreens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import koustav.duelmasters.R;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 7/5/2015.
 */
public class MenuScreen extends Screen {
    Button buttonHost, buttonJoin;
    AlertDialog action;
    DialogInterface.OnClickListener actionListener;

    public MenuScreen(final AndroidGame game) {
        super(game);
        game.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.getRenderObj().pause();
                game.setContentView(R.layout.activity_menu);

                buttonHost = (Button)game.findViewById(R.id.host);
                buttonJoin = (Button)game.findViewById(R.id.join);
                buttonHost.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        game.setScreen(new HostScreen(game));
                    }
                });

                buttonJoin.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        game.setScreen(new ClientLoginScreen(game));
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(game);
                actionListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: game.finish();
                                break;
                            default:
                                break;
                        }
                    }
                };
                builder.setTitle("Choose an Option");
                String [] options = {"Accept"};
                builder.setItems(options, actionListener);
                builder.setNegativeButton("Cancel", null);
                action = builder.create();
            }
        });
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        game.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.getRenderObj().pause();
            }
        });
    }

    @Override
    public void present(float deltaTime, float totalTime) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {
        action.show();
    }
}
