package com.cs2063project.papamoogicplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button next_btn, previous_btn, pause_btn;
    TextView songTxtView;
    SeekBar songSeekBar;
    String sname;
    static MediaPlayer myMediaPlayer;
    int position;

    ArrayList<File> mySongs;
    Thread updateSeekBar;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        next_btn = (Button)findViewById(R.id.next);
        previous_btn = (Button)findViewById(R.id.previous);
        pause_btn = (Button)findViewById(R.id.pause);

        songTxtView = (TextView) findViewById(R.id.songTextView);
        songSeekBar = (SeekBar) findViewById(R.id.seekBar);

        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = myMediaPlayer.getDuration();
                int currentPosition = 0;

                while(currentPosition < totalDuration){

                    try {
                        sleep(500);
                        currentPosition = myMediaPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currentPosition);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
            }
        };


        if(myMediaPlayer != null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        sname = mySongs.get(position).getName().toString();

        String songName = intent.getStringExtra("song Name");

        songTxtView.setText(songName);
        songTxtView.setSelected(true);

        position = bundle.getInt("pos", 0);

        Uri u = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

        myMediaPlayer.start();
        songSeekBar.setMax(myMediaPlayer.getDuration());

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateSeekBar.start();
        songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);



        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

            pause_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    songSeekBar.setMax(myMediaPlayer.getDuration());

                    if(myMediaPlayer.isPlaying()){
                        pause_btn.setBackgroundResource(R.drawable.play_icon);
                        myMediaPlayer.pause();
                    }
                    else
                    {
                        pause_btn.setBackgroundResource(R.drawable.pause_icon);
                        myMediaPlayer.start();
                    }

                }
            });

            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myMediaPlayer.stop();
                    myMediaPlayer.release();
                    position = ((position+1)%mySongs.size());

                    Uri u = Uri.parse(mySongs.get(position).toString());

                    myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                    sname = mySongs.get(position).getName().toString();
                    songTxtView.setText(sname);

                    myMediaPlayer.start();

                }
            });

            previous_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myMediaPlayer.stop();
                    myMediaPlayer.release();

                    position = ((position-1)<0)?(mySongs.size()-1):(position-1);
                    Uri u = Uri.parse(mySongs.get(position).toString());
                    myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                    sname = mySongs.get(position).getName().toString();
                    songTxtView.setText(sname);

                    myMediaPlayer.start();



                }
            });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
