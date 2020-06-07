package spotifyPackage.Activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
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
    private TextView genreTxt;
    private ImageView songImage;
    private Button playButton;
    private Button backwardsButton;
    private Button forwardButton;
    private Button backToSearchButton;
    private Button downloadButton;
    private boolean buttonState = false;
    private SeekBar seekbar;
    private TextView timerTextView;
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

        genreTxt = findViewById(R.id.genreTextView);

        songImage = findViewById(R.id.songImage);

        timerTextView = findViewById(R.id.currentTime);
        timerTextView.setTextColor(Color.WHITE);

        duration = findViewById(R.id.totalDuration);
        duration.setTextColor(Color.WHITE);

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
        songTxt.setTextColor(Color.WHITE);

        albumTxt = findViewById(R.id.AlbumName);


        try {
            Mp3File mp3File = new Mp3File(path + File.separator + artistName + "@" + songName + "_final.mp3");
            ID3v2 songTags = mp3File.getId3v2Tag();

            if (mp3File.hasId3v2Tag()) {
                Drawable image = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(songTags.getAlbumImage(), 0, songTags.getAlbumImage().length));
                songImage.setImageDrawable(image);
                albumTxt.setText(songTags.getAlbum());
                albumTxt.setTextColor(Color.WHITE);
                genreTxt.setText(songTags.getGenreDescription());
                genreTxt.setTextColor(Color.WHITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        seekbar = findViewById(R.id.seekBar);

        buttonState = true;
        playButton.setBackgroundResource(R.drawable.pausebutton);
    }

    @Override
    protected void onPause() {
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
        duration.setText(Utilities.toTimer(mediaPlayer.getDuration()));

        seekbar.setMax(mediaPlayer.getDuration() / 1000);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                    seekUpdater();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    playButton.setBackgroundResource(R.drawable.playbutton);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    //mediaPlayer.seekTo(seekBar.getProgress());
                    mediaPlayer.start();
                    playButton.setBackgroundResource(R.drawable.pausebutton);
                }
            }
        });

        seekUpdater();
        timerUpdater();
    }

    @Override
    public void onClick(View v) {
        if (v == playButton) {
            if (buttonState) {
                playButton.setBackgroundResource(R.drawable.playbutton);
                mediaPlayer.pause();
                position = mediaPlayer.getCurrentPosition();
            } else {
                playButton.setBackgroundResource(R.drawable.pausebutton);
                mediaPlayer.seekTo(position);
                mediaPlayer.start();
            }
            buttonState = !buttonState;
        }

        if (v == forwardButton) {
            if (mediaPlayer.getCurrentPosition() + 10000 <= mediaPlayer.getDuration()) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
            } else mediaPlayer.seekTo(mediaPlayer.getDuration());
        }

        if (v == backwardsButton) {
            if (mediaPlayer.getCurrentPosition() - 10000 >= 0) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            } else mediaPlayer.seekTo(0);
        }

        if (v == backToSearchButton) {
            Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
            startActivity(intent);
        }

        if (v == downloadButton) {
            Toast.makeText(PlayerActivity.this, Utilities.moveFromStreamingToDownload(artistName + "@" + songName), Toast.LENGTH_SHORT).show();
        }
    }

    public void seekUpdater() {
        if (mediaPlayer.isPlaying()) {
            seekbar = findViewById(R.id.seekBar);
            seekbar.setProgress(mediaPlayer.getCurrentPosition() / 1000);

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    seekUpdater();
                }
            };
            new Handler().postDelayed(r, 1000);
        }
    }

    public void timerUpdater() {
        if (mediaPlayer.isPlaying()) {
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null) {
                                timerTextView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        timerTextView.setText(Utilities.toTimer(mediaPlayer.getCurrentPosition()));
                                    }
                                });
                            } else {
                                timer.cancel();
                                timer.purge();
                            }
                        }
                    });
                }
            }, 0, 1000);
        }
    }
}