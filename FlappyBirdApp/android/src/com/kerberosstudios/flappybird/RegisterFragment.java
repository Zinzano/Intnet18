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
 * The register user page of the app.
 */
public class RegisterFragment extends Fragment {

    View registerLayout;
    ArrayList<ProfileParameter> profileParameterArrayList;
    RegisterAdapter registerAdapter;
    MainActivity activity;
    ListView profileListView;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        registerLayout = inflater.inflate(R.layout.fragment_register, container, false);

        // Get a reference to the activity
        activity = (MainActivity) getActivity();

        // Make sure that the toolbar and side menu is disabled for this fragment
        activity.hideToolbarAndSideMenu();

        // Get the ListView from the current layout
        profileListView = registerLayout.findViewById(R.id.profileList);

        // Create a list that will hold the ProfileParameter objects
        profileParameterArrayList = new ArrayList<>();

        // Create and set a custom list adapter
        registerAdapter = new RegisterAdapter(getActivity().getApplicationContext(), profileParameterArrayList);
        profileListView.setAdapter(registerAdapter);

        // Add a onClickListener to the register user button
        // Will run createUser method when clicked
        Button registerUserButton = (Button)registerLayout.findViewById(R.id.registerUserButton);
        registerUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        // Create the profileParameter objects and add them to the list
        profileParameterArrayList.add( new ProfileParameter("", "E-mail: ", "mail", "email"));
        profileParameterArrayList.add( new ProfileParameter("", "Nickname: ", "default", "nickname"));
        profileParameterArrayList.add( new ProfileParameter("", "Password: ", "password", "password"));
        profileParameterArrayList.add( new ProfileParameter("", "First name: ", "default", "firstName"));
        profileParameterArrayList.add( new ProfileParameter("", "Last name: ", "default", "lastName"));
        profileParameterArrayList.add( new ProfileParameter("", "Age: ", "number", "age"));
        profileParameterArrayList.add( new ProfileParameter("", "Street name: ", "default", "street"));
        profileParameterArrayList.add( new ProfileParameter("", "Street number: ", "number", "streetNumber"));
        profileParameterArrayList.add( new ProfileParameter("", "City: ", "default", "city"));
        profileParameterArrayList.add( new ProfileParameter("", "Country: ", "default", "country"));

        return registerLayout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Method that will check, format and send the user parameters to the controller
     */
    public void createUser(){
        JSONObject profile = new JSONObject();
        Boolean requiredFieldsAreCorrect = true;
        for (int i = 0; i < profileListView.getCount(); i++) {
            try {
                ProfileParameter para = profileParameterArrayList.get(i);
                View v = profileListView.getChildAt(i);
                Log.i("parameter", para.getIdJSON());
                EditText et = (EditText) v.findViewById(R.id.profileParameter);
                para.setValue(et.getText().toString());
                int passwordLength = 2;

                // Different checks for email, nickname and password
                switch (para.getIdJSON()){
                    case "email":
                        String emailAddress = et.getText().toString().trim();
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                            et.setError("please enter valid email address");
                            et.requestFocus();
                            requiredFieldsAreCorrect = false;
                        } else if (et.getText().toString().equals("")) {
                            et.setError("please enter email address");
                            et.requestFocus();
                            requiredFieldsAreCorrect = false;
                        }
                        break;

                    case "nickname":
                        if (et.getText().toString().equals("")) {
                            et.setError("please enter a nickname");
                            et.requestFocus();
                            requiredFieldsAreCorrect = false;
                        }
                        break;

                    case "password":
                        if (et.getText().toString().length() < passwordLength) {
                            et.setError("password minimum contain " + passwordLength +" character");
                            et.requestFocus();
                            requiredFieldsAreCorrect = false;
                        }else if (et.getText().toString().equals("")) {
                            et.setError("please enter password");
                            et.requestFocus();
                            requiredFieldsAreCorrect = false;
                        }
                        break;
                    default:

                        if (et.getText().toString().equals("")) {
                            et.setError("please enter you information");
                            et.requestFocus();
                            requiredFieldsAreCorrect = false;
                        }

                        break;
                }


                profile.put(para.getIdJSON(), para.getValue());



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Only send data if the required fields are filled
        if (requiredFieldsAreCorrect){
            activity.createUser(profile);
        }
    }

    /**
     * Method that will display the errors if the response from the server is that the create failed
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
                            View v = profileListView.getChildAt(0);
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
                            View view = profileListView.getChildAt(1);
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
                                    "Unknown server error when creating user", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
