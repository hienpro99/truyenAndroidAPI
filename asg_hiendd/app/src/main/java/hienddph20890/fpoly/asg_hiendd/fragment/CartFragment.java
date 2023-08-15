package hienddph20890.fpoly.asg_hiendd.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.addapter.UserAdapter;
import hienddph20890.fpoly.asg_hiendd.model.User;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private static final long INTERVAL_IN_MILLISECONDS = 10000; // 1 phút
    private static final long INITIAL_DELAY_IN_MILLISECONDS = 0; // Không có độ trễ ban đầu
    private Handler handler = new Handler();
    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            getUsersFromApi();
            handler.postDelayed(this, INTERVAL_IN_MILLISECONDS);
        }
    };

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.idrcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(requireContext(), userList);
        recyclerView.setAdapter(userAdapter);

        // Gọi API để lấy danh sách người dùng
        getUsersFromApi();
        handler.postDelayed(updateTask, INITIAL_DELAY_IN_MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the update task when the fragment is destroyed
        handler.removeCallbacks(updateTask);
    }
    private void getUsersFromApi() {
        String url = "http://10.24.54.45:3000/listusers";

        JsonObjectRequest getRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý dữ liệu trả về từ server
                        try {
                            JSONArray jsonArray = response.getJSONArray("account");
                            // Xóa dữ liệu cũ trong productList
                            userList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject userObject = jsonArray.getJSONObject(i);

                                // Lấy thông tin của người dùng từ JSON và tạo đối tượng User
                                String userId = userObject.getString("_id");
                                String username = userObject.getString("username");
                                String fullname = userObject.getString("fullname");
                                String email = userObject.getString("email");
                                Boolean Admin = userObject.getBoolean("admin");
                                User user = new User(userId, username, fullname,email,Admin);
                                userList.add(user);
                            }

                            // Cập nhật dữ liệu lên Adapter và hiển thị lên RecyclerView
                            userAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi khi gọi API không thành công
                        Toast.makeText(requireContext(), "Error getting user list: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Thêm request vào RequestQueue để thực thi
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(getRequest);
    }
}
