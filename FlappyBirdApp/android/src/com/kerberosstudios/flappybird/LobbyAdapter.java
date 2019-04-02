package com.kerberosstudios.flappybird;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zinzano on 3/9/2018.
 * Custom ArrayAdapter for the lobby information
 */

public class LobbyAdapter extends ArrayAdapter<LobbyUser> {

    private Context mContext;
    private List<LobbyUser> currentUsers = new ArrayList<>();

    public LobbyAdapter(Context c, ArrayList<LobbyUser> list) {
        super(c, 0, list);
        mContext = c;
        currentUsers = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null)
            // Inflate the layout for this list item
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

        // Get the current LobbyUser object to access its data
        LobbyUser currentUser = currentUsers.get(position);

        // Get the TextView that holds the username and update its text
        TextView username = (TextView) listItem.findViewById(R.id.profileParaName);
        username.setText(currentUser.getName());

        // Get the TextView that holds the high score and update its text
        TextView highScore = (TextView) listItem.findViewById(R.id.textView_release);
        highScore.setText(currentUser.getCurrentScore());

        return listItem;
    }
}
