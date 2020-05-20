package spotifyPackage.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int position;
    private String songName = getIntent().getStringExtra("Song_Name");
    private ImageView songImage;
    private ImageView playButtonImage;
    private Button playButton;
    private boolean buttonState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playButton = (Button)findViewById(R.id.play_button);
        playButton.setOnClickListener(this);

        Utilities.playSong(mediaPlayer, songName);
        buttonState = true;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == playButton) {
            if (buttonState){
                //needs pause button image
                //playButtonImage.setImageResource(R.drawable.pausebutton);
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

    }




}
