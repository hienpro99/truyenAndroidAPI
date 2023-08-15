package hienddph20890.fpoly.asg_hiendd.addapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.fragment.SearchFragment;
import hienddph20890.fpoly.asg_hiendd.model.Truyen;

public class TruyenAdapter extends RecyclerView.Adapter<TruyenAdapter.ViewHolder> {

    private List<Truyen> productList;
    private OnItemClickListener listener;

    private Context context;

    private boolean isAdmin; // Thêm biến isAdmin để lưu giá trị từ Fragment

    // Cập nhật phương thức khởi tạo của Adapter để nhận giá trị isAdmin từ Fragment
    public TruyenAdapter(Context context, List<Truyen> productList, boolean isAdmin) {
        this.context = context;
        this.productList = productList;
        this.isAdmin = isAdmin;
    }
    // Phương thức này dùng để cập nhật danh sách truyện được tìm kiếm trên Adapter
    public void updateData(List<Truyen> newData) {
        productList = newData;
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {
        void onItemClick(Truyen truyen);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public TruyenAdapter(List<Truyen> productList) {
        this.productList = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Truyen truyen = productList.get(position);
        holder.tvname.setText("Tên Truyện: " + truyen.getName());
        holder.tvauthor.setText("Tác Giả: "+truyen.getAuthor());
        holder.tvyear.setText("Năm xuất bản: "+String.valueOf(truyen.getYearPublished())); // Chuyển đổi sang chuỗi nếu gia là số

        // Sử dụng Picasso hoặc Glide để tải và hiển thị hình ảnh từ URL hoặc tên tệp
        Picasso.get().load(truyen.getCoverImage()).into(holder.ivHinhAnh);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    Log.d("TruyenAdapter", "Truyen Name: " + truyen.getName());
                    onItemClickListener.onItemClick(truyen); // Truyền đối tượng Truyen khi item được nhấp vào
                }
            }
        });
        if(isAdmin){
            holder.imgdel.setVisibility(View.VISIBLE);
        }else {
            holder.imgdel.setEnabled(false);
            holder.imgdel.setVisibility(View.INVISIBLE);
        }
        holder.imgdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAdmin){
                    holder.imgdel.setVisibility(View.VISIBLE);
                    showDeleteConfirmationDialog(truyen);
                }else {
                    holder.imgdel.setEnabled(false);
                    holder.imgdel.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    private void showDeleteConfirmationDialog(Truyen truyen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa truyện");
        builder.setMessage("Bạn có chắc chắn muốn xóa truyện \"" + truyen.getName() + "\" không?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteComic(truyen.getId());
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


    private void deleteComic(String comicId) {
        int position = -1;
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(comicId)) {
                position = i;
                break;
            }
        }

        // Nếu tìm thấy truyện, xóa truyện khỏi danh sách productList
        if (position != -1) {
            productList.remove(position);
            // Thông báo cho RecyclerView biết đã có thay đổi trong dataset
            notifyDataSetChanged();
        }
        // Đường dẫn API để xóa truyện
        String url = "http://10.24.54.45:3000/comics/" + comicId;

        // Tạo một RequestQueue bằng Volley
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Tạo một JsonObjectRequest với phương thức DELETE
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Lỗi"+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvname;
        TextView tvauthor;
        TextView tvyear;
        ImageView ivHinhAnh, imgdel;

        public ViewHolder(View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.tvname);
            tvauthor = itemView.findViewById(R.id.tvauthor);
            tvyear = itemView.findViewById(R.id.tvyear);
            ivHinhAnh = itemView.findViewById(R.id.ivHinhAnh);
            imgdel = itemView.findViewById(R.id.imgdel);
        }
    }
}
