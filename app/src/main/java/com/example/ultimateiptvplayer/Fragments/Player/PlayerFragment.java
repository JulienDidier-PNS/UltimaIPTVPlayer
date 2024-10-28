package com.example.ultimateiptvplayer.Fragments.Player;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;

import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.ultimateiptvplayer.Activities.OnFullScreenListener;
import com.example.ultimateiptvplayer.R;

public class PlayerFragment extends Fragment {
    public static PlayerFragment instance;
    public static PlayerFragment getInstance(Context context, OnFullScreenListener fcListener) {
        if (instance == null) {instance = new PlayerFragment(context,fcListener);}
        return instance;
    }
    private final OnFullScreenListener fcListener;
    private final Context context;
    private PlayerView playerView;
    private ExoPlayer player;
    private String currentChannelUrl;
    private boolean inFullScreen = false;
    private AmbilightManager ambilightManager;

    public PlayerFragment(Context context,OnFullScreenListener fcListener) {
        this.context = context;
        this.fcListener = fcListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_layout, container, false);
    }

    private Handler handler;
    private Runnable hideRunnable;
    private static final long DISPLAY_DURATION_MS = 3000; // Durée pendant laquelle le logo et l'overlay doivent rester visibles (en millisecondes)

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerView = view.findViewById(R.id.player_view);
        initializePlayer();

        ImageView focusLogo = view.findViewById(R.id.focus_logo);
        View grayOverlay = view.findViewById(R.id.gray_overlay);

        // Initialisation du Handler et du Runnable
        handler = new Handler();
        hideRunnable = () -> {
            focusLogo.setVisibility(View.GONE);
            grayOverlay.setVisibility(View.GONE);
        };

        this.playerView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Afficher le logo
                focusLogo.setVisibility(View.VISIBLE);
                grayOverlay.setVisibility(View.VISIBLE);

                // Annuler toute tâche de masquage programmée
                handler.removeCallbacks(hideRunnable);

                // Planifier la disparition des éléments après un délai
                handler.postDelayed(hideRunnable, DISPLAY_DURATION_MS);
            } else {
                // Retirer le logo
                focusLogo.setVisibility(View.GONE);
                grayOverlay.setVisibility(View.GONE);

                // Annuler toute tâche de masquage programmée (au cas où)
                handler.removeCallbacks(hideRunnable);
            }
        });

        this.playerView.setOnClickListener(v -> {
            System.out.println("Click on player the view");
            System.out.println("Currently in fullscreen ? " + fcListener.playerInFullScreen());
            //if not in full screen, set it in full screen
            //otherwise, set it back to normal
            fcListener.setFullScreen(!fcListener.playerInFullScreen());
        });
    }



    public void setCurrentChannelUrl(String currentChannelUrl){
        this.currentChannelUrl = currentChannelUrl;
    }

    public void playChannel(){
        Uri uri = Uri.parse(this.currentChannelUrl);
        MediaItem mediaItem = MediaItem.fromUri(uri);

        player.setMediaItem(mediaItem);
        player.prepare();
        try{
            player.play();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this.context).build();
        playerView.setPlayer(player);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public boolean playerReady(){
        return player != null && this.playerView != null;
    }
}
