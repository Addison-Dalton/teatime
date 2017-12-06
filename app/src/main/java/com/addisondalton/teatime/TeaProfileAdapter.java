package com.addisondalton.teatime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
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

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }

        TextView tv_teaProfile = convertView.findViewById(R.id.tv_spinner_tea_item);
        Button deleteButton = convertView.findViewById(R.id.btn_delete); //TODO may not need this
        tv_teaProfile.setText(teaProfileString);

        convertView.setTag(R.string.click_tag, position);
        convertView.setClickable(false);
        convertView.setLongClickable(false);

        return convertView;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        this.teaProfile = getItem(position);
        this.teaProfileString = this.teaProfile.getFullString();

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }

        TextView tv_teaProfile = convertView.findViewById(R.id.tv_spinner_tea_item);
        Button deleteButton = convertView.findViewById(R.id.btn_delete); //TODO may not need this
        tv_teaProfile.setText(teaProfileString);

        convertView.setTag(R.string.click_tag, position);

        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(spinnerClickListener != null){
                    spinnerClickListener.onItemClicked(view);
                }
            }
        });

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
