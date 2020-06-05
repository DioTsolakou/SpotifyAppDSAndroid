package spotifyPackage.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import spotifyPackage.Consumer;
import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;

import static spotifyPackage.Utilities.Utilities.streamingPath;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText artistEditTxt;
    private EditText songEditTxt;
    private Button searchButton;
    private Button libraryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAppPermissions();

        songEditTxt = findViewById(R.id.songEditText);
        artistEditTxt = findViewById(R.id.artistEditText);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        libraryButton = findViewById(R.id.libraryButton);
        libraryButton.setOnClickListener(this);

        Utilities.createStorageDir(MainActivity.this, "streaming");
        Utilities.createStorageDir(MainActivity.this, "downloads");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onClick(View v) {
        if (v == searchButton) {
            String artist = artistEditTxt.getText().toString();
            String title = songEditTxt.getText().toString();
            if (title.length() > 0 && artist.length() > 0) {
                Utilities.createStorageDir(MainActivity.this, "streaming");
                Consumer c = new Consumer(artist + "," + title, streamingPath.getPath());
                if (c.run() == 0) {
                    Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
                    intent.putExtra("Artist_Name", artist);
                    intent.putExtra("Song_Name", title);
                    intent.putExtra("Path", streamingPath.getPath());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Error: Song not available!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (v == libraryButton) {
            Intent intent = new Intent(this, DownloadActivity.class);
            startActivity(intent);
        }
    }


    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 112); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
}