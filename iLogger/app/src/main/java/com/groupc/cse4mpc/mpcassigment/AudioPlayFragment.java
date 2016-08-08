package com.groupc.cse4mpc.mpcassigment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.groupc.cse4mpc.mpcassigment.dao.AudioDataSource;
import com.groupc.cse4mpc.mpcassigment.dao.MyAudio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by junqi on 21/10/15.
 */
public class AudioPlayFragment extends Fragment {
    private boolean startPlayFlag = true;
    private AudioDataSource dataSource;
    private ListView lvAudioHistory;
    private TextView tvAudioInfo;
    private String playFile;
    private MediaPlayer mediaPlayer;

    public AudioPlayFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Connect Database
        dataSource = new AudioDataSource(this.getContext());
        dataSource.open();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_audioplay, container, false);
        final Button btnPlay = (Button)rootView.findViewById(R.id.btnAudioPlay);
        tvAudioInfo = (TextView)rootView.findViewById(R.id.tvAudioInfo);
        lvAudioHistory = (ListView) rootView.findViewById(R.id.lvAudioHistory);
        //Use the adapter to show the elements in a listView
        List<MyAudio> myAudios = dataSource.getAllAudios();
        String[] mFrom = new String[]{"id","time"};
        int[] mTo = new int[]{R.id.itemTitle1,R.id.itemTitle2};

        List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
        Map<String,Object> mMap = null;
        for(MyAudio t: myAudios) {
            mMap = new HashMap<String, Object>();
            mMap.put("data",t);
            mMap.put("id",t.getId());
            mMap.put("time",t.getTime());
            mList.add(mMap);
            Log.d("Description",t.getDescription());
        }

        //Create Adapter
        SimpleAdapter tAdapter = new SimpleAdapter(this.getContext(),mList,R.layout.listview_item,mFrom,mTo);
        lvAudioHistory.setAdapter(tAdapter);
        lvAudioHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
                MyAudio pAudio = (MyAudio) map.get("data");
                tvAudioInfo.setText(pAudio.getTime() + pAudio.getLocation() + pAudio.getDescription());
                playFile = pAudio.getFilepath();
            }
        });
        //send through bluetooth
        lvAudioHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
                MyAudio pAudio = (MyAudio) map.get("data");
                String photoInfo = pAudio.getTime() + pAudio.getLocation() + pAudio.getDescription();

                Uri outputFileUri = Uri.fromFile(new File(pAudio.getFilepath()));

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, photoInfo);
                shareIntent.putExtra(Intent.EXTRA_STREAM, outputFileUri);
                shareIntent.setType("audio/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share"));

                return false;
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(startPlayFlag);
                if (startPlayFlag) {
                    btnPlay.setText("Stop playing");
                } else {
                    btnPlay.setText("Start playing");
                }
                startPlayFlag = !startPlayFlag;
            }
        });
        return rootView;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(playFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
