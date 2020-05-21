package spotifyPackage.Activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int position;
    private String songName = getIntent().getStringExtra("Song_Name");
    private ImageView songImage;
    private ImageView playButtonImage;
    private Button playButton;
    private Button backwardsButton;
    private Button forwardButton;
    private Button backToSearchButton;
    private boolean buttonState = false;
    private SeekBar seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(this);

        backwardsButton = findViewById(R.id.backwards_button);
        backwardsButton.setOnClickListener(this);

        forwardButton = findViewById(R.id.forward_button);
        forwardButton.setOnClickListener(this);

        seekbar = findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // This method will automatically
                // called when the user touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                // This method will automatically
                // called when the user
                // stops touching the SeekBar
            }
        });

        Utilities.playSong(mediaPlayer, songName);
        buttonState = true;
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
    public void onClick(View v) {
        if (v == playButton) {
            if (buttonState){
                playButtonImage.setImageResource(R.drawable.pausebutton);
                mediaPlayer.pause();
                position = mediaPlayer.getCurrentPosition();
            }
            else {
                playButtonImage.setImageResource(R.drawable.playbutton);
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
            onPause();
        }
    }

    public void seekUpdater()
    {
        if(mediaPlayer.isPlaying())
        {
            seekbar = findViewById(R.id.seekBar);
            seekbar.setProgress(mediaPlayer.getCurrentPosition());

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
}