package hienddph20890.fpoly.asg_hiendd.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hienddph20890.fpoly.asg_hiendd.R;

public class HomeFragment extends Fragment {
    private ViewFlipper viewFlipper;
    private int currentPage = 0;
    private Handler handler;
    private Runnable runnable;
    private final int DELAY_TIME = 5000; // Thời gian chờ giữa các chuyển đổi (5 giây)

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewFlipper = view.findViewById(R.id.viewlipper);
        ActionViewFlipper();
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoFlip();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoFlip();
    }

    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://skdesu.com/wp-content/uploads/2022/05/image-2.jpg");
        mangquangcao.add("https://phuongnamvina.com/img_data/images/kich-thuoc-banner-web-chuan.jpg");
        mangquangcao.add("https://toquoc.mediacdn.vn/280518851207290880/2022/7/26/banner141200x628-16587434648621017256903-1658811001657-16588110017811422256770.png");

        for (int i = 0; i < mangquangcao.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            Glide.with(getContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }

        viewFlipper.setFlipInterval(DELAY_TIME);
        viewFlipper.setAutoStart(true);

        Animation sline_in = AnimationUtils.loadAnimation(getContext(), R.anim.sline_in_right);
        Animation sline_out = AnimationUtils.loadAnimation(getContext(), R.anim.sline_out_right);
        viewFlipper.setInAnimation(sline_in);
        viewFlipper.setOutAnimation(sline_out);
    }

    private void startAutoFlip() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                currentPage++;
                if (currentPage >= viewFlipper.getChildCount()) {
                    currentPage = 0;
                }
                viewFlipper.setDisplayedChild(currentPage);
                handler.postDelayed(this, DELAY_TIME);
            }
        };
        handler.postDelayed(runnable, DELAY_TIME);
    }

    private void stopAutoFlip() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
