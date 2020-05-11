package com.mp3Player;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.mp3Player.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsLoader {

    private List<Song> songs = new ArrayList<>();
    private static SongsLoader instance = null;

    private SongsLoader() {
    }

    public static SongsLoader getInstance() {
        if (instance == null)
            instance = new SongsLoader();
        return instance;
    }


    public List<Song> getSongs() {
        return songs;
    }

    public void loadSongs(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    Song s = new Song(name, url, Integer.parseInt(duration));
                    songs.add(s);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }
}
