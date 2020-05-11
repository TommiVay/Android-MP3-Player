package com.mp3Player;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.mp3Player.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SongListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        adapter = new SongListAdapter(this);
        List<String> titles = new ArrayList<>();
        SongsLoader songsLoader = SongsLoader.getInstance();

        for (Song s : songsLoader.getSongs()) {
            titles.add(s.getTitle());
        }
        adapter.setTitles(titles);
        View view = this.findViewById(android.R.id.content);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    Intent intent = new Intent();
                    intent.putExtra("index", position);
                    setResult(52, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

}
