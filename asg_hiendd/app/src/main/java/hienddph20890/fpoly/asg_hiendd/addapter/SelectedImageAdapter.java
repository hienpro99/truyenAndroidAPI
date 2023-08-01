package hienddph20890.fpoly.asg_hiendd.addapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import hienddph20890.fpoly.asg_hiendd.R;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ImageViewHolder> {
    private List<String> selectedImages;
    private Context context;

    public SelectedImageAdapter(List<String> selectedImages) {
        this.selectedImages = selectedImages;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String imageUrl = selectedImages.get(position);

        // Sử dụng thư viện Picasso để tải hình ảnh từ URL và hiển thị lên ImageView
        Picasso.get().load(imageUrl).into(holder.imageView);
        // Bắt sự kiện xóa ảnh khi bấm nút "Delete"
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa URL ảnh khỏi danh sách đã chọn
                selectedImages.remove(position);
                // Cập nhật RecyclerView để hiển thị danh sách mới sau khi xóa
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, selectedImages.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedImages.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView,btnDelete;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSelectedImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
