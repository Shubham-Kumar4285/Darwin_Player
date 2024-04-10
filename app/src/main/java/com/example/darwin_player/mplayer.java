package com.example.darwin_player;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class mplayer extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateseek.interrupt();
    }

    private TextView textView;
    private ImageView pause,next,previous;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Thread updateseek;
    boolean flag=true;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mplayer);
        textView = findViewById(R.id.textView);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs= (ArrayList) bundle.getParcelableArrayList("mysongs");
        String textcontent = intent.getStringExtra("current_song");
        final int[] pos = {intent.getIntExtra("position", 0)};
        textView.setSelected(true);
        textView.setText(textcontent);
        Uri uri = Uri.parse(songs.get(pos[0]).toString());
        mediaPlayer = MediaPlayer.create(this, uri);

        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        updateseek = new Thread(){
            @Override
            public void run() {
                super.run();
                int position=0;
                    try {
                        while (position<mediaPlayer.getDuration() && flag){
                            position = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(position);
                            sleep(800);
                        }
                        if(position==mediaPlayer.getDuration() && flag){
                            seekBar.setProgress(0);
                            next.callOnClick();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

        };
        updateseek.start();

        pause.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                pause.setImageDrawable(getDrawable(R.drawable.play));
            }
            else{
                mediaPlayer.start();
                pause.setImageDrawable(getDrawable(R.drawable.pause));
            }
        }});
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setflag(false);
                mediaPlayer.stop();
                mediaPlayer.release();
                if(pos[0] !=songs.size()-1){
                    pos[0] = pos[0] +1;

                }else{
                    pos[0]=0;
                }
                Uri uri = Uri.parse(songs.get(pos[0]).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                seekBar.setMax(mediaPlayer.getDuration());
                textView.setText(songs.get(pos[0]).getName().toString());
                resetPos(seekBar);
                mediaPlayer.start();

                setflag(true);


                pause.setImageDrawable(getDrawable(R.drawable.pause));
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setflag(false);
                mediaPlayer.stop();
                mediaPlayer.release();
                if(pos[0] !=0){
                    pos[0] = pos[0] -1;

                }else{
                    pos[0]=songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(pos[0]).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                seekBar.setMax(mediaPlayer.getDuration());
                textView.setText(songs.get(pos[0]).getName().toString());
                resetPos(seekBar);
                mediaPlayer.start();

                setflag(true);
                pause.setImageDrawable(getDrawable(R.drawable.pause));
            }
        });


    }
    public void resetPos(SeekBar seekBar){
        seekBar.setProgress(0);
    }
    public void setflag(boolean value){
        flag= value;
    }
}