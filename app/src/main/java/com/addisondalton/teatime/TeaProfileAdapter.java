package com.addisondalton.teatime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Owner on 12/5/2017.
 */

public class TeaProfileAdapter extends ArrayAdapter<TeaProfile> {
    private TeaProfile teaProfile;
    private String teaProfileString;
    private SpinnerClickListener spinnerClickListener;

    public TeaProfileAdapter(Context context, List<TeaProfile> teaprofiles, SpinnerClickListener spinnerClickListener){
        super(context, 0, teaprofiles);
        this.spinnerClickListener = spinnerClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        this.teaProfile = getItem(position);
        this.teaProfileString = this.teaProfile.getFullString();

        //check if an existing view is being reused, otherwise inflate the view
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }

        TextView tv_teaProfile = convertView.findViewById(R.id.tv_spinner_tea_item);
        tv_teaProfile.setText(teaProfileString);

        //sets the tag of the convertView as the position in the spinner of the tea profile.
        //the tag is later called to figure which tea profile is being selected
        convertView.setTag(R.string.spinner_index_tag, position);
        convertView.setClickable(false);
        convertView.setLongClickable(false);

        return convertView;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        this.teaProfile = getItem(position);
        this.teaProfileString = this.teaProfile.getFullString();

        //check if an existing view is being reused, otherwise inflate the view
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }

        TextView tv_teaProfile = convertView.findViewById(R.id.tv_spinner_tea_item);
        tv_teaProfile.setText(teaProfileString);

        //sets the tag of the convertView as the position in the spinner of the tea profile.
        //the tag is later called to figure which tea profile is being selected
        convertView.setTag(R.string.spinner_index_tag, position);

        //listens for a click on this view (spinner item) and calls the custom spinnerClickListener
        //for normal and long clicks.
        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(spinnerClickListener != null){
                    spinnerClickListener.onItemClicked(view);
                }
            }
        });

        //listens for a click on this view (spinner item) and calls the custom spinnerClickListener
        //for normal and long clicks.
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(spinnerClickListener != null){
                    spinnerClickListener.onItemLongClicked(view);
                }
                return true;
            }
        });
        return convertView;
    }
}
