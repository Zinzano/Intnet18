package com.kerberosstudios.flappybird;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Socket socket;
    private Boolean isConnected = true;
    DrawerLayout drawer;
    Toolbar toolbar;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // String uri = "http://192.168.1.6:8080";
        // String uri = "http://130.229.145.44:8080";
        String uri = "http://130.229.171.112:8080";
        connectToServer(uri);

        fragmentManager = getFragmentManager();

        startLoginFragment();

    }

    /**
     * Method that will set up a socket connection with the server
     * @param uri
     */
    private void connectToServer(String uri) {
        Log.i("Connection with server", "start");
        try {
            socket = IO.socket(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Set the created socket in the SocketHandler
        SocketHandler.setSocket(socket);

        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on("userData", onUserData);
        socket.on("updateUserResponse", onUpdateUserResponse);
        socket.on("loginResponse", onLoginResponse);
        socket.on("highScoreData", onHighScoreData);
        socket.on("lobbyData", onLobbyData);
        socket.on("userLeft", onUserLeft);
        socket.on("newUserInLobby", onNewUserInLobby);
        socket.on("userCreated", onUserCreated);
        socket.on("chatData", onChatData);
        socket.on("newMessage", onNewMessage);
        socket.on("notLoggedIn", onNotLoggedIn);
        socket.connect();

        Log.i("Connection with server", "done");
    }

    /**
     * Method will hide the toolbar and disable the side menu
     */
    public void hideToolbarAndSideMenu() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // This locks the drawer so it cant be opened
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                // Hides the toolbar
                ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
                layoutParams.height = 0;
                toolbar.setLayoutParams(layoutParams);
            }
        });

    }

    /**
     * Method will show the toolbar and enable the side menu
     */
    public void showToolbarAndSideMenu() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // This unlocks the drawer so it can be opened
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                // Shows the toolbar
                ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
                layoutParams.height = dpToPx(56);
                toolbar.setLayoutParams(layoutParams);
            }
        });
    }

    /**
     * Method that converts Density-independent Pixels to Pixels.
     * @param dp
     * @return
     */
    public int dpToPx(int dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    /**
     * Will close the drawer if it is opened.
     * Will go back to LoginFragment if the user are on the register page.
     */
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment f = fragmentManager.findFragmentById(R.id.fragment);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (f instanceof RegisterFragment) {
            startLoginFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    /**
     * Method that handle action bar item clicks.
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        // The id of the clicked item
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            socket.emit("logout");
            startLoginFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    /**
     * Method that handle navigation view item clicks.
     */
    public boolean onNavigationItemSelected(MenuItem item) {

        // The id of the clicked item
        int id = item.getItemId();
        Fragment f;
        switch (id){
            case R.id.nav_profile:
                startProfileFragment();
                break;

            case R.id.nav_high_scores:
                startHighScoreFragment();
                break;

            case R.id.nav_chat_room:
                f = fragmentManager.findFragmentById(R.id.fragment);

                // Do not want to join the chat room again because that would duplicate the data in the list
                if (!(f instanceof ChatRoomFragment)){
                    startChatRoomFragment();
                }
                break;

            case R.id.nav_lobby:
                f = fragmentManager.findFragmentById(R.id.fragment);

                // Do not want to join the lobby again because that would duplicate the data in the list
                if (!(f instanceof LobbyFragment)){
                    startLobbyFragment();
                }
                break;

            case R.id.nav_send:
                // Start the activity that will run the Flappy Bird game
                Intent i = new Intent(this, AndroidLauncher.class);
                startActivity(i);
                break;
        }

        // Will close the drawer with an animation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method that let you set the title of the toolbar
     * @param title
     */
    public void setNewTitle(final String title){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle(title);
            }
        });
    }

    /**
     * Method that let you set the information in the nav header.
     * @param email
     * @param fullName
     */
    private void setNavheaderInfo(final String email, final String fullName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView navHeaderEmail =  findViewById(R.id.nav_header_email);
                navHeaderEmail.setText(email);
                TextView navHeaderName =  findViewById(R.id.nav_header_user_name);
                navHeaderName.setText(fullName);

            }
        });
    }

    // Below are methods that will start a particular fragment

    public void startRegisterFragment() {
        RegisterFragment registerFragment = new RegisterFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment, registerFragment).commit();
    }

    public void startProfileFragment() {
        setNewTitle("Profile");
        ProfileFragment profileFragment = new ProfileFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment, profileFragment).commit();
    }

    public void startLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment, loginFragment).commit();
    }

    public void startLobbyFragment() {
        setNewTitle("Lobby");
        LobbyFragment lobbyFragment = new LobbyFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment, lobbyFragment).commit();
    }

    public void startChatRoomFragment() {
        setNewTitle("Chat room");
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment, chatRoomFragment).commit();
    }

    public void startHighScoreFragment() {
        setNewTitle("High scores");
        HighScoresFragment highScoresFragment = new HighScoresFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment, highScoresFragment).commit();
    }

    @Override
    public void onDestroy() {

        socket.emit("logout");
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("userData", onUserData);
        socket.off("updateUserResponse", onUpdateUserResponse);
        socket.off("loginResponse", onLoginResponse);
        socket.off("highScoreData", onHighScoreData);
        socket.off("lobbyData", onLobbyData);
        socket.off("userLeft", onUserLeft);
        socket.off("newUserInLobby", onNewUserInLobby);
        socket.off("userCreated", onUserCreated);
        socket.off("chatData", onChatData);
        socket.off("newMessage", onNewMessage);
        socket.off("notLoggedIn", onNotLoggedIn);
        socket.disconnect();
        super.onDestroy();
    }

    // Below are methods that will listen to events sent from the server
    /**
     * Listener for the Socket.EVENT_CONNECT event.
     * Displays a Toast message
     */
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        Toast.makeText(getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    /**
     * Listener for the notLoggedIn event.
     * Display a Toast message if active fragment is not LoginFragment.
     */
    private Emitter.Listener onNotLoggedIn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Fragment f = fragmentManager.findFragmentById(R.id.fragment);
            if (!(f instanceof LoginFragment)){
                startLoginFragment();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MainActivity", "forceRelogin");
                        isConnected = false;
                        Toast.makeText(getApplicationContext(),
                                "Server force sign in", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };

    /**
     * Listener for the Socket.EVENT_DISCONNECT event.
     * Displays a Toast message.
     */
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("MainActivity", "diconnected");
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    /**
     * Listener for the Socket.EVENT_CONNECT_ERROR event.
     * Displays a Toast message.
     */
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("MainActivity", "Error connecting");
                    Log.e("MainActivity", args[0].toString());
                    Toast.makeText(getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    /**
     * Listener for the userData event.
     * Send the data to the ProfileFragment if active
     */
    private Emitter.Listener onUserData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            Log.d("onUserListener JSON", data.toString());
            if (data.length() != 0){
                Fragment f = fragmentManager.findFragmentById(R.id.fragment);
                if (f instanceof ProfileFragment){
                    ProfileFragment profileFragment = (ProfileFragment)f;
                    profileFragment.setProfileInfo(data);
                }
            } else {
                callForProfileData();
            }
        }
    };

    /**
     * Listener for the lobbyData event.
     * Send the data to the lobbyFragment if active
     */
    private Emitter.Listener onLobbyData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONArray arr = (JSONArray) args[0];

            Log.d("LobbyData", arr.toString());

            Fragment f = fragmentManager.findFragmentById(R.id.fragment);
            if (f instanceof LobbyFragment){
                LobbyFragment lobbyFragment = (LobbyFragment)f;
                lobbyFragment.populateLobby(arr);
            }
        }
    };

    /**
     * Listener for the updateUserResponse event.
     * Handles errors from server and prompt the ProfileFragment to display the error.
     * If changes are accepted, display a Toast message.
     */
    private Emitter.Listener onUpdateUserResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("UpdateProfile Response", args[0].toString());
            JSONObject data = (JSONObject) args[0];
            try {
                Boolean accepted = data.getBoolean("result");

                if (accepted){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Profile updates accepted", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Fragment f = fragmentManager.findFragmentById(R.id.fragment);
                    if (f instanceof ProfileFragment) {
                        ProfileFragment profileFragment = (ProfileFragment) f;
                        profileFragment.displayErrorFromServer(data);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * Listener for the userCreated event.
     * Handles errors from server and prompt the RegisterFragment to display the error.
     * If changes are accepted, activate LobbyFragment
     */
    private Emitter.Listener onUserCreated = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Log.d("UserCreated Response", args[0].toString());
            try {
                Boolean accepted = data.getBoolean("result");

                if (accepted){
                    // Server will login user se we only need to start displaying the "home" page
                    startLobbyFragment();
                } else {
                    Fragment f = fragmentManager.findFragmentById(R.id.fragment);
                    if (f instanceof RegisterFragment) {
                        RegisterFragment registerFragment = (RegisterFragment) f;
                        registerFragment.displayErrorFromServer(data);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Listener for the loginResponse event.
     * If accepted response. Set the nav header and activate LobbyFragment.
     * If not accepted, displays a Toast message.
     */
    private Emitter.Listener onLoginResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject response = (JSONObject) args[0];
            try {
                Boolean accepted= response.getBoolean("login");

                if (accepted){
                    String email = response.getJSONObject("user").getString("email");
                    String firstName = response.getJSONObject("user").getString("firstName");
                    String lastName = response.getJSONObject("user").getString("lastName");
                    String fullName= firstName + " " + lastName;

                    setNavheaderInfo(email, fullName);

                    Fragment f = fragmentManager.findFragmentById(R.id.fragment);
                    if (f instanceof LoginFragment){
                        //startProfileFragment();
                        startLobbyFragment();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    R.string.login_failed, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Login Response", args[0].toString());
        }
    };

    /**
     * Listener for the highScoreData event.
     * Send the data to HighScoresFragment if active.
     */
    private Emitter.Listener onHighScoreData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("HighScore data", args[0].toString());

            JSONObject data = (JSONObject) args[0];
            try {
                JSONArray arr = data.getJSONArray("Score");

                Fragment f = fragmentManager.findFragmentById(R.id.fragment);
                if (f instanceof HighScoresFragment){
                    HighScoresFragment highScoresFragment = (HighScoresFragment)f;
                    highScoresFragment.populateHighScoresList(arr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Listener for the userLeft event.
     * Tell the LobbyFragment to remove user if active.
     */
    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d("User left", args[0].toString());
            Fragment f = fragmentManager.findFragmentById(R.id.fragment);
            if (f instanceof LobbyFragment){
                LobbyFragment lobbyFragment = (LobbyFragment)f;
                lobbyFragment.removeUserFromLobby(args[0].toString());
            }
        }
    };

    /**
     * Listener for the newUserInLobby event.
     * Tell the LobbyFragment to add user if active.
     */
    private Emitter.Listener onNewUserInLobby = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d("User joined lobby", args[0].toString());
            JSONObject data = (JSONObject) args[0];
            Fragment f = fragmentManager.findFragmentById(R.id.fragment);
            if (f instanceof LobbyFragment){
                LobbyFragment lobbyFragment = (LobbyFragment)f;
                lobbyFragment.addUserToLobby(data);
            }

        }
    };

    /**
     * Listener for the chatData event.
     * Send chat data to ChatRoomFragment if active.
     */
    private Emitter.Listener onChatData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Get chat data", args[0].toString());
            JSONArray data = (JSONArray) args[0];
            Fragment f = fragmentManager.findFragmentById(R.id.fragment);
            if (f instanceof ChatRoomFragment){
                ChatRoomFragment chatRoomFragment = (ChatRoomFragment)f;
                chatRoomFragment.populateChatroom(data);
            }

        }
    };

    /**
     * Listener for the newMessage event.
     * Tell ChatRoomFragment to add a message if active.
     */
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject message = (JSONObject) args[0];

            Log.d("Recived a new message", args[0].toString());

            Fragment f = fragmentManager.findFragmentById(R.id.fragment);
            if (f instanceof ChatRoomFragment){
                ChatRoomFragment chatRoomFragment = (ChatRoomFragment)f;
                chatRoomFragment.addMessage(message);
            }

        }
    };

    // Below are methods that will emit an event and possibly data to the server

    public void callForProfileData(){
        socket.emit("user");
    }

    public void callForLobbyData(){
        socket.emit("joinLobby");
    }

    public void callForHighScoreData(){
        socket.emit("highscore");
    }

    public void saveProfile(JSONObject profile){
        socket.emit("updateUser", profile);
    }

    public void createUser(JSONObject profile){
        socket.emit("createUser", profile);
        Log.d("Create User", profile.toString());
    }

    public void leaveLobby(){
        socket.emit("leaveLobby");
    }

    public void sendChatMessage(String message){
        socket.emit("sendMessage", message);
    }

    public void leaveChatroom(){
        socket.emit("leaveChat");
    }

    public void joinChatroom(){
        socket.emit("joinChat");
    }

    public void sendLoginRequest(String username, String password) {
        JSONObject data = new JSONObject();
        try {
            data.put("username", username);
            data.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Login request", data.toString());
        socket.emit("login", data);
    }


}
