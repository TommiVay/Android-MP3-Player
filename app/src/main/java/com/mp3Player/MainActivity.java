package com.mp3Player;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button playButton, nextButton, prevButton, playModeButton, muteButton, libraryButton;
    private TextView total, current, title;
    private BoundPlayerService player;
    private Intent mBoundServiceIntent;
    private SeekBar seekBar, volumeBar;
    private AudioManager audioManager;
    private SongsLoader loader;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BoundPlayerService.LocalBinder binder = (BoundPlayerService.LocalBinder) service;
            player = binder.getService();
            current.setText(formatTime(player.getCurrentSeekLenght()));
            total.setText(formatTime(player.getCurrentSongDuration()));
            title.setText(player.getCurrentSongTitle());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkUserPermission();
        loader = SongsLoader.getInstance();
        loader.loadSongs(this);
        mBoundServiceIntent = new Intent(this, BoundPlayerService.class);
        bindService(mBoundServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        setButtonListeners();
    }

    public void setButtonListeners() {
        current = (TextView) findViewById(R.id.current);
        total = (TextView) findViewById(R.id.total);
        title = (TextView) findViewById(R.id.title);
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!player.isPlaying()) {
                    player.playSong();
                    playButton.setBackgroundResource(R.drawable.pause_round);
                    seekBarProgress.run();
                } else {
                    player.pauseSong();
                    mHandler.removeCallbacks(seekBarProgress);
                    playButton.setBackgroundResource(R.drawable.play_round);
                }
            }
        });

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                player.nextSong();
                setSongInfo();
            }
        });

        prevButton = (Button) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.prevSong();
                setSongInfo();
            }
        });

        libraryButton = (Button) findViewById(R.id.libraryButton);
        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SongListActivity.class);
                startActivityForResult(myIntent, 52);
            }
        });

        playModeButton = (Button) findViewById(R.id.playmodeButton);
        playModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (player.switchPlayMode()) {
                    case SUFFLE:
                        playModeButton.setBackgroundResource(R.drawable.suffle_icon);
                        showToast("Suffle");
                        break;
                    case REPEAT:
                        playModeButton.setBackgroundResource(R.drawable.repeat_icon);
                        showToast("Repeat");
                        break;
                    default:
                        playModeButton.setBackgroundResource(R.drawable.normal_icon);
                        showToast("Play in order");
                        break;
                }
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.setSeekLength(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));


        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                    muteButton.setBackgroundResource(R.drawable.mute_icon);
                } else {
                    muteButton.setBackgroundResource(R.drawable.volume_icon);
                }
            }
        });

        muteButton = (Button) findViewById(R.id.muteButton);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    volumeBar.setProgress(audioManager
                            .getStreamVolume(AudioManager.STREAM_MUSIC));
                    muteButton.setBackgroundResource(R.drawable.mute_icon);
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager
                            .getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);
                    muteButton.setBackgroundResource(R.drawable.volume_icon);
                    volumeBar.setProgress(audioManager
                            .getStreamVolume(AudioManager.STREAM_MUSIC));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == 52) {
            try {
                player.playSong(intent.getIntExtra("index", 0));
                playButton.setBackgroundResource(R.drawable.pause_round);
                current.setText(formatTime(player.getCurrentSeekLenght()));
                total.setText(formatTime(player.getCurrentSongDuration()));
                title.setText(player.getCurrentSongTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Permission Granted");
                    loader.loadSongs(this);
                } else {
                    showToast("Permission Denied");
                    checkUserPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void setSongInfo() {
        current.setText(formatTime(player.getCurrentSeekLenght()));
        total.setText(formatTime(player.getCurrentSongDuration()));
        title.setText(player.getCurrentSongTitle());
    }

    private Handler mHandler = new Handler();
    private Runnable seekBarProgress = new Runnable() {
        @Override
        public void run() {
            seekBar.setMax(player.getCurrentSongDuration());
            seekBar.setProgress(player.getCurrentSeekLenght());
            setSongInfo();
            mHandler.postDelayed(this, 10);
        }
    };

    private String formatTime(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf.append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }
}
