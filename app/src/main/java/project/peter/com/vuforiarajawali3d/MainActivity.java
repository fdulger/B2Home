package project.peter.com.vuforiarajawali3d;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt_userdefined_target = (Button)findViewById(R.id.bt_userdefined_target);
        bt_userdefined_target.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_userdefined_target:
                startActivity(new Intent(MainActivity.this, UserDefinedTargetActivity.class));
                break;
        }
    }
}
