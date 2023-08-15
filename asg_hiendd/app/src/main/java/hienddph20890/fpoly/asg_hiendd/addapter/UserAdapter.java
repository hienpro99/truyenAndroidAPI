package hienddph20890.fpoly.asg_hiendd.addapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.model.Truyen;
import hienddph20890.fpoly.asg_hiendd.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    public UserAdapter(Context context, List<User> userList ) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUserId.setText("User ID: " + user.getUserId());
        holder.tvUsername.setText("Username: " + user.getUsername());
        holder.tvFullname.setText("Fullname: " + user.getFullname());
        holder.tvEmail.setText("Email: "+user.getEmail());
        holder.tIsAdmin.setText("Admin: "+user.getAdmin());
        if (user.getAdmin()){
            holder.imageView.setEnabled(false);
            holder.imageView.setVisibility(View.INVISIBLE);
        }else {
            holder.imageView.setVisibility(View.VISIBLE);
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateConfirmationDialog(user);
            }
        });
    }
    public void updateUserData(List<User> updatedUserList) {
        userList.clear();
        userList.addAll(updatedUserList);
        notifyDataSetChanged();
    }
    private void showUpdateConfirmationDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Phân Quyền");
        builder.setMessage("Bạn có chắc chắn muốn Thăng Cấp cho \"" + user.getFullname() + "\" không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateUserPermission(user.getUserId());
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void updateUserPermission(String userId) {
        String url = "http://10.24.54.45:3000/PhanQuyens/" + userId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("isAdmin", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Tạo một PUT request để phân quyền người dùng thành admin
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                        // Cập nhật lại dữ liệu người dùng sau khi đã thăng cấp thành công
                        List<User> updatedUserList = new ArrayList<>(userList);
                        for (User user : updatedUserList) {
                            if (user.getUserId().equals(userId)) {
                                user.setAdmin(true); // Cập nhật trạng thái quản trị viên cho người dùng
                                break;
                            }
                        }
                        updateUserData(updatedUserList); // Cập nhật lại dữ liệu cho adapter
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Lỗi "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Thêm request vào hàng đợi và gửi đi
        requestQueue.add(request);
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvUsername, tvFullname,tIsAdmin,tvEmail;
        ImageView imageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvFullname = itemView.findViewById(R.id.tvFullname);
            imageView = itemView.findViewById(R.id.imgupdate);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tIsAdmin = itemView.findViewById(R.id.tIsAdmin);
        }
    }
}
