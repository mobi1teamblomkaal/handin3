package mob.assignment.mediaplayerassignment;

import java.io.File;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class FileBrowserActivity extends ListActivity {
    private File[] data;
    private File root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        root = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
//        root = new File("/storage/sdcard0/Music/"); // Christian's Nexus
        root = new File("/" + Environment.getExternalStorageDirectory().getPath() + "/Music/"); // Andi's LG-E400
        data = root.listFiles();

        setListAdapter(new FileAdapter(getBaseContext(), data));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (data[position].isDirectory()) {
            data = data[position].listFiles();
            setListAdapter(new FileAdapter(getBaseContext(), data));
        } else if (FileType.isMediaFile(data[position])) {
            Intent playMediaIntent = new Intent(getBaseContext(), MainActivity.class);
            playMediaIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            MainActivity.path = data[position].getAbsolutePath();
            //playMediaIntent.putExtra("path", data[position].getAbsolutePath());
            startActivity(playMediaIntent);
        } else {
            Toast.makeText(getBaseContext(), "The file type is not an audio file", Toast.LENGTH_SHORT).show();
        }
    }
}