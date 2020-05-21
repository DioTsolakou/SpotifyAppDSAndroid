package spotifyPackage.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import spotifyPackage.R;

public class MainActivity extends AppCompatActivity {

    private Button searchButton;
    private EditText songName;
    private EditText artistName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songName = findViewById(R.id.songEditText);
        artistName = findViewById(R.id.artistEditText);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }


}