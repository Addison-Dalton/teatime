package com.addisondalton.teatime;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SpinnerClickListener {
    TeaProfileAdapter teaProfileAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //only add the default tea profiles if there are no tea profiles
        if(TeaProfile.listAll(TeaProfile.class).isEmpty()){
            setDefaultTeaProfiles(); //adds stored teas to database //TODO consider renaming
        }

        setTeaProfileAdapter();

        Button showAddTeaProfilePopup = findViewById(R.id.btn_show_add_tea_profile_popup);
        showAddTeaProfilePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });
    }

    //detects long click on an item within the spinner containing tea profiles. Presents a delete
    //button to delete the profile
    @Override
    public void onItemLongClicked(final View view){
        LongClickSpinner teaProfileSpinner = findViewById(R.id.spinner_tea_profiles);
        final Button deleteButton = view.findViewById(R.id.btn_delete);
        final int position = (int) view.getTag(R.string.spinner_index_tag);

        view.findViewById(R.id.tv_spinner_tea_item).animate().xBy(-170f).setDuration(250); //TODO fiddle with this animation
        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTeaProfile(teaProfileAdapter.getItem(position));
            }
        });

    }

    //detects a normal click on an item and sets it as the spinner's selected item
    @Override
    public void onItemClicked(final View view){
        LongClickSpinner teaProfileSpinner = findViewById(R.id.spinner_tea_profiles);
        teaProfileSpinner.onDetachedFromWindow();
        final int position = (int) view.getTag(R.string.spinner_index_tag);
        teaProfileSpinner.setSelection(position);
    }
    /**
     * MAY NOT NEED
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        //TODO if a tea profile, start the timer
        //TODO if new tea profile, allow them to enter new tea profile
        //TODO if just a custom time, allow them to enter a time

        Log.i("parent", parent.toString());
        Log.i("position", Integer.toString(position));
        Log.i("id", Long.toString(id));

    }
    public void onNothingSelected(AdapterView<?> arg0){

    }**/


    //method that add all the default tea profiles to the sugar orm database
    private void setDefaultTeaProfiles(){
        new TeaProfile("Black Tea", 60000).save();
        new TeaProfile("Green Tea", 30000).save();
        new TeaProfile("Flavored Black Tea",60000).save();
        new TeaProfile("Herbal Tea", 60000).save();
    }

    //populates the spinner with items from a list of all tea profiles,
    private void setTeaProfileAdapter(){
        Spinner teaProfileSpinner = findViewById(R.id.spinner_tea_profiles);
        teaProfileAdapter =  new TeaProfileAdapter(this, TeaProfile.listAll(TeaProfile.class), this);
        teaProfileSpinner.setAdapter(teaProfileAdapter);
    }

    //takes a tea profile and deletes it from the database, then calls a method to reset the spinner
    private void deleteTeaProfile(TeaProfile teaProfile){
        teaProfile.delete();
        setTeaProfileAdapter();
    }

    private void showPopup(){
        //LayoutInflater
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View addTeaProfilePopup = layoutInflater.inflate(R.layout.add_tea_profile_popup, null);

        //PopupWindow
        final PopupWindow teaProfilePopupWindow = new PopupWindow(addTeaProfilePopup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        teaProfilePopupWindow.setTouchable(true);
        teaProfilePopupWindow.setFocusable(true);
        teaProfilePopupWindow.showAtLocation(addTeaProfilePopup, Gravity.CENTER, 0, -200);

        //View variables
        final EditText teaName = addTeaProfilePopup.findViewById(R.id.et_popup_tea_name);
        final EditText teaMinutes = addTeaProfilePopup.findViewById(R.id.et_popup_minutes);
        final EditText teaSeconds = addTeaProfilePopup.findViewById(R.id.et_popup_seconds);
        Button addTeaProfile = addTeaProfilePopup.findViewById(R.id.btn_popup_add_tea);
        Button cancelAddTeaProfile = addTeaProfilePopup.findViewById(R.id.btn_popup_cancel);

        //check that minutes and seconds are within acceptable bounds
        checkTimeFormat(teaMinutes);
        checkTimeFormat(teaSeconds);

        //onClickListener for 'Add Tea Profile' button, checks for valid tea profile, then adds it to the tea profile database
        //also display a toast that the tea has been added.
        addTeaProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teaNameString = teaName.getText().toString();

                if(!teaNameString.matches("")){ //check that a tea's name is NOT empty
                    if(teaMinutes.getText().toString().matches("")){ //if minutes has no value, set it to 00
                        teaMinutes.setText(R.string.default_time_value);
                    }
                    if(teaSeconds.getText().toString().matches("")){ //if seconds has no value, set it to 00
                        teaSeconds.setText(R.string.default_time_value);
                    }

                    //checks that both minutes and seconds are NOT 00
                    if(!teaMinutes.getText().toString().matches(getString(R.string.default_time_value)) ||
                            !teaSeconds.getText().toString().matches(getString(R.string.default_time_value))){

                        //TODO add tea profile to database, before adding do following:
                        //TODO convert any 0d to just have the digit without leading 0
                        //TODO convery minutes and seconds into milliseconds.
                        Toast.makeText(getApplicationContext(), "Tea added.", Toast.LENGTH_SHORT).show();
                        teaProfilePopupWindow.dismiss();
                    }else{ //show toast if both minutes and seconds are 00
                        Toast.makeText(getApplicationContext(), "Please input a time!",Toast.LENGTH_SHORT).show();
                    }
                }else { //show toast if tea name is empty
                    Toast.makeText(getApplicationContext(), "Please input a tea name!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //cancel button onClickListener, simply dismisses the popup window.
        cancelAddTeaProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teaProfilePopupWindow.dismiss();
            }
        });
    }

    /**
     *
     * this method is called to check that both the minutes and seconds of an added tea profile are
     * within acceptable bounds, that being less then 60. This is done by using a textChangedListener
     */
    private void checkTimeFormat(final EditText time){
        time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().matches("")){ //to avoid NumberFormatException if the user was to return the string to ""
                    int intOfTime = Integer.parseInt(editable.toString());

                    if(intOfTime > 59){
                        time.removeTextChangedListener(this);
                        time.setText(R.string.max_time_value);
                        time.setSelection(time.getText().length()); //set cursor to end of string
                        time.addTextChangedListener(this);
                    }
                }
            }
        });


    }

    //TODO fiddle with how I want the text to react with the delete button present. May just hide text altogether.
    //TODO Whatever I do to text, when the user clicks off the delete button and text should return to normal if the user did not delete the profile
    //TODO add first option of spinner to be "select tea profile" make sure it can't be chosen as actual option
    //TODO ability to add custom tea profile, place at end of spinner
    //TODO ability to just input a time, place at end of spinner
    //TODO timer functionality
    //TODO start button functionality
}
