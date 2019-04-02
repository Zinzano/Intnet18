package com.kerberosstudios.flappybird;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * The chat room page of the app.
 */
public class ChatRoomFragment extends Fragment {

    MainActivity activity;
    MessageAdapter messageAdapter;
    ArrayList<Message> messagesList;
    EditText inputMessage;
    ListView messagesListView;

    public ChatRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View chatRoomLayout = inflater.inflate(R.layout.fragment_chat_room, container, false);

        // Get the ListView from the current layout
        messagesListView = chatRoomLayout.findViewById(R.id.messageList);

        // Create a list that will hold the Message objects
        messagesList = new ArrayList<>();

        // Create and set a custom list adapter
        messageAdapter = new MessageAdapter(getActivity().getApplicationContext(), messagesList);
        messagesListView.setAdapter(messageAdapter);

        // Get a reference to the EditText where the user enter the message
        inputMessage = (EditText)chatRoomLayout.findViewById(R.id.inputMessage);

        // Add a onClickListener to the send button
        // Will run sendMessage method when clicked
        Button sendMessageButton = (Button)chatRoomLayout.findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Get a reference to the activity
        activity = (MainActivity) getActivity();

        // Call controller method that will inform the server that the user has joined the chat room
        activity.joinChatroom();

        return chatRoomLayout;
    }

    @Override
    public void onDestroy() {
        activity.leaveChatroom();
        super.onDestroy();
    }

    /**
     * Method that is run when the user is accepted into the chatroom.
     * Will get all the messages from the chat room.
     * @param messages
     */
    public void populateChatroom(JSONArray messages){
        Log.d("arr", messages.toString());
        for (int i=0; i < messages.length(); i++) {
            try {

                JSONObject messageObject = messages.getJSONObject(i);
                String sender = messageObject.getString("user");
                String message = messageObject.getString("message");
                messagesList.add( new Message(message, sender));

                Log.d("messageObject", messageObject.toString());
                Log.d("nickname", sender);
                Log.d("Message", message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        updateListView();
    }

    /**
     * Method that force the listView to be visually updated
     */
    public void updateListView(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageAdapter.notifyDataSetChanged();
                messagesListView.setSelection(messagesListView.getCount() - 1 );
            }
        });
    }

    /**
     * Will get the inputted test in the EditText and send it to the server.
     * Will only send information if there is a message to send.
     */
    public void sendMessage(){
        if (inputMessage.length() != 0) {
            activity.sendChatMessage(inputMessage.getText().toString());
        } else {
            inputMessage.setError("please enter a message");
            inputMessage.requestFocus();
        }
    }

    /**
     * Get a message from the controller and add it to the messageList and the update the ListView
     * @param message
     */
    public void addMessage(JSONObject message){
        try {
            messagesList.add( new Message(message.getString("message"), message.getString("user")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateListView();
    }
}
