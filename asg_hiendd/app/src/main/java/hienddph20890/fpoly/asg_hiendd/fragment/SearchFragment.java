package hienddph20890.fpoly.asg_hiendd.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.addapter.SelectedImageAdapter;
import hienddph20890.fpoly.asg_hiendd.addapter.TruyenAdapter;
import hienddph20890.fpoly.asg_hiendd.model.Truyen;

public class SearchFragment extends Fragment {
    //Search
    private EditText etSearch;
    //
    //add
    private SelectedImageAdapter adapter; // Khai báo Adapter mới
    private List<String> selectedImages = new ArrayList<>();
    //
    private RecyclerView idRCV;
    private FloatingActionButton fab;
    Dialog dialog;
    private List<Truyen> productList;
    private TruyenAdapter truyenAdapter;
    private ViewPager viewPager;
    private String URL_GET_SP = "http://10.24.54.45:3000/comics";
    //
    private static final long INTERVAL_IN_MILLISECONDS = 10000; // 1 phút
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
        // Search
        ImageView ivsearch = view.findViewById(R.id.ivsearch);
        ivsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setVisibility(View.VISIBLE);
                String searchQuery = etSearch.getText().toString().trim();
                performSearch(searchQuery);
            }
        });
        etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Thực hiện tìm kiếm khi người dùng nhập liệu và mỗi lần nội dung thay đổi
                String searchQuery = charSequence.toString().trim();
                performSearch(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //end search
        Boolean isAdmin = getArguments().getBoolean("isAdmin");
        idRCV = view.findViewById(R.id.idRCV);
        idRCV.setLayoutManager(new LinearLayoutManager(getContext()));
        viewPager = getActivity().findViewById(R.id.viewPager);
        viewPager.setVisibility(View.VISIBLE);
        fab = view.findViewById(R.id.fab);
        productList = new ArrayList<>();
        truyenAdapter = new TruyenAdapter(getContext(),productList,isAdmin);
        truyenAdapter.setOnItemClickListener(new TruyenAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Truyen truyen) {
                openComicDetailFragment(truyen); // Gọi phương thức mở ComicDetailFragment và truyền dữ liệu của Truyen
            }
        });
        idRCV.setAdapter(truyenAdapter);

       if (isAdmin){
           fab.setOnClickListener(view1 -> {
               openDiaLog(getContext());
           });
       }else {
           fab.setVisibility(View.INVISIBLE);
       }

        dataproduct();
        // bắt đầu cập nhật khi ưứng dunụng chạy
        handler.postDelayed(updateTask, INITIAL_DELAY_IN_MILLISECONDS);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Reset danh sách truyện trên Adapter khi quay lại tab search
        truyenAdapter.updateData(productList);
    }

    private void performSearch(String searchQuery) {
        // Tạo một danh sách mới để lưu các truyện trùng khớp
        List<Truyen> matchedTruyenList = new ArrayList<>();

        // Lặp qua danh sách truyện hiện có để tìm kiếm các truyện trùng khớp với searchQuery
        for (Truyen truyen : productList) {
            if (truyen.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                // Nếu tên truyện chứa searchQuery, thì thêm vào danh sách trùng khớp
                matchedTruyenList.add(truyen);
            }
        }

        // Cập nhật danh sách truyện trên Adapter để hiển thị các truyện trùng khớp
        truyenAdapter.updateData(matchedTruyenList);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTask);
    }

    private void openComicDetailFragment(Truyen truyen) {
        // Ẩn ViewPager
        viewPager.setVisibility(View.GONE);
        ComicDetailFragment comicDetailFragment = new ComicDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("truyen", truyen);
        bundle.putString("comicId", truyen.getId());
        // Lấy dữ liệu người dùng từ arguments Main truyền xuống addapter của SearchFragment
        Bundle searchFragmentBundle = getArguments();
        if (searchFragmentBundle != null) {
            String userId = searchFragmentBundle.getString("userId");
            String userFullname = searchFragmentBundle.getString("userFullname");
            Boolean isAdmin = getArguments().getBoolean("isAdmin");
            bundle.putString("userId", userId);
            bundle.putString("userFullname", userFullname);
            bundle.putBoolean("isAdmin",isAdmin);
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
                                Truyen truyen = new Truyen(id, name, description, author, yearPublished, hinhAnhUrl);
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
        dialog.setCancelable(true);

        EditText etName = dialog.findViewById(R.id.edtName);
        EditText etDescription = dialog.findViewById(R.id.edtDescription);
        EditText etAuthor = dialog.findViewById(R.id.edtAuthor);
        EditText etYearPublished = dialog.findViewById(R.id.edtYearPublished);
        EditText edtImageUrl = dialog.findViewById(R.id.edtImageUrl);
        Button btnAddComic = dialog.findViewById(R.id.btnAddComic);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        RecyclerView recyclerView = dialog.findViewById(R.id.rcvSelectedImages);
        adapter = new SelectedImageAdapter(selectedImages);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
        // Đăng ký lắng nghe sự kiện khi bấm nút "Add Comic Image"
        btnAddComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy URL ảnh từ EditText
                String imageUrl = edtImageUrl.getText().toString().trim();
                selectedImages.add(imageUrl);

                // Hiển thị các URL ảnh đã chọn lên RecyclerView
                adapter.notifyDataSetChanged();
                edtImageUrl.setText("");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin truyện từ EditTexts
                String name = etName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String author = etAuthor.getText().toString().trim();
                String yearPublished = etYearPublished.getText().toString().trim();

                // Chỉ gửi API khi đủ thông tin và có ít nhất một ảnh đã chọn
                if (!name.isEmpty() && !description.isEmpty() && !author.isEmpty() && !yearPublished.isEmpty() && !selectedImages.isEmpty()) {
                    int yearPublishedInt = Integer.parseInt(yearPublished);
                    addComicWithImageUrl(name, description, author, yearPublishedInt, selectedImages);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Vui lòng nhập đủ thông tin truyện và chọn ít nhất một ảnh!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }


    private void addComicWithImageUrl(String name, String description, String author, int yearPublished, List<String> selectedImages) {
        String url = "http://10.24.54.45:3000/comic"; // Thay thế bằng API endpoint của bạn
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        try {
            // Tạo JSONObject chứa thông tin truyện và danh sách các URL ảnh đã chọn
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("name", name);
            jsonRequest.put("description", description);
            jsonRequest.put("author", author);
            jsonRequest.put("yearPublished", yearPublished);

            // Chuyển danh sách các URL ảnh sang một JSONArray
            JSONArray imagesArray = new JSONArray(selectedImages);
            jsonRequest.put("images", imagesArray);

            // Tạo một POST request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Xử lý khi server trả về thành công
                            // Cập nhật danh sách truyện sau khi thêm truyện thành công
                            dataproduct();
                            Toast.makeText(getContext(), "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                            // Xóa danh sách các URL ảnh đã chọn để chuẩn bị cho lần thêm truyện tiếp theo
                            selectedImages.clear();

                            // Cập nhật lại RecyclerView để hiển thị danh sách URL ảnh đã chọn
                            adapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Xử lý khi gửi request thất bại
                            Toast.makeText(getContext(), "Lỗi khi thêm truyện mới!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            // Thêm request vào hàng đợi và gửi đi
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            // Xử lý nếu có lỗi khi tạo JSONObject
            Toast.makeText(getContext(), "Lỗi khi xử lý dữ liệu truyện!", Toast.LENGTH_SHORT).show();
        }
    }


}
