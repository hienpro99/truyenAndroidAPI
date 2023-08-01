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
import org.json.JSONException;
import org.json.JSONObject;

import hienddph20890.fpoly.asg_hiendd.MainActivity;
import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.addapter.CommentAdapter;
import hienddph20890.fpoly.asg_hiendd.model.Comment;
import hienddph20890.fpoly.asg_hiendd.model.Truyen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        Boolean isAdmin = getArguments().getBoolean("isAdmin");
        String userId = getArguments().getString("userId");
        selectedComic = (Truyen) getArguments().getSerializable("truyen");
        comicId = getArguments().getString("comicId");
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
        commentAdapter = new CommentAdapter(requireContext(), commentList,isAdmin,comicId,userId);
        ///bắt sự kiện để sửa comment
        commentAdapter.setOnCommentEditListener(new CommentAdapter.OnCommentEditListener() {
            @Override
            public void onCommentEdit(Comment comment) {
                Bundle bundle = getArguments();
                String userId = bundle.getString("userId");
                if (userId.equals(comment.getUserId())) {
                    etComment.setText(comment.getContent());
                    btnPostComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String updatedContent = etComment.getText().toString().trim();
                            if (TextUtils.isEmpty(updatedContent)) {
                                Toast.makeText(getContext(), "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            editComment(comicId, comment.get_id(), userId, updatedContent);
                        }
                    });
                } else {
                    // If user IDs don't match, show an error message or handle the restriction as needed
                    Toast.makeText(getContext(), "Bạn không có quyền chỉnh sửa bình luận này", Toast.LENGTH_SHORT).show();
                }


            }
        });
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

                // Lấy dữ liệu từ Bundle
                Bundle bundle = getArguments();
                if (bundle != null) {
                    String userId = bundle.getString("userId");
                    String userFullname = bundle.getString("userFullname");
                    Boolean isAdmin = getArguments().getBoolean("isAdmin"); // Giá trị mặc định nếu không có giá trị "isAdmin" trong Intent
                    comicId = getArguments().getString("comicId"); // Lấy id truyện từ selectedComic (nếu có)
                    Toast.makeText(getContext(), userId+"Với"+userFullname+"với"+comicId+""+isAdmin, Toast.LENGTH_SHORT).show();
                    // Kiểm tra xem đã có id truyện chưa
                    if (TextUtils.isEmpty(comicId)) {
                        Toast.makeText(getContext(), "Không có ID truyện", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Tạo JSON object để lưu thông tin bình luận
                    JSONObject commentJson = new JSONObject();
                    try {
                        commentJson.put("userId", userId);
                        commentJson.put("fullname", userFullname);
                        commentJson.put("content", commentContent);
                        // Để có createdAt theo ngày giờ hiện tại, bạn có thể sử dụng một thư viện để đơn giản hóa việc xử lý ngày tháng
                        // Ví dụ sử dụng thư viện SimpleDateFormat để lấy ngày giờ hiện tại
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                        String createdAt = sdf.format(new Date());
                        commentJson.put("createdAt", createdAt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error creating comment JSON", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Tạo một request POST lên server để thêm bình luận
                    String URL_POST_COMMENT = "http://192.168.1.8:3000/comics/" + comicId + "/comment";
                    RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_POST_COMMENT, commentJson,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Bình luận được thêm thành công, cập nhật lại danh sách bình luận
                                    Toast.makeText(getContext(), "Thêm bình luận thành công", Toast.LENGTH_SHORT).show();
                                    etComment.setText(""); // Xóa nội dung bình luận trong EditText sau khi gửi thành công
                                    fetchComicComments(comicId); // Gọi lại phương thức để lấy danh sách bình luận mới
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), "Error posting comment" + error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    // Thêm request vào hàng đợi
                    requestQueue.add(request);
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
    private void editComment(String comicId, String commentId, String userId, String newContent) {
        // Tạo JSON object để lưu thông tin chỉnh sửa bình luận
        JSONObject commentJson = new JSONObject();
        try {
            commentJson.put("userId", userId);
            commentJson.put("content", newContent);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error creating comment JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo URL từ comicId và commentId
        String URL_EDIT_COMMENT = "http://192.168.1.8:3000/comment/" + comicId + "/" + commentId;
        // Tạo một request PUT lên server để chỉnh sửa bình luận
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, URL_EDIT_COMMENT, commentJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Bình luận được chỉnh sửa thành công, cập nhật lại danh sách bình luận
                        Toast.makeText(getContext(), "Chỉnh sửa bình luận thành công", Toast.LENGTH_SHORT).show();
                        etComment.setText("");
                        // Gọi lại phương thức để lấy danh sách bình luận mới
                        fetchComicComments(comicId);
                        // Gọi lại Fragment để load lại dữ liệu
                        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                        ft.detach(ComicDetailFragment.this).attach(ComicDetailFragment.this).commit();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error editing comment" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Thêm request vào hàng đợi
        requestQueue.add(request);
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
                                String userId = commentObject.getString("userId");
                                String id = commentObject.getString("_id");
                                String fullname = commentObject.getString("fullname");
                                String content = commentObject.getString("content");
                                String createdAt = commentObject.getString("createdAt");
                                Comment comment = new Comment(userId,fullname, content,id, createdAt);
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
