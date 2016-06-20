package project.peter.com.vuforiarajawali3d;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.rajawali3d.Camera;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.renderer.RajawaliRenderer;

import java.util.ArrayList;

import project.peter.com.vuforiarajawali3d.Unit.BaseVuforiaActivity;
import project.peter.com.vuforiarajawali3d.Unit.Model3D;
import project.peter.com.vuforiarajawali3d.Unit.ObjectsCallback;

/**
 * Created by linweijie on 6/20/16.
 */
public class CylinderTargetActivity extends BaseVuforiaActivity implements View.OnClickListener {

    private String LOGTAG = "CylinderTargetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initAR();
        super.onCreate(savedInstanceState);
        setShowModels();
        addCustomView();

        Button bt_about = (Button)findViewById(R.id.bt_about);
        bt_about.setText("ABOUT");
        bt_about.setOnClickListener(this);
    }

    private void initAR(){
        // set mode
        this.setARMode(BaseVuforiaActivity.MODE_ImageTarget);

        // set local target library
        ArrayList<String> dataString = new ArrayList<>();
        dataString.add("3dObjectPhoto.xml");
        this.setDatasetStrings(dataString);

        // set max targets will show in same time
        this.setMAX_TARGETS_COUNT(1);
    }

    private void setShowModels(){
        // set show models
        ArrayList<Model3D> arrayList = new ArrayList<>();

        Model3D tempM3D;

        // target 1 , id : 0
        // MESH MD5 ANIMS
        tempM3D = new Model3D(this, R.raw.ingrid_mesh);
        tempM3D.setMODE(Model3D.LOAD_MD5_MASH);
        tempM3D.addAnims(R.raw.ingrid_idle);
        tempM3D.addAnims(R.raw.ingrid_arm_stretch);
        tempM3D.addAnims(R.raw.ingrid_bend);
        tempM3D.addAnims(R.raw.ingrid_walk);
        tempM3D.setObj_scale(300.0f);
        tempM3D.setObj_rotate_angle(90.0f);

        tempM3D.setCanCollision(false);
        tempM3D.setShowBounding(false);
        tempM3D.setColl_pos_y(0.1f);
        tempM3D.setColl_scale_x(1.5f);
        tempM3D.setColl_scale_y(6.0f);

        arrayList.add(tempM3D);

        // target 2 , id : 1
        // VIDEO

        tempM3D = new Model3D(this, R.raw.music_video, new ObjectsCallback() {
            @Override
            public void parse(RajawaliRenderer renderer) {
                
            }
            @Override
            public void render(Camera camera, Matrix4 vpMatrix, Matrix4 projMatrix, Matrix4 vMatrix) {

            }
        });
        tempM3D.setMODE(Model3D.LOAD_VIDEO_PLANE);
        tempM3D.setObj_scale(100.0f);
        tempM3D.setObj_translate_x(0.0f);
        tempM3D.setObj_translate_y(-800.0f);
        tempM3D.setObj_rotate_angle(90.0f);

        arrayList.add(tempM3D);

        this.setModel3DArrayList(arrayList);
    }

    private void addCustomView(){
        // add custom view
        View root = findViewById(android.R.id.content);
        View.inflate(this, R.layout.sub_custom, (ViewGroup) root);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(CylinderTargetActivity.this, "CylinderTarget", Toast.LENGTH_SHORT).show();
    }
}
