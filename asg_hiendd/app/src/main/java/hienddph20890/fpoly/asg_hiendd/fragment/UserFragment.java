package hienddph20890.fpoly.asg_hiendd.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import hienddph20890.fpoly.asg_hiendd.ChatActivity;
import hienddph20890.fpoly.asg_hiendd.Login;
import hienddph20890.fpoly.asg_hiendd.R;

public class UserFragment extends Fragment {
    private TextView userId, userName, userEmail, tvChangePassword,iddangxuat;
    private Handler handler;
    private Runnable dataUpdater;
    private LinearLayout section3;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        section3 = view.findViewById(R.id.section3);
        section3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                startActivity(intent);
            }
        });
        userId = view.findViewById(R.id.userId);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        tvChangePassword = view.findViewById(R.id.tvChangePassword);
        iddangxuat = view.findViewById(R.id.iddangxuat);
        iddangxuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị hộp thoại xác nhận đăng xuất
                showLogoutConfirmationDialog();
            }
        });

        handler = new Handler(Looper.getMainLooper());
        dataUpdater = new Runnable() {
            @Override
            public void run() {
                // Do something to update data if needed
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(dataUpdater);

        // Lấy dữ liệu từ Bundle và hiển thị lên giao diện
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userIdValue = bundle.getString("userId");
            String userFullname = bundle.getString("userFullname");
            String userEmailValue = bundle.getString("userEmail");

            userId.setText(userIdValue);
            userName.setText(userFullname);
            userEmail.setText(userEmailValue);
        }

        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToChangePasswordFragment();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý đăng xuất ở đây, chẳng hạn gọi Intent để chuyển đến màn hình đăng nhập
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Người dùng chọn hủy, không thực hiện đăng xuất
                dialog.dismiss();
            }
        });
        builder.show();
    }


    // Phương thức để ẩn UserFragment
    private void hideUserFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(this);
        fragmentTransaction.commit();
    }

    // Phương thức để hiển thị lại UserFragment
    private void showUserFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(this);
        fragmentTransaction.commit();
    }

    // Phương thức để chuyển sang Fragment Đổi mật khẩu
    private void changeToChangePasswordFragment() {
        // Ẩn UserFragment
        hideUserFragment();

        // Tiến hành chuyển sang Fragment Đổi mật khẩu
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Tạo một instance của Fragment Đổi mật khẩu
        change_passwordFragment changePasswordFragment = change_passwordFragment.newInstance();

        // Tạo bundle để đính kèm dữ liệu
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId.getText().toString());
        bundle.putString("userFullname", userName.getText().toString());
        bundle.putString("userEmail", userEmail.getText().toString());

        // Gán bundle vào Fragment Đổi mật khẩu
        changePasswordFragment.setArguments(bundle);

        // Thực hiện thay thế Fragment hiện tại bằng Fragment Đổi mật khẩu và đưa Fragment cũ vào BackStack
        fragmentTransaction.replace(R.id.fragment_container, changePasswordFragment);
        fragmentTransaction.addToBackStack(null); // Thêm Fragment hiện tại vào BackStack
        // Submit thay đổi
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(dataUpdater);
    }
}
