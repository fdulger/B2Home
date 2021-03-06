package com.fdulger.b2home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//TODO:
//handle error cases: !!! no connection, invalid object, ...
//add more models !!!
//gallery refresh button !
//scale model dynamically !
//load other formats !
//final report !!!!!!!!!!!!!!!!!!!!!!!!!!!

    public class OnlineGalleryActivity extends AppCompatActivity {

        private static final String TAG = "OnlineGalleryActivity";

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnlineGalleryActivity.this,PreferencesActivity.class));
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        final String server_ip = PreferenceManager.getDefaultSharedPreferences(OnlineGalleryActivity.this).getString("server_ip",getString(R.string.pref_default_server_ip));
        String url ="http://"+server_ip+"/b2home/index.json";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LinearLayout gallery_items_container = (LinearLayout) findViewById(R.id.gallery_items_container);
                        try {
                            JSONArray jsonArr = new JSONArray(response);
                            for(int i=0;i<jsonArr.length();i++) {
                                final JSONObject item = jsonArr.getJSONObject(i);
                                View item_view = getLayoutInflater().inflate(R.layout.gallery_item_layout, null);
                                ((TextView)item_view.findViewById(R.id.gallery_item_title)).setText(item.getString("title"));

                                JSONArray thumbnails = item.getJSONArray("thumbnails");
                                LinearLayout thumbnails_container = (LinearLayout)item_view.findViewById(R.id.gallery_item_pictures_container_scroll);
                                for(int j=0;j< thumbnails.length();j++) {
                                    ImageView thumbnail = new ImageView(OnlineGalleryActivity.this);
                                    new DownloadThumbnailTask(thumbnail)
                                            .execute("http://"+server_ip+"/b2home/thumbs/"+thumbnails.getJSONObject(j).getString("url"));
                                    thumbnails_container.addView(thumbnail);
                                }
                                thumbnails_container.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    try {
                                        File obj = new File(Environment.getExternalStorageDirectory().getPath() + "/" + item.getString("folder") + "/" + item.getString("file"));
                                        if(obj.exists()) {
                                            Log.e(TAG,"obj file already exist! ");
                                            startShow(
                                                item.getString("folder") + "/" + item.getString("file"),
                                                item.has("scale") ? Float.parseFloat(item.getString("scale")) : 0.0f,
                                                item.has("rotation") ? Float.parseFloat(item.getString("rotation")) : 0.0f,
                                                item.has("translatex") ? Float.parseFloat(item.getString("translatex")) : 0.0f,
                                                item.has("translatey") ? Float.parseFloat(item.getString("translatey")) : 0.0f
                                            );
                                        } else {
                                            new DownloadObjectTask(
                                                    OnlineGalleryActivity.this,
                                                    item.getString("folder"),
                                                    item.getString("file"),
                                                    item.has("scale") ? Float.parseFloat(item.getString("scale")) : 0.0f,
                                                    item.has("rotation") ? Float.parseFloat(item.getString("rotation")) : 0.0f,
                                                    item.has("translatex") ? Float.parseFloat(item.getString("translatex")) : 0.0f,
                                                    item.has("translatey") ? Float.parseFloat(item.getString("translatey")) : 0.0f)
                                                    .execute("http://"+server_ip+"/b2home/files/"+item.getString("url"));
                                        }
                                    } catch(JSONException e) {
                                        Log.e(TAG,"Unable to parse object url!",e);
                                    }
                                    }
                                });
                                gallery_items_container.addView(item_view);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG,"Unable to parse response, invalid json format: " + response,e);
                        }
                    }
                }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LinearLayout gallery_items_container = (LinearLayout) findViewById(R.id.gallery_items_container);
                            TextView text = new TextView(OnlineGalleryActivity.this);
                            text.setText(getString(R.string.error_cannot_connect_server));
                            text.setTextSize(30);
                            gallery_items_container.setVerticalGravity(Gravity.CENTER_VERTICAL);
                            gallery_items_container.addView(text);
                        }
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void startShow(String file,Float scale,Float rotation,Float translatex,Float translatey) {
        Intent intent = new Intent(OnlineGalleryActivity.this, UserDefinedTargetActivity.class);
        intent.putExtra(UserDefinedTargetActivity.OBJ_FILE_PATH,file);
        intent.putExtra(UserDefinedTargetActivity.OBJ_SCALE_FACTOR,scale);
        intent.putExtra(UserDefinedTargetActivity.OBJ_ROTATION_FACTOR,rotation);
        intent.putExtra(UserDefinedTargetActivity.OBJ_TRANSLATE_X_FACTOR,translatex);
        intent.putExtra(UserDefinedTargetActivity.OBJ_TRANSLATE_Y_FACTOR,translatey);
        startActivity(intent);
    }

    private class DownloadThumbnailTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadThumbnailTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class DownloadObjectTask extends AsyncTask<String, Integer, String> {

        private OnlineGalleryActivity activity;
        private ProgressDialog downloadProgress;
        private PowerManager.WakeLock mWakeLock;

        private String obj_folder;
        private String obj_file;
        private Float scale;
        private Float rotation;
        private Float translatex;
        private Float translatey;

        public DownloadObjectTask(
                OnlineGalleryActivity activity,
                String folder,
                String file,
                Float scale,
                Float rotation,
                Float translatex,
                Float translatey) {
            this.activity = activity;
            this.obj_folder = folder;
            this.obj_file = file;
            this.scale = scale;
            this.rotation = rotation;
            this.translatex = translatex;
            this.translatey = translatey;
            downloadProgress = new ProgressDialog(this.activity);
            downloadProgress.setCancelable(true);
            downloadProgress.setIndeterminate(true);
            downloadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            downloadProgress.setMessage("Downloading...");
        }

        protected String doInBackground(String... urls) {
            String zip_name = System.currentTimeMillis()+ "_object.zip";
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.i(TAG,"download failed: " + connection.getResponseMessage());
                    return null;
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + zip_name);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        Log.i(TAG,"download cancelled.");
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            Log.i(TAG,"download finished: " + Environment.getExternalStorageDirectory().getPath() + "/" + zip_name);
            publishProgress(101);
            if(unpackZip(Environment.getExternalStorageDirectory().getPath(), zip_name)) {
                Log.i(TAG,"extracted archive");
            } else {
                Log.e(TAG,"extract failed");
                return null;
            }
            File zip = new File(Environment.getExternalStorageDirectory().getPath() + "/" + zip_name);
            if(zip.exists()) {
                Log.e(TAG,"deleting zip");
                zip.delete();
            } else {
                Log.e(TAG,"zip does not exist!");
                return null;
            }
            File obj = new File(Environment.getExternalStorageDirectory().getPath() + "/" + obj_folder + "/" + obj_file);
            if(obj.exists()) {
                Log.e(TAG,"obj file :" + obj.getAbsolutePath());
                return obj_folder + "/" + obj_file;
            } else {
                Log.e(TAG,"obj file does not exist! " + obj.getAbsolutePath());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) this.activity.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            downloadProgress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            if(progress[0]>100) {
                downloadProgress.setMessage("Extracting...");
                downloadProgress.setIndeterminate(true);
            } else {
                downloadProgress.setIndeterminate(false);
                downloadProgress.setMax(100);
                downloadProgress.setProgress(progress[0]);
            }
        }

        protected void onPostExecute(String path) {
            downloadProgress.dismiss();
            if(path == null) {
                Log.e(TAG,"Error, can not open model");
                return;
            }
            else {
                Log.e(TAG,"Object read: " + path);
            }
            startShow(path,scale,rotation,translatex,translatey);
        }

        private boolean unpackZip(String path, String zipname)
        {
            InputStream is;
            ZipInputStream zis;
            try
            {
                String filename;
                is = new FileInputStream(path + "/" + zipname);
                zis = new ZipInputStream(new BufferedInputStream(is));
                ZipEntry ze;
                byte[] buffer = new byte[1024];
                int count;

                while ((ze = zis.getNextEntry()) != null)
                {
                    // zapis do souboru
                    filename = ze.getName();

                    // Need to create directories if not exists, or
                    // it will generate an Exception...
                    if (ze.isDirectory()) {
                        File fmd = new File(path + "/" + filename);
                        fmd.mkdirs();
                        continue;
                    }

                    FileOutputStream fout = new FileOutputStream(path + "/" + filename);

                    // cteni zipu a zapis
                    while ((count = zis.read(buffer)) != -1)
                    {
                        fout.write(buffer, 0, count);
                    }

                    fout.close();
                    zis.closeEntry();
                }

                zis.close();
            }
            catch(IOException e)
            {
                Log.e(TAG,"error while extraction",e);
                return false;
            }

            return true;
        }
    }
}
