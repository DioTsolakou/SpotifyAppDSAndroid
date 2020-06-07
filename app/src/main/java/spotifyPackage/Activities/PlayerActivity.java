package spotifyPackage.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private int position;
    private String artistName;
    private String songName;
    private String path;
    private TextView songTxt;
    private TextView albumTxt;
    private ImageView songImage;
    private Button playButton;
    private Button backwardsButton;
    private Button forwardButton;
    private Button backToSearchButton;
    private Button downloadButton;
    private boolean buttonState = false;
    private SeekBar seekbar;
    private TextView timer;
    private TextView duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        artistName = getIntent().getStringExtra("Artist_Name");
        songName = getIntent().getStringExtra("Song_Name");
        path = getIntent().getStringExtra("Path");

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(path + File.separator + artistName + "@" + songName + "_final.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync();

        songImage = findViewById(R.id.songImage);
        songImage.setImageResource(R.drawable.noimage);

        timer = findViewById(R.id.currentTime);

        duration = findViewById(R.id.totalDuration);
        duration.setText(Utilities.toTimer(mediaPlayer.getDuration()));

        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(this);

        backwardsButton = findViewById(R.id.backwards_button);
        backwardsButton.setOnClickListener(this);

        forwardButton = findViewById(R.id.forward_button);
        forwardButton.setOnClickListener(this);

        backToSearchButton = findViewById(R.id.back_to_search_button);
        backToSearchButton.setOnClickListener(this);

        downloadButton = findViewById(R.id.downloadbutton);
        downloadButton.setOnClickListener(this);

        songTxt = findViewById(R.id.SongName);
        String songTxtToBeShown = artistName + " - " + songName;
        songTxt.setText(songTxtToBeShown);

        albumTxt = findViewById(R.id.AlbumName);
        albumTxt.setText(""); // Album name needs to be pulled from ID3v2 tags

        seekbar = findViewById(R.id.seekBar);
        seekbar.setMax(mediaPlayer.getDuration()/1000);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (mediaPlayer != null && fromUser)
                {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                if (mediaPlayer != null)
                {
                    mediaPlayer.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if(mediaPlayer != null)
                {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        //check if given path is downloadPath
        //Utilities.playSong(mediaPlayer, artistName + "@" + songName, path.equals(Utilities.downloadPath.getPath()));
/*        try {
            mediaPlayer.setDataSource(path + File.separator + songName + "_final.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        buttonState = true;
        playButton.setBackgroundResource(R.drawable.pausebutton);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            if (isFinishing()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onClick(View v) {
        if (v == playButton) {
            if (!buttonState) {
                playButton.setBackgroundResource(R.drawable.pausebutton);
                mediaPlayer.pause();
                position = mediaPlayer.getCurrentPosition();
            }
            else {
                playButton.setBackgroundResource(R.drawable.playbutton);
                mediaPlayer.seekTo(position);
                mediaPlayer.start();
            }
            buttonState = !buttonState;
        }

        if (v == forwardButton)
        {
            if (mediaPlayer.getCurrentPosition() + 10000 <= mediaPlayer.getDuration())
            {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
            }
            else mediaPlayer.seekTo(mediaPlayer.getDuration());
        }

        if (v == backwardsButton)
        {
            if (mediaPlayer.getCurrentPosition() - 10000 >= 0)
            {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }
            else mediaPlayer.seekTo(0);
        }

        if (v == backToSearchButton)
        {
            Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
            startActivity(intent);
        }

        if (v == downloadButton) {
            Toast.makeText(PlayerActivity.this, Utilities.moveFromStreamingToDownload(songName), Toast.LENGTH_SHORT).show();
        }
    }

    public void seekUpdater()
    {
        if(mediaPlayer.isPlaying())
        {
            seekbar = findViewById(R.id.seekBar);
            seekbar.setProgress(mediaPlayer.getCurrentPosition()/1000);

            Runnable r = new Runnable()
            {
                @Override
                public void run() {
                    seekUpdater();
                }
            };
            new Handler().postDelayed(r,1000);
        }
    }

    public void timerUpdater()
    {
        if (mediaPlayer.isPlaying()) {
            timer = findViewById(R.id.currentTime);
            timer.setText(Utilities.toTimer(mediaPlayer.getCurrentPosition()));

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    timerUpdater();
                }
            };
        }
    }
}