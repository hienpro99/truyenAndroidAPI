package hienddph20890.fpoly.asg_hiendd.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import org.json.JSONException;
import org.json.JSONObject;

import hienddph20890.fpoly.asg_hiendd.Login;
import hienddph20890.fpoly.asg_hiendd.R;

public class change_passwordFragment extends Fragment {

    public change_passwordFragment() {
        // Required empty public constructor
    }

    public static change_passwordFragment newInstance() {
        change_passwordFragment fragment = new change_passwordFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Bộ lắng nghe sự kiện khi nút back được nhấn
        // Ánh xạ các view từ layout XML
        EditText etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = view.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        Button btnChangePassword = view.findViewById(R.id.btnChangePassword);
        // Ánh xạ các view từ layout XML
        ImageView backButton = view.findViewById(R.id.backButton);

        // Bắt sự kiện khi người dùng ấn nút back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại UserFragment khi ấn nút back
                goBackToUserFragment();
            }
        });
        // Lấy dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userIdValue = bundle.getString("userId");

            // Bộ lắng nghe sự kiện khi nút "Đổi mật khẩu" được nhấn
            btnChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lấy giá trị đã nhập trong các trường EditText
                    String currentPassword = etCurrentPassword.getText().toString();
                    String newPassword = etNewPassword.getText().toString();
                    String confirmPassword = etConfirmPassword.getText().toString();
                    Toast.makeText(getContext(), userIdValue, Toast.LENGTH_SHORT).show();
                    // Kiểm tra mật khẩu hiện tại không được để trống
                    if (currentPassword.isEmpty()) {
                        etCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
                        return;
                    }

                    // Kiểm tra mật khẩu mới không được để trống
                    if (newPassword.isEmpty()) {
                        etNewPassword.setError("Vui lòng nhập mật khẩu mới");
                        return;
                    }

                    // Kiểm tra xác nhận mật khẩu mới không được để trống
                    if (confirmPassword.isEmpty()) {
                        etConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
                        return;
                    }

                    // Kiểm tra xem mật khẩu mới và xác nhận mật khẩu mới có khớp nhau không
                    if (!newPassword.equals(confirmPassword)) {
                        etConfirmPassword.setError("Mật Khẩu hoặc Xác nhận mật khẩu mới không khớp");
                        return;
                    }

                    // Gọi API đổi mật khẩu
                    String url = "http://10.24.54.45:3000/changepasswords";
                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("userId", userIdValue);
                        requestBody.put("currentPassword", currentPassword);
                        requestBody.put("newPassword", newPassword);
                        requestBody.put("confirmPassword", confirmPassword);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Xử lý khi đổi mật khẩu thành công
                                    Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                                    // Xóa nội dung các trường EditText sau khi đã thay đổi mật khẩu thành công
                                    etCurrentPassword.setText("");
                                    etNewPassword.setText("");
                                    etConfirmPassword.setText("");
                                    goBackToUserFragment();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Xử lý khi có lỗi từ server
                                    Toast.makeText(getContext(), "Mật khẩu cũ hoặc mật khẩu mới không đúng ", Toast.LENGTH_SHORT).show();
                                }
                            });

                    // Thêm request vào hàng đợi của Volley để thực hiện gửi request đến server
                    RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
                    requestQueue.add(request);
                }
            });
        }
    }
    // Phương thức để quay lại Fragment User sau khi đổi mật khẩu thành công
    private void goBackToUserFragment() {
        if (getActivity() != null) {
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//            // Tạo một instance của Fragment User
//            UserFragment userFragment = UserFragment.newInstance();
//            // Thực hiện thay thế Fragment hiện tại bằng Fragment User và đưa Fragment cũ vào BackStack
//            fragmentTransaction.replace(R.id.fragment_container, userFragment);
//            fragmentTransaction.addToBackStack(null);
//
//            // Submit thay đổi
//            fragmentTransaction.commit();
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
        }
    }


}
