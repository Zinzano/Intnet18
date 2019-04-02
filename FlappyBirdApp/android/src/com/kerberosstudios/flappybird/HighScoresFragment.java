package com.kerberosstudios.flappybird;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * The high scores page of the app.
 */
public class HighScoresFragment extends Fragment {

    MainActivity activity;
    HighScoreAdapter highScoreAdapter;
    ArrayList<HighScoreUser> highScoresList;

    public HighScoresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View highScoresLayout = inflater.inflate(R.layout.fragment_high_scores, container, false);

        // Get the ListView from the current layout
        ListView highScoresListView = highScoresLayout.findViewById(R.id.list);

        // Create a list that will hold the HighScoreUser objects
        highScoresList = new ArrayList<>();

        // Create and set a custom list adapter
        highScoreAdapter = new HighScoreAdapter(getActivity().getApplicationContext(), highScoresList);
        highScoresListView.setAdapter(highScoreAdapter);

        // Get a reference to the activity
        activity = (MainActivity) getActivity();

        // Tell the controller to get the high score data from the server
        activity.callForHighScoreData();

        return highScoresLayout;
    }

    /**
     * Method that is run when the user is accepted into the high scores room.
     * Will get all the high scores data from controller.
     * @param highScores
     */
    public void populateHighScoresList(JSONArray highScores){
        Log.d("highScores", highScores.toString());
        for (int i=0; i < highScores.length(); i++) {
            try {
                JSONObject user = highScores.getJSONObject(i);
                String score = user.getString("score");
                String date = user.getString("date");
                JSONObject userId = user.getJSONObject("userId");
                String nickname = userId.getString("nickname");

                Log.d("user", user.toString());
                Log.d("userId", userId.toString());
                Log.d("nickname", nickname);
                Log.d("score", score);
                Log.d("date", date);

                highScoresList.add( new HighScoreUser(nickname, score, date));
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
                highScoreAdapter.notifyDataSetChanged();
            }
        });
    }
}
