package com.kerberosstudios.flappybird;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * The login page of the app.
 */
public class LoginFragment extends Fragment {

    View loginLayout;
    MainActivity activity;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loginLayout = inflater.inflate(R.layout.fragment_login, container, false);



        // Get a reference to the activity
        activity = (MainActivity) getActivity();

        // Make sure that the toolbar and side menu is disabled for this fragment
        activity.hideToolbarAndSideMenu();

        // Add a onClickListener to the login button
        // Will run loginUser method when clicked
        Button loginButton = loginLayout.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Hardcoded cheat login. This is just for debug
        ImageView logo = loginLayout.findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = "freddejn@mail.com";
                String password = "1234";
                activity.sendLoginRequest(username, password);
            }});

        // Add a onClickListener to the register button
        // Will run loginUser method when clicked
        Button registerButton = loginLayout.findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startRegisterFragment();
            }});

        return loginLayout;
    }

    /**
     * Method that will check so the fields are correctly filled out before sending information to the controller
     */
    private void loginUser() {
        EditText emailET = loginLayout.findViewById(R.id.email);
        EditText passwordET = loginLayout.findViewById(R.id.password);

        int passwordLength = 2;
        String emailAddress = emailET.getText().toString().trim();
        if (passwordET.getText().toString().length() < passwordLength) {
            passwordET.setError("password minimum contain " + passwordLength +" character");
                    passwordET.requestFocus();
        }
        if (passwordET.getText().toString().equals("")) {
            passwordET.setError("please enter password");
                    passwordET.requestFocus();
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            emailET.setError("please enter valid email address");
                    emailET.requestFocus();
        }
        if (emailET.getText().toString().equals("")) {
            emailET.setError("please enter email address");
                    emailET.requestFocus();
        }
        if (!emailAddress.equals("") &&
                passwordET.getText().toString().length() >= passwordLength &&
                !passwordET.getText().toString().trim().equals("")
                && android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {

            String username = emailET.getText().toString();
            String password = passwordET.getText().toString();


            activity.sendLoginRequest(username, password);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
