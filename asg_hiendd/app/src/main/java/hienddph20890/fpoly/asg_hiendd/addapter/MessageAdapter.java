package hienddph20890.fpoly.asg_hiendd.addapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hienddph20890.fpoly.asg_hiendd.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<String> messages;
    private List<Boolean> messageIsMine;

    public MessageAdapter(List<String> messages, List<Boolean> messageIsMine) {
        this.messages = messages;
        this.messageIsMine = messageIsMine;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = messages.get(position);
        boolean isMyMessage = messageIsMine.get(position);

        holder.tvMessage.setText(message);

        if (isMyMessage) {
            holder.tvMessage.setGravity(Gravity.END); // Tin nhắn của người gửi hiển thị bên phải
            holder.tvMessage.setBackgroundResource(R.drawable.comment_background);
        } else {
            holder.tvMessage.setGravity(Gravity.START); // Tin nhắn của người nhận hiển thị bên trái
            holder.tvMessage.setBackgroundResource(R.drawable.bg);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.messageText);
        }
    }
}
