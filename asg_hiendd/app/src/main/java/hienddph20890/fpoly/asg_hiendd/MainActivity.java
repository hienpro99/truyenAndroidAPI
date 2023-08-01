package hienddph20890.fpoly.asg_hiendd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import hienddph20890.fpoly.asg_hiendd.addapter.TabAdapter;
import hienddph20890.fpoly.asg_hiendd.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
// Ngăn việc trượt giữa các tab bằng cách vô hiệu hóa swipe
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        // Retrieve data passed from Login activity
        Intent intent = getIntent();
        Bundle userData = intent.getExtras();
        if (userData != null) {
            // Thêm dữ liệu boolean isAdmin vào userData
            boolean isAdmin = intent.getBooleanExtra("isAdmin", false);
            userData.putBoolean("isAdmin", isAdmin);

            // Chuyển dữ liệu người dùng xuống SearchFragment
            FragmentPagerAdapter pagerAdapter = new TabAdapter(getSupportFragmentManager(), userData);
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            // Nếu không có dữ liệu người dùng, sử dụng TabAdapter bình thường
            TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
            viewPager.setAdapter(tabAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    public void onBackPressed() {
        // Kiểm tra xem có fragment nào đang hiển thị không
        FragmentManager fragmentManager = getSupportFragmentManager();
        int fragmentCount = fragmentManager.getBackStackEntryCount();
        if (fragmentCount > 0) {
            // Nếu có fragment đang hiển thị, pop fragment ra khỏi stack
            fragmentManager.popBackStackImmediate();
        } else {
            // Nếu không có fragment nào đang hiển thị, thực hiện hành động mặc định khi ấn nút back
            super.onBackPressed();
        }

        // Hiển thị lại ViewPager khi quay lại từ ComicDetailFragment
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setVisibility(View.VISIBLE);
    }

}
