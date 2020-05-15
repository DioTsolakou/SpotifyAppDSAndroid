package spotifyPackage.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;


public class MainActivity extends AppCompatActivity {


    private ListView artistList;
    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String artistName = (String) artistList.getItemAtPosition(position);
            Toast.makeText(MainActivity.this, artistName + " was selected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        artistList = findViewById(R.id.listView);

        //test arraylist , will be replaced with artist's arraylist
        ArrayList<String> places = new ArrayList<String>();
        places.add("Buenos Aires");
        places.add("Córdoba");
        places.add("La Plata");
        places.add("Buenos Aires");
        places.add("Córdoba");
        places.add("La Plata");
        places.add("Buenos Aires");
        places.add("Córdoba");
        places.add("La Plata");
        places.add("Buenos Aires");
        places.add("Córdoba");


        Toast.makeText(MainActivity.this, Utilities.findArtistsAll().get(1), Toast.LENGTH_SHORT).show();

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places);
        artistList.setAdapter(myAdapter);
        artistList.setOnItemClickListener(listClick);
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }
}