package com.example.darwin_player;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class Songs {
     String ID;
    String Title;
    String album ;
    String artist ;
    Uri uri;
    long duration ;
   long album_ID ;

    public Songs(String ID, String title, String album, String artist, long duration, long album_ID,Uri uri) {
        this.ID = ID;
        Title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.album_ID = album_ID;
        this.uri=uri;
    }
}
