package hienddph20890.fpoly.asg_hiendd;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    private EditText userNameInput;
    private TextView userNameError;
    private EditText passwordInput;
    private Button loginButton;
    private TextView signupText;
    private CheckBox saveCredentialsCheckbox;
    private boolean shouldSaveCredentials = false;
    private SharedPreferences sharedPreferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("credentials", MODE_PRIVATE);
        userNameInput = findViewById(R.id.userNameInput);
        userNameError = findViewById(R.id.userNameError);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        saveCredentialsCheckbox = findViewById(R.id.saveCredentialsCheckbox);

        // Check if there are saved credentials and update the checkbox accordingly
        shouldSaveCredentials = sharedPreferences.contains("username") && sharedPreferences.contains("password");
        saveCredentialsCheckbox.setChecked(shouldSaveCredentials);
        saveCredentialsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Lưu trạng thái lựa chọn của người dùng
                shouldSaveCredentials = isChecked;
            }
        });
        loadSavedCredentials();
        TextView textView = findViewById(R.id.textView);

// Hiệu ứng nhấp nháy (alpha)
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f, 1f);
        alphaAnimator.setDuration(1000);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);

// Hiệu ứng thay đổi màu
        PropertyValuesHolder colorValuesHolder = PropertyValuesHolder.ofInt("textColor", Color.RED, Color.BLUE);
        ObjectAnimator colorAnimator = ObjectAnimator.ofPropertyValuesHolder(textView, colorValuesHolder);
        colorAnimator.setDuration(1000);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);

// Kết hợp các hiệu ứng và chạy
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnimator, colorAnimator);
        animatorSet.start();

        signupText = findViewById(R.id.signupText);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin đăng nhập từ người dùng
                String username = userNameInput.getText().toString();
                String password = passwordInput.getText().toString();
                // Kiểm tra nếu có tài khoản cũ trong SharedPreferences thì xóa nó
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("username");
                editor.remove("password");
                if (shouldSaveCredentials && !username.isEmpty() && !password.isEmpty()) {
                    // Nếu checkbox được chọn và người dùng đã nhập thông tin đăng nhập
                    // Lưu tài khoản mới vào SharedPreferences
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.apply();
                } else {
                    editor.apply();
                    Toast.makeText(Login.this, "Tài khoản đăng nhập không được lưu.", Toast.LENGTH_SHORT).show();
                }
                String loginUrl = "http://10.24.54.45:3000/login";

                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("username", username);
                    jsonParams.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Tạo JsonObjectRequest để gửi yêu cầu đăng nhập
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, loginUrl, jsonParams,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    // Get the user object from the server response
                                    JSONObject userObj = response.getJSONObject("user");

                                    // Retrieve id and fullname from the user object
                                    String userId = userObj.getString("_id");
                                    String userFullname = userObj.getString("fullname");
                                    String userEmail = userObj.getString("email");
                                    boolean isAdmin = userObj.getBoolean("admin");
                                    // Create an Intent to launch MainActivity and pass the data
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("userFullname", userFullname);
                                    intent.putExtra("userEmail", userEmail);
                                    intent.putExtra("isAdmin", isAdmin); // Lưu giá trị isAdmin vào Intent

                                    startActivity(intent);
                                    Toast.makeText(Login.this, "Đăng Nhập Thành công", Toast.LENGTH_SHORT).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Xử lý lỗi khi đăng nhập thất bại
                                showLoginError("Đăng nhập thất bại. Vui lòng kiểm tra thông tin đăng nhập của bạn.");
                            }
                        });

                // Thêm yêu cầu vào RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(request);
            }
        });
    }

    private void showLoginError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void loadSavedCredentials() {
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");

        userNameInput.setText(savedUsername);
        passwordInput.setText(savedPassword);
    }
}