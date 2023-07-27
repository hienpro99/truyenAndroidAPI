package hienddph20890.fpoly.asg_hiendd;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private TextView passwordError;
    private Button loginButton;
    private TextView forgotPasswordText;
    private TextView signupText;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userNameInput = findViewById(R.id.userNameInput);
        userNameError = findViewById(R.id.userNameError);
        passwordInput = findViewById(R.id.passwordInput);
        passwordError = findViewById(R.id.passwordError);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
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

                String loginUrl = "http://192.168.1.8:3000/login";

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
                                Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
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
}