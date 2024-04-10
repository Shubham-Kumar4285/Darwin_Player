package com.example.darwin_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorTreeAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    ArrayList<Songs> songs=new ArrayList<>();
    ArrayList<File> mysongs=new ArrayList<>();
    TextView textView;
    ArrayAdapter ad ;
    String item[];
    @SuppressLint("HandlerLeak")
    Handler objhandler= new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            textView = findViewById(R.id.textView3);

                textView.setText("");

        }
    };



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        textView = findViewById(R.id.textView3);
        listView = findViewById(R.id.listview);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(MainActivity.this, "The permission was granted successfully !", Toast.LENGTH_SHORT).show();
                textView.setText(" Loading Songs...");

                Executor executor=Executors.newFixedThreadPool(2);
//                Runnable obj = () -> {
//                    try {
//                        mysongs = fetchsongs(Environment.getExternalStorageDirectory());
//                        item = new String[mysongs.size()];
//                        listView = findViewById(R.id.listview);
//                        for(int i =0;i<mysongs.size();i++){
//                            item[i]=mysongs.get(i).getName().replace(".mp3","");
//                        }
//
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                };
//                Runnable obj2 = () -> {
//                    try {
//
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                        textView.setText(" No music found !");
//
//                    }
//                    objhandler.sendEmptyMessage(0);
//                };

                executor.execute(() -> {
                    try{
                    mysongs = fetchsongs(Environment.getExternalStorageDirectory());
                    if(!mysongs.isEmpty()){
                        item = new String[mysongs.size()];
                        for(int i =0;i<mysongs.size();i++){
                            item[i]=mysongs.get(i).getName().replace(".mp3","");
                        }
                    }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> {
                        if(item!=null && item.length>0){
                        ad= new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,item);
                        listView.setAdapter(ad);
                        textView.setVisibility(View.INVISIBLE);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Intent intent = new Intent(MainActivity.this,mplayer.class);
                            String  current_song = listView.getItemAtPosition(position).toString();
                            intent.putExtra("mysongs",mysongs);
                            intent.putExtra("current_song",current_song);
                            intent.putExtra("position",position);
                            startActivity(intent);

                        });
                        }else{
                            textView.setText(" No music found !");

                        }

                    });

                });




                addMusic(mysongs);
                if(!songs.isEmpty()){
                Log.d("length is not empty :", " working");}


//                Thread songSearch= new Thread(obj);
//                Thread songSearch2= new Thread(obj2);
//
//                songSearch.start();
//                try {
//                    songSearch.join();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                songSearch2.start();


            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity.this, "Unable to load Songs", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }

                })
                .check();

    }



    public ArrayList<File> fetchsongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] songs = file.listFiles();
        if (songs != null) {
            for (File myfile : songs) {
                if (!myfile.isHidden() && myfile.isDirectory()) {
                    arrayList.addAll(fetchsongs(myfile));
                } else {
                    if (myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")) {
                        arrayList.add(myfile);
                    }
                }
            }
        }
        return arrayList;
    }

    public boolean checkList(ArrayList<File> songs){
        return !songs.isEmpty();
    }

    void addMusic(ArrayList<File> files){
        Uri Collection;
      if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
          Collection=MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);

      }else{
          Collection=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
      }
      String selection =MediaStore.Audio.Media.IS_MUSIC+" !=0";
      String[] projections =new String[]{
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID,
      };

        try{
        @SuppressLint("Recycle") Cursor cursor =this.getContentResolver().query(Collection,projections,selection,null,MediaStore.Audio.Media.DATE_ADDED+ " DESC",null);
        if(cursor!=null){
            while (cursor.moveToNext()){
                    @SuppressLint("Range") String ID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    @SuppressLint("Range") String Title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    @SuppressLint("Range") String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    @SuppressLint("Range") long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    @SuppressLint("Range") long album_ID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    @SuppressLint("Range") Uri uri= Uri.parse(cursor.getString(cursor.getPosition()));
                    Songs tempSong=new Songs(ID,Title,album,artist,duration,album_ID,uri);
                    songs.add(tempSong);

                }
            }
        }catch (Exception e){
            Log.d("DB part1 - fetching", "addMusic: Error occurred");
            e.printStackTrace();
        }


    }



}