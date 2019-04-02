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
 * Created by Zinzano on 3/11/2018.
 * Custom ArrayAdapter for the high score information
 */

public class HighScoreAdapter extends ArrayAdapter<HighScoreUser> {

    private Context mContext;
    private List<HighScoreUser> highScoreList = new ArrayList<>();


    public HighScoreAdapter(Context c, ArrayList<HighScoreUser> list) {
        super(c, 0, list);
        mContext = c;
        highScoreList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            // Inflate the layout for this list item
            listItem = LayoutInflater.from(mContext).inflate(R.layout.high_score_list_item,parent,false);

        // Get the current ProfileParameter object to access its data
        HighScoreUser currentHighScore = highScoreList.get(position);

        // Get the TextView that holds the username and update its text
        TextView username = (TextView) listItem.findViewById(R.id.profileParaName);
        username.setText(currentHighScore.getName());

        // Get the TextView that holds the value and update its text
        TextView highScoreValue = (TextView) listItem.findViewById(R.id.textView_release);
        highScoreValue.setText(currentHighScore.getHighScore());

        // Get the TextView that holds the position and update its text
        TextView pos = (TextView) listItem.findViewById(R.id.position);
        pos.setText(String.valueOf(position + 1));

        return listItem;
    }
}
