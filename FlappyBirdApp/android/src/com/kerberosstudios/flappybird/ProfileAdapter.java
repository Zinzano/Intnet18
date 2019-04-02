package com.kerberosstudios.flappybird;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zinzano on 3/12/2018.
 * Custom ArrayAdapter for the profile information
 */

public class ProfileAdapter extends ArrayAdapter<ProfileParameter> {

    private Context mContext;
    private List<ProfileParameter> profileParameterList = new ArrayList<>();

    public ProfileAdapter(Context c, ArrayList<ProfileParameter> list) {
        super(c, 0, list);
        mContext = c;
        profileParameterList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;

        // Get the current ProfileParameter object to access its data
        ProfileParameter currentProfileParameter = profileParameterList.get(position);

        // Display one layout for when the parameter is editable and one for when its not
        if (!currentProfileParameter.getEditable()){
            listItem = LayoutInflater.from(mContext).inflate(R.layout.profile_list_item, parent, false);

            // Add OnClickListeners to both TextViews in the layout. This enables the user to click anywhere on the list item to make it editable
            listItem.findViewById(R.id.profileParameter).setOnClickListener(new ListViewButtonOnClickListener(position, this, "edit"));
            listItem.findViewById(R.id.profileParaName).setOnClickListener(new ListViewButtonOnClickListener(position, this, "edit"));

        } else {
            // Different edit layouts depending on what data type it contains
            switch(currentProfileParameter.getType()) {
                case "mail":
                    listItem = LayoutInflater.from(mContext).inflate(R.layout.profile_list_email_edit, parent, false);
                    break;
                case "number":
                    listItem = LayoutInflater.from(mContext).inflate(R.layout.profile_list_number_edit, parent, false);
                    break;
                case "password":
                    listItem = LayoutInflater.from(mContext).inflate(R.layout.profile_list_item_edit, parent, false);
                    break;
                default:
                    listItem = LayoutInflater.from(mContext).inflate(R.layout.profile_list_item_edit, parent, false);
                    break;
            }

            // Add OnClickListeners to the accept change and discard changes buttons on the edit layouts
            listItem.findViewById(R.id.acceptEdit).setOnClickListener(new ListViewButtonOnClickListener(position, this, "accept"));
            listItem.findViewById(R.id.discardEdit).setOnClickListener(new ListViewButtonOnClickListener(position, this, "discard"));
        }

        // Get the TextView that holds the name and update its text
        TextView parameterName = (TextView) listItem.findViewById(R.id.profileParaName);
        parameterName.setText(currentProfileParameter.getName());

        // Get the TextView that holds the value and update its text
        TextView parameterValue = (TextView) listItem.findViewById(R.id.profileParameter);
        parameterValue.setText(currentProfileParameter.getValue());

        return listItem;
    }

    /**
     * Custom OnClickListener class
     */
    class ListViewButtonOnClickListener implements View.OnClickListener{

        private int position;
        private ProfileAdapter adapter;
        private String state;

        public ListViewButtonOnClickListener(int position, ProfileAdapter a, String s) {
            this.position = position;
            this.adapter = a;
            this.state = s;
        }

        @Override
        /**
         * Three different actions based on the state
         */
        public void onClick(View v) {
            switch (state){
                case "edit":
                    // Make the clicked list item editable and update ListView
                    profileParameterList.get(position).setEditable(true);
                    adapter.notifyDataSetChanged();

                    // TODO: Add so that the input request focus, through fragment.
                    break;
                case "accept":
                    // Make the clicked list item non editable and mark it as updated
                    // Update the profileParameters value with the value from the EditText
                    profileParameterList.get(position).setEditable(false);
                    RelativeLayout rel = (RelativeLayout) v.getParent();
                    EditText et = rel.findViewById(R.id.profileParameter);
                    profileParameterList.get(position).setValue(et.getText().toString());
                    profileParameterList.get(position).setUpdated(true);
                    adapter.notifyDataSetChanged();
                    break;
                case "discard":
                    // Make the clicked list item non editable and update ListView
                    profileParameterList.get(position).setEditable(false);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
