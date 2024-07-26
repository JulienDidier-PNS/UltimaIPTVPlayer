package com.example.ultimateiptvplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ultimateiptvplayer.Channels.Channel;
import com.example.ultimateiptvplayer.Fragments.Categorie.CategorieFragment;
import com.example.ultimateiptvplayer.Fragments.Categorie.OnCategoriesListener;
import com.example.ultimateiptvplayer.Fragments.Channels.ChannelsFragment;
import com.example.ultimateiptvplayer.Fragments.Channels.OnChannelListener;
import com.example.ultimateiptvplayer.Playlist.PlaylistsManager;

public class NavigationActivity extends AppCompatActivity implements OnCategoriesListener, OnChannelListener {
    private PlaylistsManager playlistManager;
    private CategorieFragment categorieFragment;
    private ChannelsFragment channelsFragment;
    private PlayerFragment playerFragment;
    private String currentCategory;
    private Channel currentChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Create the playlist manager
        playlistManager = PlaylistsManager.getInstance(getApplicationContext());

        playlistManager.getCurrentPlaylist().updateChannels();

        this.categorieFragment = new CategorieFragment(playlistManager.getCurrentPlaylist(),this);
        getSupportFragmentManager().beginTransaction().replace(R.id.categories_fragment, categorieFragment).commit();
    }

    @Override
    public void onCategoriesClick(String categoryName) {
        channelsFragment = new ChannelsFragment(playlistManager.getCurrentPlaylist().getChannelsByCategoryName(categoryName),this);
        getSupportFragmentManager().beginTransaction().replace(R.id.channel_navigation_fragment, channelsFragment).commit();
        this.currentCategory = categoryName;
    }

    @Override
    public void onChannelClick(int position) {
        this.currentChannel = playlistManager.getCurrentPlaylist().getChannelsByCategoryName(currentCategory).get(position);
        playerFragment = PlayerFragment.getInstance(getApplicationContext());
        getSupportFragmentManager().beginTransaction().replace(R.id.player_fragment, playerFragment).commit();
        playerFragment.setCurrentChannelUrl(this.currentChannel.getUrl());
        if(this.playerFragment.playerReady()){playerFragment.playChannel();}
    }
}
