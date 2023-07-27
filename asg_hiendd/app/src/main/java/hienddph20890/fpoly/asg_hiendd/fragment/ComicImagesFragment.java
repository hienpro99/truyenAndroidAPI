package hienddph20890.fpoly.asg_hiendd.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import hienddph20890.fpoly.asg_hiendd.R;

public class ComicImagesFragment extends Fragment {

    private ArrayList<String> comicImages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comic_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy danh sách các ảnh truyện từ đối số
        Bundle args = getArguments();
        if (args != null) {
            comicImages = args.getStringArrayList("comicImages");
        }

        // Sử dụng RecyclerView để hiển thị danh sách ảnh truyện dọc
        RecyclerView rvComicImages = view.findViewById(R.id.rvComicImages);
        ComicImagesAdapter comicImagesAdapter = new ComicImagesAdapter(comicImages);
        rvComicImages.setAdapter(comicImagesAdapter);
        rvComicImages.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
    }

    // Tạo một adapter tùy chỉnh cho RecyclerView
    private class ComicImagesAdapter extends RecyclerView.Adapter<ComicImagesAdapter.ViewHolder> {
        private ArrayList<String> comicImages;

        public ComicImagesAdapter(ArrayList<String> comicImages) {
            this.comicImages = comicImages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comic_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Tải và hiển thị ảnh truyện bằng Picasso
            Picasso.get().load(comicImages.get(position)).into(holder.imgComic);
        }

        @Override
        public int getItemCount() {
            return comicImages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgComic;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgComic = itemView.findViewById(R.id.imgComic);
            }
        }
    }
}
