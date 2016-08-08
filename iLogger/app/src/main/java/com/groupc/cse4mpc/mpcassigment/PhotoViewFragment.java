package com.groupc.cse4mpc.mpcassigment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.groupc.cse4mpc.mpcassigment.dao.MyPhoto;
import com.groupc.cse4mpc.mpcassigment.dao.PhotoDataSource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by junqi on 21/10/15.
 */
public class PhotoViewFragment extends Fragment{
    private PhotoDataSource dataSource;
    private ListView lvPhotoHistory;
    private TextView tvPhotoInfo;
    private ImageView imageViewPhoto;
    private String showFile;

    public PhotoViewFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Connect Database
        dataSource = new PhotoDataSource(this.getContext());
        dataSource.open();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_photoview, container, false);
        tvPhotoInfo = (TextView)rootView.findViewById(R.id.tvPhotoInfo);
        lvPhotoHistory = (ListView) rootView.findViewById(R.id.lvPhotoHistory);
        imageViewPhoto = (ImageView) rootView.findViewById(R.id.imageViewPhoto);
        //Use the adapter to show the elements in a listView
        List<MyPhoto> myPhotos = dataSource.getAllPhotos();
        String[] mFrom = new String[]{"id","time"};
        int[] mTo = new int[]{R.id.itemTitle1,R.id.itemTitle2};

        List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
        Map<String,Object> mMap = null;
        for(MyPhoto t: myPhotos) {
            mMap = new HashMap<String, Object>();
            mMap.put("data",t);
            mMap.put("id",t.getId());
            mMap.put("time",t.getTime());
            mList.add(mMap);
            Log.d("Description", t.getDescription());
        }

        //Create Adapter
        SimpleAdapter tAdapter = new SimpleAdapter(this.getContext(),mList,R.layout.listview_item,mFrom,mTo);
        lvPhotoHistory.setAdapter(tAdapter);
        lvPhotoHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
                MyPhoto pPhoto = (MyPhoto) map.get("data");
                tvPhotoInfo.setText(pPhoto.getTime() + pPhoto.getLocation() + pPhoto.getDescription());
                showFile = pPhoto.getFilepath();

                // Resize the full image to fit in out image view.
                int width = imageViewPhoto.getWidth();
                int height = imageViewPhoto.getHeight();

                BitmapFactory.Options factoryOptions = new
                        BitmapFactory.Options();

                factoryOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(showFile,
                        factoryOptions);

                int imageWidth = factoryOptions.outWidth;
                int imageHeight = factoryOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(imageWidth / width,
                        imageHeight / height);

                // Decode the image file into a Bitmap sized to fill the View
                factoryOptions.inJustDecodeBounds = false;
                factoryOptions.inSampleSize = scaleFactor;
                //factoryOptions.inPurgeable = true;

                Bitmap bitmap =
                        BitmapFactory.decodeFile(showFile,
                                factoryOptions);

                // imageView.setImageURI(outputFileUri);
                imageViewPhoto.setImageBitmap(bitmap);

            }
        });
        //send through bluetooth
        lvPhotoHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
                MyPhoto pPhoto = (MyPhoto) map.get("data");
                String photoInfo = pPhoto.getTime() + pPhoto.getLocation() + pPhoto.getDescription();

                Uri outputFileUri = Uri.fromFile(new File(pPhoto.getFilepath()));

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, photoInfo);
                shareIntent.putExtra(Intent.EXTRA_STREAM, outputFileUri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share images..."));

                return false;
            }
        });

        return rootView;
    }
}
