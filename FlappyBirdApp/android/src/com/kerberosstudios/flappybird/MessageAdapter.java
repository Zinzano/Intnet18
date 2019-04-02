package com.kerberosstudios.flappybird;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zinzano on 3/14/2018.
 * Custom ArrayAdapter for the message information
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context mContext;
    private List<Message> messagesList = new ArrayList<>();


    public MessageAdapter(Context c, ArrayList<Message> list) {
        super(c, 0, list);
        mContext = c;
        messagesList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null)
            // Inflate the layout for this list item
            listItem = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item,parent,false);

        // Get the current Message object to access its data
        Message currentMessage = messagesList.get(position);

        // Get the TextView that holds the username and update its text
        TextView usernameTextView = (TextView) listItem.findViewById(R.id.profileParaName);
        usernameTextView.setText(currentMessage.getUsername());

        // Get the TextView that holds the message and update its text
        TextView messageTextView = (TextView) listItem.findViewById(R.id.messageText);
        String message = currentMessage.getMessage();
        messageTextView.setText(message);

        // Create a hash int from the username
        int hash = currentMessage.getUsername().hashCode()%13421772;

        // Convert the has to a String
        String hex = Integer.toHexString(hash);

        // Update the color of the message
        messageTextView.setTextColor(Color.parseColor('#'+hex));

        Log.d("Current user", currentMessage.getUsername());
        Log.d("Current user", Integer.toString(hash));
        Log.d("Color", hex);

        return listItem;
    }
}