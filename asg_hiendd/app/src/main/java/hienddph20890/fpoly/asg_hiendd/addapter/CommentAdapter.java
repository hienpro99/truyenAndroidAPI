package hienddph20890.fpoly.asg_hiendd.addapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

import java.util.List;

import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.model.Comment;
import hienddph20890.fpoly.asg_hiendd.model.Truyen;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> commentList;
    private OnCommentEditListener commentEditListener;
    private boolean isAdmin;
    private String comicId, userId;
    public interface OnCommentEditListener {
        void onCommentEdit(Comment comment);
    }
    public void setOnCommentEditListener(OnCommentEditListener listener) {
        this.commentEditListener = listener;
    }




    public CommentAdapter(Context context, List<Comment> commentList,boolean isAdmin,String comicId,String userId) {
        this.context = context;
        this.commentList = commentList;
        this.isAdmin = isAdmin;
        this.comicId =comicId;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvFullname = convertView.findViewById(R.id.tvFullname);
            viewHolder.tvContent = convertView.findViewById(R.id.tvContent);
            viewHolder.tvCreatedAt = convertView.findViewById(R.id.tvCreatedAt);
            viewHolder.imgdel = convertView.findViewById(R.id.imgdel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Lấy đối tượng Comment tại vị trí position
        Comment comment = commentList.get(position);

        // Hiển thị thông tin bình luận lên giao diện
        viewHolder.tvFullname.setText("Người dùng: "+comment.getFullname());
        viewHolder.tvContent.setText("Đã Bình Luận: "+comment.getContent());
        viewHolder.tvCreatedAt.setText("Vào Lúc: "+comment.getCreatedAt());
// Bắt sự kiện chỉnh sửa comment khi người dùng click vào item
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentEditListener != null) {
                    // Gọi sự kiện chỉnh sửa comment và truyền comment hiện tại
                    commentEditListener.onCommentEdit(comment);
                }
            }
        });
        viewHolder.imgdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (isAdmin || comment.getUserId().equals(userId)){
                   viewHolder.imgdel.setVisibility(View.VISIBLE);
                   showDeleteConfirmationDialog(comment);
               }else {
                   viewHolder.imgdel.setVisibility(View.INVISIBLE);
                   Toast.makeText(context, "Bạn không có quyền xóa bình luận này", Toast.LENGTH_SHORT).show();
               }
            }
        });
        return convertView;
    }
    private void showDeleteConfirmationDialog(Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa truyện");
        builder.setMessage("Bạn có chắc chắn muốn xóa không?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteComment(comicId, comment.get_id());
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
    private void deleteComment(String comicId, String commentId) {
        int position = -1;
        for (int i = 0; i < commentList.size(); i++) {
            if (commentList.get(i).get_id().equals(commentId)) {
                position = i;
                break;
            }
        }

        if (position != -1) {
            commentList.remove(position);
            notifyDataSetChanged();
        }
        String url = "http://192.168.1.8:3000/comment/" + comicId + "/" + commentId;

        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Xóa Thành công", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error response (failed to delete comment)
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 403) {
                                Toast.makeText(context, "Bạn không có quyền xóa bình luận này", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error deleting comment"+error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(deleteRequest);
    }

    private static class ViewHolder {
        TextView tvFullname, tvContent, tvCreatedAt;
        ImageView imgdel;
    }
}
