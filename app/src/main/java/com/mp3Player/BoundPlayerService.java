package com.mp3Player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.mp3Player.model.Song;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BoundPlayerService extends Service {
    private static final String TAG = "MyBoundService";
    private final MediaPlayer mp = new MediaPlayer();
    List<Song> songs = SongsLoader.getInstance().getSongs();

    enum playmode {
        NORMAL,
        SUFFLE,
        REPEAT
    }

    private playmode currentMode = playmode.NORMAL;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        BoundPlayerService getService() {
            // Return this instance of MyBoundService so clients can call public methods
            return BoundPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        initMediaPlayer();
    }

    private boolean initialized = false;

    public void initMediaPlayer() {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    if (initialized) {
                        nextSong();
                        playSong();
                    } else {
                        initialized = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int nowPlaying = 0;
    private int seekLength = 0;

    public void playSong(int index) throws Exception {
        if (index != nowPlaying) {
            seekLength = 0;
        }
        nowPlaying = index;
        playSong();
    }

    public void playSong() {
        try {
            mp.reset();
            Uri path = Uri.parse(songs.get(nowPlaying).getUrl());
            mp.setDataSource(String.valueOf(path));
            mp.prepare();
            mp.seekTo(seekLength);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        mp.pause();
        seekLength = mp.getCurrentPosition();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void nextSong() {
        switch (currentMode) {
            case SUFFLE:
                int randomSong = 0;
                do {
                    randomSong = ThreadLocalRandom.current().nextInt(0, songs.size());
                } while (randomSong == nowPlaying);
                nowPlaying = randomSong;
                break;
            case NORMAL:
                nowPlaying = nowPlaying + 1;
                if (nowPlaying == songs.size()) {
                    nowPlaying = 0;
                }
        }
        seekLength = 0;
        if (mp.isPlaying()) {
            playSong();
        }
    }

    public void prevSong() {
        nowPlaying = nowPlaying - 1;
        if (nowPlaying < 0) {
            nowPlaying = songs.size() - 1;
        }
        seekLength = 0;
        if (mp.isPlaying()) {
            playSong();
        }
    }

    public boolean isPlaying() {
        return mp.isPlaying();
    }

    public void setSeekLength(int progress) {
        this.seekLength = progress;
        this.playSong();
    }

    public int getCurrentSeekLenght() {
        return mp.getCurrentPosition();
    }

    public String getCurrentSongTitle() {
        return songs.get(nowPlaying).getTitle();
    }

    public playmode switchPlayMode() {
        switch (currentMode) {
            case NORMAL:
                currentMode = playmode.SUFFLE;
                break;
            case SUFFLE:
                currentMode = playmode.REPEAT;
                break;
            default:
                currentMode = playmode.NORMAL;
                break;
        }
        return currentMode;
    }

    public int getCurrentSongDuration() {
        return songs.get(nowPlaying).getDuration();
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
    }
}
