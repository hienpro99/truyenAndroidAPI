package hienddph20890.fpoly.asg_hiendd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText fullNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText userNameInput;
    private Button registerButton;
    private TextView loginText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        userNameInput = findViewById(R.id.userNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.loginText);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,Login.class );
                startActivity(intent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin từ các trường nhập liệu
                String fullName = fullNameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String username = userNameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Tạo đối tượng JSON để gửi dữ liệu đăng ký lên server
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("fullname", fullName);
                    jsonParams.put("email", email);
                    jsonParams.put("username", username);
                    jsonParams.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Gửi yêu cầu đăng ký thông qua API sử dụng Volley
                String registerUrl = "http://10.24.54.45:3000/register";
                RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, registerUrl, jsonParams,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // Kiểm tra xem đăng ký thành công hay không
                                    if (response.has("newUser")) {
                                        // Đăng ký thành công
                                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                         Intent intent = new Intent(RegisterActivity.this,Login.class);
                                         startActivity(intent);
                                        // Chuyển về trang đăng nhập
                                        finish();
                                    } else if (response.has("error")) {
                                        // Đăng ký thất bại vì thông tin đã tồn tại
                                        String errorMessage = response.getString("error");
                                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Xử lý lỗi khi đăng ký thất bại
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        // Truyền thông tin header nếu cần thiết (ví dụ: định dạng dữ liệu, token, ...)
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                // Thêm yêu cầu vào hàng đợi và gửi
                requestQueue.add(request);
            }
        });

    }
}