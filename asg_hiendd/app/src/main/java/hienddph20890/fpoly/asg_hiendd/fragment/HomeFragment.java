package hienddph20890.fpoly.asg_hiendd.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.addapter.TruyenAdapter;
import hienddph20890.fpoly.asg_hiendd.addapter.TruyenHomeAdapter;
import hienddph20890.fpoly.asg_hiendd.model.Truyen;

public class HomeFragment extends Fragment {
    private ViewFlipper viewFlipper;
    private int currentPage = 0;
    private Runnable runnable;
    private final int DELAY_TIME = 5000; // Thời gian chờ giữa các chuyển đổi (5 giây)
    private RecyclerView idRCVH;
    TruyenHomeAdapter truyenHomeAdapter;
    private List<Truyen> productList;
    private ViewPager viewPager;
    private static final long INTERVAL_IN_MILLISECONDS = 60000; // 1 phút
    private static final long INITIAL_DELAY_IN_MILLISECONDS = 0; // Không có độ trễ ban đầu
    private Handler handler = new Handler();
    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            dataproduct();
            handler.postDelayed(this, INTERVAL_IN_MILLISECONDS);
        }
    };
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
        idRCVH = view.findViewById(R.id.idRCVH);
        // Thay đổi LinearLayoutManager thành LinearLayoutManager với hướng ngang (horizontal)
        idRCVH.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        productList = new ArrayList<>();
        truyenHomeAdapter = new TruyenHomeAdapter(productList);
        truyenHomeAdapter.setOnItemClickListener(new TruyenHomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Truyen truyen) {
                openComicDetailFragment(truyen);
            }
        });
        idRCVH.setAdapter(truyenHomeAdapter);
      dataproduct();
        // bắt đầu cập nhật khi ưứng dunụng chạy
        handler.postDelayed(updateTask, INITIAL_DELAY_IN_MILLISECONDS);


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the update task when the fragment is destroyed
        handler.removeCallbacks(updateTask);
    }
    private void openComicDetailFragment(Truyen truyen) {
        // Ẩn ViewPager
        viewPager.setVisibility(View.GONE);
        ComicDetailFragment comicDetailFragment = new ComicDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("truyen", truyen);
        bundle.putString("comicId", truyen.getId());
        // Lấy dữ liệu người dùng từ arguments của SearchFragment
        Bundle searchFragmentBundle = getArguments();
        if (searchFragmentBundle != null) {
            String userId = searchFragmentBundle.getString("userId");
            String userFullname = searchFragmentBundle.getString("userFullname");
            bundle.putString("userId", userId);
            bundle.putString("userFullname", userFullname);
        }
        comicDetailFragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, comicDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void dataproduct() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
         String URL_GET_SP = "http://192.168.1.8:3000/comics";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL_GET_SP, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("comics");
                            // Xóa dữ liệu cũ trong productList
                            productList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String id = jsonObject.getString("_id");
                                String name = jsonObject.getString("name");
                                String description = jsonObject.getString("description");
                                int yearPublished = jsonObject.optInt("yearPublished", 0); // Lấy giá trị trường yearPublished, mặc định là 0 nếu không có trong JSON
                                String author = jsonObject.getString("author");
                                String hinhAnhUrl = jsonObject.getString("coverImage");
                                Truyen truyen = new Truyen(id,name, description, author, yearPublished, hinhAnhUrl);
                                productList.add(truyen);
                            }
                            truyenHomeAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("errorVolley", error.toString());
                        Toast.makeText(getContext(), "Đã xảy ra lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(objectRequest);
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
