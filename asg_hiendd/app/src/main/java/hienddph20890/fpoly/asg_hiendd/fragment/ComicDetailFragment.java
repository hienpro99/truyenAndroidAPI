package hienddph20890.fpoly.asg_hiendd.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import hienddph20890.fpoly.asg_hiendd.MainActivity;
import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.addapter.CommentAdapter;
import hienddph20890.fpoly.asg_hiendd.model.Comment;
import hienddph20890.fpoly.asg_hiendd.model.Truyen;

import java.util.ArrayList;
import java.util.List;

public class ComicDetailFragment extends Fragment {
    private TextView tvName, tvDescription, tvAuthor, tvYearPublished;
    private ImageView imgcoverImage;
    private ListView commentListView;
    private EditText etComment;
    private Button btnPostComment, btnViewTruyen;
    private Truyen selectedComic;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private String comicId;
    private boolean isViewPagerVisible = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comic_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.tvName);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        tvYearPublished = view.findViewById(R.id.tvYearPublished);
        imgcoverImage = view.findViewById(R.id.imgcoverImage);
        commentListView = view.findViewById(R.id.commentListView);
        etComment = view.findViewById(R.id.etComment);
        btnPostComment = view.findViewById(R.id.btnPostComment);
        btnViewTruyen = view.findViewById(R.id.btnViewTruyen);
        Toast.makeText(getContext(), "Đang ở chi tiết Truyện", Toast.LENGTH_SHORT).show();
        commentList = new ArrayList<>();
        // Khởi tạo Adapter cho ListView
        commentAdapter = new CommentAdapter(requireContext(), commentList);

        // Set Adapter cho ListView
        commentListView.setAdapter(commentAdapter);

        // Retrieve the selected Truyen object from arguments
        selectedComic = (Truyen) getArguments().getSerializable("truyen");
        comicId = getArguments().getString("comicId");
        Toast.makeText(getContext(), comicId, Toast.LENGTH_SHORT).show();
        if (comicId != null) {
            // Tạo URL từ comicId
            String URL_GET_COMIC_DETAIL = "http://192.168.1.8:3000/comics/" + comicId;
            // Gọi phương thức fetch dữ liệu từ URL
            fetchComicDetails(URL_GET_COMIC_DETAIL);
        }

        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the comment content from the EditText
                String commentContent = etComment.getText().toString().trim();

                // Check if the comment content is not empty
                if (TextUtils.isEmpty(commentContent)) {
                    Toast.makeText(getContext(), "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


        btnViewTruyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the list of comic images from the selectedComic object
                ArrayList<String> comicImages = (ArrayList<String>) selectedComic.getImages();

                // Create a new Fragment to display the list of comic images
                ComicImagesFragment comicImagesFragment = new ComicImagesFragment();

                // Replace the current fragment with the new fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Pass the list of comic images to ComicImagesFragment through arguments
                Bundle args = new Bundle();
                args.putStringArrayList("comicImages", comicImages);
                comicImagesFragment.setArguments(args);

                fragmentTransaction.replace(R.id.fragment_container, comicImagesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    private void fetchComicDetails(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý dữ liệu chi tiết truyện từ response
                        try {
                            String name = response.getString("name");
                            String description = response.getString("description");
                            int yearPublished = response.optInt("yearPublished", 0);
                            String author = response.getString("author");
                            String coverImage = response.getString("coverImage");
                            selectedComic = new Truyen(name, description, author);
                            selectedComic.setCoverImage(coverImage);

                            // Parse the list of image URLs from the JSON response
                            JSONArray imagesArray = response.getJSONArray("images");
                            ArrayList<String> imagesList = new ArrayList<>();
                            for (int i = 0; i < imagesArray.length(); i++) {
                                String imageUrl = imagesArray.getString(i);
                                imagesList.add(imageUrl);
                            }
                            // Set the list of image URLs to the selectedComic object
                            selectedComic.setImages(imagesList);

                            // Hiển thị thông tin truyện lên giao diện
                            tvName.setText("Tên Truyện: " + name);
                            tvDescription.setText("Mô Tả: " + description);
                            tvAuthor.setText("Tác Giả: " + author);
                            tvYearPublished.setText("Năm Xuất Bản: " + String.valueOf(yearPublished));
                            // Load and display cover image using Picasso
                            Picasso.get().load(coverImage).into(imgcoverImage);
                            // Gọi hàm để lấy danh sách bình luận
                            fetchComicComments(comicId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing comic detail", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error fetching comic detail", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(objectRequest);
    }

    private void fetchComicComments(String comicId) {
        // Gọi API lấy danh sách bình luận theo ID truyện
        String URL_GET_COMIC_COMMENTS = "http://192.168.1.8:3000/comments/" + comicId;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, URL_GET_COMIC_COMMENTS, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Xóa danh sách bình luận cũ
                            commentList.clear();

                            // Thêm các bình luận mới vào danh sách
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject commentObject = response.getJSONObject(i);
                                String fullname = commentObject.getString("fullname");
                                String content = commentObject.getString("content");
                                String createdAt = commentObject.getString("createdAt");
                                Comment comment = new Comment(fullname, content, createdAt);
                                commentList.add(comment);
                            }

                            // Cập nhật dữ liệu mới và hiển thị lên ListView
                            commentAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing comic comments", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error fetching comic comments", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(arrayRequest);
    }
}
