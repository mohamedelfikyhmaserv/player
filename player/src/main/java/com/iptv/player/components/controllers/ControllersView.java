package com.iptv.player.components.controllers;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.iptv.player.R;
import com.iptv.player.eventTypes.UserInteraction;
import com.iptv.player.eventTypes.UserInteractionEvent;
import com.iptv.player.components.UIView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class ControllersView extends UIView implements View.OnClickListener {

    public static final String LOCK_TAG = "controllersComponent";

    private MaterialButton minus10;
    private MaterialButton plus10;
    private MaterialButton playPause;
    private boolean isPlaying;

    private TextView currentTime;
    private TextView mediaTime;
    private SeekBar seekBar;

    private final Handler handler = new Handler();
    private final Runnable hideRunnable = this::hide;

    public ControllersView() {
        super(R.layout.component_controllers, false);
    }

    @Override
    public void setParent(@NonNull ViewGroup parent) {
        super.setParent(parent);

        minus10 = view.findViewById(R.id.minus_10);
        minus10.setOnClickListener(this);
        plus10 = view.findViewById(R.id.plus_10);
        plus10.setOnClickListener(this);
        playPause = view.findViewById(R.id.play_pause);
        playPause.setOnClickListener(this);

        currentTime = view.findViewById(R.id.current_time);
        mediaTime = view.findViewById(R.id.media_time);
        seekBar = view.findViewById(R.id.seek_bar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    startAutoHide();
                    userInteractionEvents.setValue(new UserInteraction(progress, UserInteractionEvent.TIME_CHANGED));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void show() {
        requestOnKeyLock(LOCK_TAG);
        if (!isShowing()) {
            playPause.requestFocus();
        }
        super.show();
        startAutoHide();
    }

    @Override
    public void hide() {
        super.hide();
        stopAutoHide();
        clearOnKeyLock(LOCK_TAG);
    }

    @Override
    public String getLockTag() {
        return LOCK_TAG;
    }

    public void startAutoHide() {
        handler.removeCallbacks(hideRunnable);
        handler.postDelayed(hideRunnable, 5000);
    }

    public void stopAutoHide() {
        handler.removeCallbacks(hideRunnable);
    }

    @Override
    public void onClick(View v) {
        startAutoHide();
        if (v.getId() == R.id.minus_10) {
            userInteractionEvents.setValue(new UserInteraction(10000, UserInteractionEvent.TIME_MINUS));
        } else if (v.getId() == R.id.plus_10) {
            userInteractionEvents.setValue(new UserInteraction(10000, UserInteractionEvent.TIME_PLUS));
        } else if (v.getId() == R.id.play_pause) {
            if (isPlaying) {
                userInteractionEvents.setValue(new UserInteraction(UserInteractionEvent.PAUSE));
            } else {
                userInteractionEvents.setValue(new UserInteraction(UserInteractionEvent.PLAY));
            }
        }
    }

    void play() {
        isPlaying = true;
        playPause.setIconResource(R.drawable.ic_pause_white);
    }

    void pause() {
        isPlaying = false;
        playPause.setIconResource(R.drawable.ic_play_arrow_white);
    }

    void stopped() {
        isPlaying = false;
        playPause.setIconResource(R.drawable.ic_play_arrow_white);
    }

    public void end() {
        isPlaying = false;
        playPause.setIconResource(R.drawable.ic_play_arrow_white);
    }

    void updateTime(long value) {
        seekBar.setProgress((int) value);
        String hms = String.format(Locale.getDefault(),
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(value),
            TimeUnit.MILLISECONDS.toMinutes(value) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(value) % TimeUnit.MINUTES.toSeconds(1));
        currentTime.setText(hms);
    }

    void updateLength(long lengthChanged) {
        seekBar.setMax((int) lengthChanged);
        String hms = String.format(Locale.getDefault(),
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(lengthChanged),
            TimeUnit.MILLISECONDS.toMinutes(lengthChanged) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(lengthChanged) % TimeUnit.MINUTES.toSeconds(1));
        mediaTime.setText(hms);
    }
}
