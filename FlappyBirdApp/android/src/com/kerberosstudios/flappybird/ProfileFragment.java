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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * The profile page of the app.
 */
public class ProfileFragment extends Fragment {

    View profileLayout;
    ArrayList<ProfileParameter> profileParameters;
    ProfileAdapter profileAdapter;
    MainActivity activity;
    ListView profileParameterListView;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        profileLayout = inflater.inflate(R.layout.fragment_profile2, container, false);

        // Get the ListView from the current layout
        profileParameterListView = profileLayout.findViewById(R.id.profileList);

        // Create a list that will hold the ProfileParameter objects
        profileParameters = new ArrayList<>();

        // Create and set a custom list adapter
        profileAdapter = new ProfileAdapter(getActivity().getApplicationContext(), profileParameters);
        profileParameterListView.setAdapter(profileAdapter);

        // Add a onClickListener to the save button
        // Will run saveProfile method when clicked
        Button saveProfileButton = (Button)profileLayout.findViewById(R.id.saveProfileButton);
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     saveProfile();
                                                 }
                                             });
        // Get a reference to the activity
        activity = (MainActivity) getActivity();

        // Tell the controller to get the user data from the server
        activity.callForProfileData();

        // Activate the toolbar and side menu
        activity.showToolbarAndSideMenu();

        return profileLayout;
    }

    /**
     * Method that will add the provided data as profile parameters to the ListView.
     * @param userData
     */
    public void setProfileInfo(JSONObject userData){
        try {
            JSONObject user = userData.getJSONObject("user");

            profileParameters.add( new ProfileParameter(user.getString("firstName"), "First name: ", "default", "firstName"));
            profileParameters.add( new ProfileParameter(user.getString("lastName"), "Last name: ", "default", "lastName"));
            profileParameters.add( new ProfileParameter(user.getString("age"), "Age: ", "number", "age"));
            profileParameters.add( new ProfileParameter(user.getString("nickname"), "Nickname: ", "default", "nickname"));
            profileParameters.add( new ProfileParameter(user.getString("email"), "E-mail: ", "mail", "email"));
            profileParameters.add( new ProfileParameter(user.getString("street"), "Street name: ", "default", "street"));
            profileParameters.add( new ProfileParameter(user.getString("streetNumber"), "Street number: ", "number", "streetNumber"));
            profileParameters.add( new ProfileParameter(user.getString("city"), "City: ", "default", "city"));
            profileParameters.add( new ProfileParameter(user.getString("country"), "Country: ", "default", "country"));
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
                profileAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Method that check which parameters that the user wants to update and then prepare the JSON object.
     * Calls method in activity that sends the data to the server
     */
    public void saveProfile(){

        JSONObject profile = new JSONObject();
        Boolean haveChanges = false;

        for (ProfileParameter parameter: profileParameters) {
            if (parameter.getUpdated()){

                try {
                    profile.put(parameter.getIdJSON(), parameter.getValue());
                    haveChanges = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (haveChanges){
            activity.saveProfile(profile);
        }
    }

    /**
     * Method that will display the errors if the response from the server is that the update failed
     * @param data
     */
    public void displayErrorFromServer(JSONObject data){

        try {
            String error = data.getString("newUser");
            switch (error){
                case "Email exists":
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View v = profileParameterListView.getChildAt(0);
                            EditText etMail = (EditText) v.findViewById(R.id.profileParameter);
                            etMail.setError("please enter a unique email address");
                            etMail.requestFocus();
                        }
                    });
                    break;

                case "Nickname exists":
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View view = profileParameterListView.getChildAt(1);
                            EditText etNickname = (EditText) view.findViewById(R.id.profileParameter);
                            etNickname.setError("Nickname already taken");
                            etNickname.requestFocus();
                        }
                    });
                    break;

                default:
                    Log.e("Register Fragment", "unknown server error when creating user");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    "Unknown server error when updating user", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
