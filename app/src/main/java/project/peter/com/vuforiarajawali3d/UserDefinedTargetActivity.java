package project.peter.com.vuforiarajawali3d;

import android.os.Bundle;
import android.util.Log;

import org.rajawali3d.util.RajLog;

import java.util.ArrayList;

import project.peter.com.vuforiarajawali3d.Unit.BaseVuforiaActivity;
import project.peter.com.vuforiarajawali3d.Unit.Model3D;

/**
 * Created by linweijie on 4/29/16.
 */
public class UserDefinedTargetActivity extends BaseVuforiaActivity {

    public static final String TAG = "UserDefinedTarget";
    public static final String OBJ_FILE_PATH = "OBJ_FILE_PATH";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initAR();
        super.onCreate(savedInstanceState);
        RajLog.enableLogging(true);
        String path = getIntent().getExtras().getString(OBJ_FILE_PATH);
        Log.e(TAG,"UserDefinedTargetActivity starting with path: "+path);
        setShowModels(path);
    }

    private void initAR(){
        // set mode
        this.setARMode(BaseVuforiaActivity.MODE_UserDefinedTarget);

        // set max targets will show in same time
        this.setMAX_TARGETS_COUNT(2);
    }

    private void setShowModels(String path){
        // set show models
        ArrayList<Model3D> arrayList = new ArrayList<>();

        Model3D tempM3D;
        //tempM3D = new Model3D(this, R.raw.roadcar_obj);
        tempM3D = new Model3D(this, path);
        tempM3D.setObj_scale(0.2f);
        tempM3D.setObj_translate_x(-20.0f);
        tempM3D.setObj_translate_y(-20.0f);
        tempM3D.setObj_rotate_angle(90.0f);
        arrayList.add(tempM3D);

        this.setModel3DArrayList(arrayList);
    }

}
