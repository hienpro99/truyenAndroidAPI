package hienddph20890.fpoly.asg_hiendd.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.addapter.TruyenAdapter;
import hienddph20890.fpoly.asg_hiendd.model.Truyen;

public class SearchFragment extends Fragment {
    private RecyclerView idRCV;
    private FloatingActionButton fab;
    Dialog dialog;
    private List<Truyen> productList;
    private TruyenAdapter truyenAdapter;
    private ViewPager viewPager;
    private String URL_GET_SP = "http://192.168.1.8:3000/comics";
    //
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

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idRCV = view.findViewById(R.id.idRCV);
        idRCV.setLayoutManager(new LinearLayoutManager(getContext()));
         viewPager = getActivity().findViewById(R.id.viewPager);
        viewPager.setVisibility(View.VISIBLE);
        fab = view.findViewById(R.id.fab);
        productList = new ArrayList<>();
        truyenAdapter = new TruyenAdapter(productList);
        truyenAdapter.setOnItemClickListener(new TruyenAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Truyen truyen) {
                openComicDetailFragment(truyen); // Gọi phương thức mở ComicDetailFragment và truyền dữ liệu của Truyen
            }
        });
        idRCV.setAdapter(truyenAdapter);

        fab.setOnClickListener(view1 -> {
            openDiaLog(getContext());
        });

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
        comicDetailFragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, comicDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void dataproduct() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                            truyenAdapter.notifyDataSetChanged();
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

    protected void openDiaLog(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Thêm Truyện");
        dialog.setCancelable(true);

        dialog.show();
    }
}
