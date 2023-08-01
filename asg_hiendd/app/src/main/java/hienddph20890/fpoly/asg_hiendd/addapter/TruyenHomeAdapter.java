package hienddph20890.fpoly.asg_hiendd.addapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.model.Truyen;

public class TruyenHomeAdapter extends RecyclerView.Adapter<TruyenHomeAdapter.ViewHolder> {

    private List<Truyen> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Truyen truyen);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public TruyenHomeAdapter(List<Truyen> productList) {
        this.productList = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_truyen_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Truyen truyen = productList.get(position);
        holder.tvTenTruyen.setText("Truyện: "+truyen.getName());
        holder.tvTacGia.setText("Tác Giả: "+truyen.getAuthor());

        // Sử dụng Picasso hoặc Glide để tải và hiển thị hình ảnh từ URL hoặc tên tệp
        Picasso.get().load(truyen.getCoverImage()).into(holder.ivHinhAnh);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    Log.d("TruyenHomeAdapter", "Truyen Name: " + truyen.getName());
                    onItemClickListener.onItemClick(truyen); // Truyền đối tượng Truyen khi item được nhấp vào
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh;
        TextView tvTenTruyen;
        TextView tvTacGia;

        public ViewHolder(View itemView) {
            super(itemView);
            ivHinhAnh = itemView.findViewById(R.id.ivHinhAnh);
            tvTenTruyen = itemView.findViewById(R.id.tvTenTruyen);
            tvTacGia = itemView.findViewById(R.id.tvTacGia);
        }
    }
}
