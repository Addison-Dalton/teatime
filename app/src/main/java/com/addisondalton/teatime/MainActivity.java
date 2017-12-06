package com.addisondalton.teatime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

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

    //TODO fiddle with how I want the text to react with the delete button present. May just hide text altogether.
    //TODO Whatever I do to text, when the user clicks off the delete button and text should return to normal if the user did not delete the profile
    //TODO add first option of spinner to be "select tea profile" make sure it can't be chosen as actual option
    //TODO ability to add custom tea profile, place at end of spinner
    //TODO ability to just input a time, place at end of spinner
    //TODO timer functionality
    //TODO start button functionality
}
