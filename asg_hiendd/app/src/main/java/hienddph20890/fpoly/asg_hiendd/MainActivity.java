package hienddph20890.fpoly.asg_hiendd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import hienddph20890.fpoly.asg_hiendd.addapter.TabAdapter;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
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