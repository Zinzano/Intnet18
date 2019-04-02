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
 * Created by Zinzano on 3/13/2018.
 * Custom ArrayAdapter for the register information
 */

public class RegisterAdapter extends ArrayAdapter<ProfileParameter>{

    private Context mContext;
    private List<ProfileParameter> profileParameterList = new ArrayList<>();

    public RegisterAdapter(Context c, ArrayList<ProfileParameter> list) {
        super(c, 0, list);
        mContext = c;
        profileParameterList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;

        // Get the current ProfileParameter object to access its data
        ProfileParameter currentProfileParameter = profileParameterList.get(position);

        // Inflate the layout for this list item´based on the type
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

        // Get the TextView that holds the name of the parameter and update its text
        TextView parameterName = (TextView) listItem.findViewById(R.id.profileParaName);
        parameterName.setText(currentProfileParameter.getName());

        // Get the TextView that holds the value and update its text
        TextView parameterValue = (TextView) listItem.findViewById(R.id.profileParameter);
        parameterValue.setText(currentProfileParameter.getValue());

        return listItem;
    }
}
