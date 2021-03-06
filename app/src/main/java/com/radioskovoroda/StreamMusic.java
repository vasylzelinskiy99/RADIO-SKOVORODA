package com.radioskovoroda;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import java.io.IOException;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;





public class StreamMusic extends AppCompatActivity {
    Button m_music;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    AudioManager audioManager;

    boolean prepared = false;
    boolean started = false;
    String stream = "http://stream.radioskovoroda.com:8060/skovoroda_music";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.stream_music);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();
        m_music = (Button) findViewById(R.id.m_music);
        m_music.setEnabled(false);
        m_music.setText("LOADING");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        new PlayerTask().execute(stream);

        m_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (started) {
                    started = false;
                    mediaPlayer.pause();
                    m_music.setText("PLAY");

                } else {
                    started = true;
                    mediaPlayer.start();
                    m_music.setText("PAUSE");
                }

            }
        });
        Button btn = (Button) findViewById(R.id.b_switch2);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(StreamMusic.this, MainActivity.class));
            }
        });
    }


    class PlayerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            m_music.setEnabled(true);
            m_music.setText("PLAY");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (prepared) {
            mediaPlayer.release();
        }
    }

    public void initControls() {
        try {
            seekBar = (SeekBar) findViewById(R.id.seekBar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            seekBar.setMax(audioManager
                    .getStreamMaxVolume(audioManager.STREAM_MUSIC));
            seekBar.setProgress(audioManager
                    .getStreamVolume(audioManager.STREAM_MUSIC));
            seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
