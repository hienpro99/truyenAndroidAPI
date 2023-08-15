package hienddph20890.fpoly.asg_hiendd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import hienddph20890.fpoly.asg_hiendd.addapter.MessageAdapter;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    private Socket socket;
    private RecyclerView messageRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private List<String> messages;
    private List<Boolean> messageIsMine;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messages = new ArrayList<>();
        messageIsMine = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, messageIsMine);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(layoutManager);
        messageRecyclerView.setAdapter(messageAdapter);

        try {
            socket = IO.socket("http://10.24.54.45:3000");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on("receiveMessage", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String message = (String) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addMessage(message, false); // false indicates that it's not my message
                    }
                });
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageToSend = messageInput.getText().toString().trim();
                if (!messageToSend.isEmpty()) {
                    sendMessage(messageToSend);
                    addMessage(messageToSend, true); // true indicates that it's my message
                    messageInput.setText("");
                }
            }
        });
    }

    private void addMessage(String message, boolean isMyMessage) {
        messages.add(message);
        messageIsMine.add(isMyMessage);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        messageRecyclerView.smoothScrollToPosition(messages.size() - 1);
    }

    private void sendMessage(String message) {
        socket.emit("sendMessage", message);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.disconnect();
        }
    }
}
