package hienddph20890.fpoly.asg_hiendd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplassScreen extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splass_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Chuyển đến màn hình chính sau khi màn hình chào kết thúc
                Intent intent = new Intent(SplassScreen.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}