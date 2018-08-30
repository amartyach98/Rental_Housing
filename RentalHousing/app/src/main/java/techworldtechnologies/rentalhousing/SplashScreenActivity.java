package techworldtechnologies.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class SplashScreenActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_splashscreen);
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               Intent intent=new Intent(SplashScreenActivity.this,Login_Activity.class);
               startActivity(intent);
               finish();
           }
       },1*2000);



    }
}
