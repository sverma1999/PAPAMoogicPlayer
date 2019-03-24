//Author-Shubham Verma
package com.cs2063project.papamoogicplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView myListViewForSongs;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myListViewForSongs = (ListView) findViewById(R.id.mySongListView);

        permission();
    }

    public void permission(){

        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();

    }


    public ArrayList<File> lookForSongs(File file){
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for(File singleFile: files){

            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.addAll(lookForSongs(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3") ||
                singleFile.getName().endsWith(".wav")){

                    arrayList.add(singleFile);
                }
            }

        }

        return arrayList;
    }


    void display(){
        final ArrayList<File>  mysongs = lookForSongs(Environment.getExternalStorageDirectory());
        items = new String[mysongs.size()];

        for(int i =0; i<mysongs.size(); i++ ){

            items[i] = mysongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
        }

        ArrayAdapter<String> myAdaptor = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item, items);
        myListViewForSongs.setAdapter(myAdaptor);


        myListViewForSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String songName = myListViewForSongs.getItemAtPosition(position).toString();

                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                .putExtra("songs", mysongs).putExtra("song Name", songName)
                .putExtra("pos", position));

            }
        });
    }

}
