package com.addisondalton.teatime;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //list to hold the spinner drop down elements
    List<String> teaProfilesStrings = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TeaProfile.deleteAll(TeaProfile.class); //TODO I think this is necessary to clear the database before using the app again
        setDefaultTeaProfiles(); //adds stored teas to database //TODO consider renaming
        getTeaProfileStrings();
        Spinner teaProfileSpinner = findViewById(R.id.spinner_tea_profiles);
        teaProfileSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> teaProfileArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, teaProfilesStrings);
        teaProfileSpinner.setAdapter(teaProfileArrayAdapter);



    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        //TODO determine which profile the user selected
        //TODO if a tea profile, start the timer
        //TODO if new tea profile, allow them to enter new tea profile
        //TODO if just a custom time, allow them to enter a time
    }
    public void onNothingSelected(AdapterView<?> arg0){

    }

    //method that add all the default tea profiles to the sugar orm database
    private void setDefaultTeaProfiles(){
        new TeaProfile("Black Tea", 60000).save();
        new TeaProfile("Green Tea", 30000).save();
        new TeaProfile("Flavored Black Tea",60000).save();
        new TeaProfile("Herbal Tea", 60000).save();
    }

    //method takes all the teaProfiles from a list and then calls each of their getFullString methods to produce a full string to be used a spinner dropdown element
    private void getTeaProfileStrings(){
        //list of all stored teaProfiles
        List<TeaProfile> teaProfiles = TeaProfile.listAll(TeaProfile.class);

        for(TeaProfile teaProfile : teaProfiles){
            teaProfilesStrings.add(teaProfile.getFullString());
        }
    }
    //TODO change the spinner color background, green or the deep red
    //TODO add first option of spinner to be "select tea profile" make sure it can't be chosen as actual option
    //TODO ability to add custom tea profile, place at end of spinner
    //TODO ability to just input a time, place at end of spinner
    //TODO timer functionality
    //TODO start button functionality
}
