package com.example.ultimateiptvplayer.Fragments.Channels;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface OnChannelListener {
    void onChannelClick(int position) throws IOException, ExecutionException, InterruptedException;
    void onChannelLongClick(int position);
}
