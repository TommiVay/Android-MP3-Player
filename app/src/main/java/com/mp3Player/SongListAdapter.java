package com.mp3Player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MusicViewHolder> {

    class MusicViewHolder extends RecyclerView.ViewHolder {
        private final TextView songTitle;

        private MusicViewHolder(View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.title);

        }
    }

    private final LayoutInflater mInflater;
    private List<String> mTitles;

    SongListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_song_item, parent, false);
        return new MusicViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        if (mTitles != null) {
            String current = mTitles.get(position);
            holder.songTitle.setText(current);
        } else {
            // Covers the case of data not being ready yet.
            holder.songTitle.setText("No music");
        }
    }

    void setTitles(List<String> titles) {
        mTitles = titles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mTitles != null)
            return mTitles.size();
        else return 0;
    }
}

