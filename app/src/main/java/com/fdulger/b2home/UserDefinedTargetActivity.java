package com.fdulger.b2home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fdulger.b2home.Unit.BaseVuforiaActivity;
import com.fdulger.b2home.Unit.Model3D;

import org.rajawali3d.util.RajLog;

import java.util.ArrayList;

/**
 * Created by linweijie on 4/29/16.
 */
public class UserDefinedTargetActivity extends BaseVuforiaActivity {

    public static final String TAG = "UserDefinedTarget";
    public static final String OBJ_FILE_PATH = "OBJ_FILE_PATH";
    public static final String OBJ_SCALE_FACTOR = "OBJ_SCALE_FACTOR";
    public static final String OBJ_ROTATION_FACTOR = "OBJ_ROTATION_FACTOR";
    public static final String OBJ_TRANSLATE_X_FACTOR = "OBJ_TRANSLATE_X_FACTOR";
    public static final String OBJ_TRANSLATE_Y_FACTOR = "OBJ_TRANSLATE_Y_FACTOR";

    private static final int CONTROL_COMMAND_SCALE_UP = 0;
    private static final int CONTROL_COMMAND_SCALE_DOWN = 1;
    private static final int CONTROL_COMMAND_TRANSLATE_X_1 = 2;
    private static final int CONTROL_COMMAND_TRANSLATE_X_2 = 3;
    private static final int CONTROL_COMMAND_TRANSLATE_Y_1 = 4;
    private static final int CONTROL_COMMAND_TRANSLATE_Y_2 = 5;
    private static final int CONTROL_COMMAND_ROTATE = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initAR();
        super.onCreate(savedInstanceState);
        RajLog.enableLogging(true);
        Bundle extras = getIntent().getExtras();
        String path = extras.getString(OBJ_FILE_PATH);
        Float scale = extras.getFloat(OBJ_SCALE_FACTOR);
        Float rotation = extras.getFloat(OBJ_ROTATION_FACTOR);
        Float translatex = extras.getFloat(OBJ_TRANSLATE_X_FACTOR,0);
        Float translatey = extras.getFloat(OBJ_TRANSLATE_Y_FACTOR,0);
        Log.e(TAG,"UserDefinedTargetActivity starting with path: "+path);
        setShowModels(path,scale,rotation,translatex,translatey);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_defined_target_activity, menu);
        return true;
    }

    private void initAR(){
        // set mode
        this.setARMode(BaseVuforiaActivity.MODE_UserDefinedTarget);
        // set max targets will show in same time
        this.setMAX_TARGETS_COUNT(2);
    }

    private void setShowModels(String path, Float scale, Float rotation, Float translatex, Float translatey){
        // set show models
        ArrayList<Model3D> arrayList = new ArrayList<>();

        Model3D tempM3D;
        tempM3D = new Model3D(this, path);
        tempM3D.setObj_scale(scale);
        tempM3D.setObj_translate_x(translatex);
        tempM3D.setObj_translate_y(translatey);
        tempM3D.setObj_translate_y(translatey);
        tempM3D.setObj_rotate_angle(rotation);
        arrayList.add(tempM3D);

        this.setModel3DArrayList(arrayList);
    }

    private void move(int command){
        switch (command) {
            case CONTROL_COMMAND_SCALE_UP:
                BaseRajawaliRender.scaleUp(0);
                break;
            case CONTROL_COMMAND_SCALE_DOWN:
                BaseRajawaliRender.scaleDown(0);
                break;
            case CONTROL_COMMAND_TRANSLATE_X_1:
                BaseRajawaliRender.translateX(0,20.0f);
                break;
            case CONTROL_COMMAND_TRANSLATE_X_2:
                BaseRajawaliRender.translateX(0,-20.0f);
                break;
            case CONTROL_COMMAND_TRANSLATE_Y_1:
                BaseRajawaliRender.translateY(0,20.0f);
                break;
            case CONTROL_COMMAND_TRANSLATE_Y_2:
                BaseRajawaliRender.translateY(0,-20.0f);
                break;
            case CONTROL_COMMAND_ROTATE:
                BaseRajawaliRender.rotate(0);
                break;
            default:
                break;
        }
    }

    public void onBuyAction(MenuItem item) {
        Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.todo), Snackbar.LENGTH_LONG)
                .show();
    }

    public void onZoomIn(View view) {
        move(CONTROL_COMMAND_SCALE_UP);
    }

    public void onZoomOut(View view) {
        move(CONTROL_COMMAND_SCALE_DOWN);
    }

    public void onMoveUp(View view) {
        move(CONTROL_COMMAND_TRANSLATE_X_2);
    }

    public void onMoveDown(View view) {
        move(CONTROL_COMMAND_TRANSLATE_X_1);
    }

    public void onMoveLeft(View view) {
        move(CONTROL_COMMAND_TRANSLATE_Y_2);
    }

    public void onMoveRight(View view) {
        move(CONTROL_COMMAND_TRANSLATE_Y_1);
    }

    public void onRotate(View view) {
        move(CONTROL_COMMAND_ROTATE);
    }

}
