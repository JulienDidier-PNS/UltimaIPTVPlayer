package com.example.ultimateiptvplayer.Fragments.Channels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ultimateiptvplayer.Entities.Channels.Channel;
import com.example.ultimateiptvplayer.R;

import java.util.ArrayList;

public class ChannelsFragment extends Fragment {

    private GridView channelGrid;
    private final ArrayList<Channel> channelsList;
    private final OnChannelListener onChannelListener;

    public ChannelsFragment(ArrayList<Channel> channels,OnChannelListener onChannelListener) {
        this.channelsList = channels;
        this.onChannelListener = onChannelListener;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channels, container, false);
        this.channelGrid = view.findViewById(R.id.channels_grid);

        // Build the Channels GridLayout
        setupGridView();

        this.channelGrid.setOnItemClickListener((parent, view1, position, id) -> {
            try {
                onChannelListener.onChannelClick(position);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        this.channelGrid.setOnItemLongClickListener((parent, view12, position, id) -> {onChannelListener.onChannelLongClick(position);return true;});

        return view;
    }

    private void setupGridView() {
        ChannelAdapter adapter = new ChannelAdapter(channelsList);
        this.channelGrid.setAdapter(adapter);
    }

    private class ChannelAdapter extends BaseAdapter {
        private final ArrayList<Channel> channels;

        public ChannelAdapter(ArrayList<Channel> channels) {
            this.channels = channels;
        }

        @Override
        public int getCount() {
            return channels.size();
        }

        @Override
        public Object getItem(int position) {
            return channels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.channel_item, parent, false);
            }

            ImageView channelLogo = convertView.findViewById(R.id.channel_logo);
            TextView channelName = convertView.findViewById(R.id.channel_name);

            Channel channel = channels.get(position);

            channelName.setText(channel.getChannelName());
            // Utilisez Glide pour charger l'image si nécessaire
            Glide.with(ChannelsFragment.this).load(channel.getTvgLogo()).into(channelLogo);

            return convertView;
        }
    }
}




