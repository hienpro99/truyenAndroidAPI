package hienddph20890.fpoly.asg_hiendd.addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import hienddph20890.fpoly.asg_hiendd.R;
import hienddph20890.fpoly.asg_hiendd.model.Comment;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> commentList;


    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
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

        return convertView;
    }

    private static class ViewHolder {
        TextView tvFullname, tvContent, tvCreatedAt;
    }
}
