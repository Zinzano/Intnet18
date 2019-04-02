package com.kerberosstudios.flappybird;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
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
 * The lobby page of the app.
 */
public class LobbyFragment extends Fragment {

    ArrayList<LobbyUser> usersInLobby;
    LobbyAdapter lobbyAdapter;
    MainActivity activity;

    public LobbyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View lobbyLayout = inflater.inflate(R.layout.fragment_lobby, container, false);

        // Get the ListView from the current layout
        ListView usersInLobbyListView = lobbyLayout.findViewById(R.id.lobbyUsersList);

        // Create a list that will hold the LobbyUser objects
        usersInLobby = new ArrayList<>();

        // Get a reference to the activity
        activity = (MainActivity) getActivity();

        // Tell the controller to get the lobby data from the server
        activity.callForLobbyData();

        // Make sure that the toolbar and side menu is enabled for this fragment
        activity.showToolbarAndSideMenu();

        // Create and set a custom list adapter
        lobbyAdapter = new LobbyAdapter(getActivity().getApplicationContext(), usersInLobby);
        usersInLobbyListView.setAdapter(lobbyAdapter);

        return lobbyLayout;
    }

    /**
     * Method that is run when the user is accepted into the lobby.
     * Will get all the lobby data from controller.
     * @param users
     */
    public void populateLobby(JSONArray users){
        for (int i=0; i < users.length(); i++) {
            try {
                JSONObject user = users.getJSONObject(i);
                usersInLobby.add( new LobbyUser(user.getString("nickname"), user.getString("score")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        updateListView();
    }

    /**
     * Method that is called when a user leave the room.
     * The event is called by the server and then the controller will call this method
     * @param nickname
     */
    public void removeUserFromLobby(String nickname){
        Log.i("LobbyFragmentTryRemoveUser", nickname);
        int userIndex = -1;
        for (int i = 0; i < usersInLobby.size(); i++) {
            LobbyUser user = usersInLobby.get(i);
            if (user.getName().equals(nickname)){
                userIndex = i;
            }
        }
        if (userIndex >= 0) {
            usersInLobby.remove(userIndex);
            Log.i("LobbyFragmentRemovedUser", nickname);
        }
        updateListView();
    }

    /**
     * Add a user to the usersInLobby list.
     * @param user
     */
    public void addUserToLobby(JSONObject user){
        try {
            usersInLobby.add( new LobbyUser(user.getString("nickname"), user.getString("score")));
        } catch (JSONException e) {
            e.printStackTrace();
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
                lobbyAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroy() {
        activity.leaveLobby();
        super.onDestroy();
    }
}
