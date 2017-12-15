package com.addisondalton.teatime;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.addisondalton.teatime.TimerButton.TIMER_FINISHED;
import static com.addisondalton.teatime.TimerButton.TIMER_NOT_RUNNING;
import static com.addisondalton.teatime.TimerButton.TIMER_PAUSED;
import static com.addisondalton.teatime.TimerButton.TIMER_RUNNING;

/**
 * The onItemClicked and onItemLongClicked methods are from the user yajnesh on stackoverflow.
 * SugarORM is used store tea profiles in a database. http://satyan.github.io/sugar/index.html
 */

public class MainActivity extends AppCompatActivity implements SpinnerClickListener {
    TeaProfileAdapter teaProfileAdapter;
    CountDownTimer teaTimer;
    long millisecondsRemaining;
    long initialMilliseconds;
    final static int TICK_INTERVAL = 100;
    TimerButton teaTimerButton;
    AudioManager audioManager;
    Ringtone alarmSound;
    int userVolume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
        //only add the default tea profiles if there are no tea profiles
        if(TeaProfile.listAll(TeaProfile.class).isEmpty()){
            setDefaultTeaProfiles(); //adds stored teas to database //TODO consider renaming
        } */

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setTeaProfileAdapter();
        Button showAddTeaProfilePopup = findViewById(R.id.btn_show_add_tea_profile_popup);
        final Spinner teaProfileSpinner = findViewById(R.id.spinner_tea_profiles);
        final TextView teaProfileName = findViewById(R.id.tv_tea_profile_value);
        final TextView teaBrewTime = findViewById(R.id.tv_time_remaining_value);
        final TextView teaBrewStatus = findViewById(R.id.tv_tea_status_value);

        teaProfileName.setSelected(true);


        showAddTeaProfilePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });


        //new TimerButton, takes a button as parameter in constructor.
        //this class simply keeps up with the various states the timer button can be in.
        teaTimerButton = new TimerButton((Button) findViewById(R.id.btn_timer));
        teaTimerButton.setState(TIMER_NOT_RUNNING);
        teaTimerButton.getButton().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (teaTimerButton.getState()){
                    //TIMER FINISHED CODE:
                    case TIMER_FINISHED:
                        teaTimerButton.setState(TIMER_NOT_RUNNING);
                        resetTimer();
                        alarmSound.stop();
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, userVolume, 0 );
                        break;

                    //START TIMER CODE:
                    case TIMER_NOT_RUNNING:
                       try { //catch when there are no tea profiles in the spinner which would produce a ArrayIndexOutOfBoundsException
                           teaProfileName.setText(teaProfileAdapter.getItem(teaProfileSpinner.getSelectedItemPosition()).name);
                           teaBrewTime.setText(teaProfileAdapter.getItem(teaProfileSpinner.getSelectedItemPosition()).getMinutesAndSeconds());
                           teaBrewStatus.setText(R.string.tea_status_brewing);
                           teaTimerButton.setState(TIMER_RUNNING);

                           //start the timer
                           initialMilliseconds = teaProfileAdapter.getItem(teaProfileSpinner.getSelectedItemPosition()).milliseconds;
                           runTimer(teaProfileAdapter.getItem(teaProfileSpinner.getSelectedItemPosition()).milliseconds);
                       } catch (ArrayIndexOutOfBoundsException e){//alert the user that there is no tea profile in the spinner
                           Toast.makeText(getApplicationContext(), "You have no tea selected. Click the '+' button to add one. ",Toast.LENGTH_SHORT).show();
                       }
                        break;

                    //RESUME TIMER CODE:
                    case TIMER_PAUSED:
                        teaTimerButton.setState(TIMER_RUNNING);
                        teaBrewStatus.setText(R.string.tea_status_brewing);

                        //resume the timer by passing milliseondsRemaining to runTimer()
                        runTimer(millisecondsRemaining);
                        break;

                    //PAUSE TIMER CODE:
                    case TIMER_RUNNING:
                        teaTimerButton.setState(TIMER_PAUSED);
                        teaBrewStatus.setText(R.string.tea_status_pause);

                        //pause the timer by canceling it
                        teaTimer.cancel();
                        break;
                }
            }
        });

        //listens for long click user, used to reset the timer.
        teaTimerButton.getButton().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(teaTimerButton.getState() == TIMER_RUNNING ){ //only executes if the timer has already started running
                    resetTimer();
                    return true;
                }
                return false;
            }
        });
    }

    //detects long click on an item within the spinner containing tea profiles. Presents a delete
    //button to delete the profile
    @Override
    public void onItemLongClicked(final View view){
        final Button deleteButton = view.findViewById(R.id.btn_delete);
        final int position = (int) view.getTag(R.string.spinner_index_tag);

        //if the delete button is not visible, then show it and handle click events on the delete button
        if(deleteButton.getVisibility() == View.GONE){
            view.findViewById(R.id.tv_spinner_tea_item).animate().xBy(-170f).setDuration(250);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteTeaProfile(teaProfileAdapter.getItem(position));
                }
            });
        }else{
            deleteButton.setVisibility(View.GONE);
            view.findViewById(R.id.tv_spinner_tea_item).animate().xBy(170f).setDuration(250);
        }
    }

    //detects a normal click on an item and sets it as the spinner's selected item
    @Override
    public void onItemClicked(final View view){
        LongClickSpinner teaProfileSpinner = findViewById(R.id.spinner_tea_profiles);
        teaProfileSpinner.onDetachedFromWindow();
        final int position = (int) view.getTag(R.string.spinner_index_tag);
        teaProfileSpinner.setSelection(position);
    }

    //creates a new instance of CountDownTimer, keeps the remaining milliseconds remaining in
    //millisecondsRemaining, and updates the brewing time on screen.
    private void runTimer(final long milliseconds){
        final TextView teaBrewTime =  findViewById(R.id.tv_time_remaining_value);
        final TextView teaStatus = findViewById(R.id.tv_tea_status_value);
        teaTimer = new CountDownTimer(milliseconds, TICK_INTERVAL) {
            @Override
            public void onTick(long l) {
                millisecondsRemaining = l;
                teaBrewTime.setText(getMinutesAndSeconds(l));
                updateImage(l);
            }

            @Override
            public void onFinish() {
                teaTimer.cancel();
                teaBrewTime.setText(R.string.time_end_value); //the last tick doesn't execute, so this will set the text to 0:00
                teaStatus.setText(R.string.tea_status_finished);
                teaTimerButton.setState(TIMER_FINISHED);

                //alarm sound
                try{
                    userVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING); //holds the users volume before this app sets it to max.
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                    String uri = "android.resource://" + getPackageName() + "/" + R.raw.alarm_sound;
                    Uri alarm = Uri.parse(uri);
                    alarmSound = RingtoneManager.getRingtone(getApplicationContext(), alarm);
                    alarmSound.play();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void resetTimer(){
        final TextView teaProfileName = findViewById(R.id.tv_tea_profile_value);
        final TextView teaBrewTime = findViewById(R.id.tv_time_remaining_value);
        final TextView teaBrewStatus = findViewById(R.id.tv_tea_status_value);
        ImageView img = findViewById(R.id.iv_tea_image);
        ClipDrawable imageDrawable = (ClipDrawable) img.getDrawable();

        imageDrawable.setLevel(0);
        teaProfileName.setText(R.string.default_tea_profile);
        teaTimerButton.setState(TIMER_NOT_RUNNING);
        teaBrewStatus.setText(R.string.default_tea_status);
        teaBrewTime.setText(R.string.default_time);
        teaTimer.cancel();
        initialMilliseconds = 0;
    }

    private void updateImage(long currentMilliseconds){

        float ratio = (float) currentMilliseconds / (float) initialMilliseconds;

        float levelValue = 10000 - (10000 * ratio);

        ImageView img = findViewById(R.id.iv_tea_image);
        ClipDrawable imageDrawable = (ClipDrawable) img.getDrawable();
        imageDrawable.setLevel((int) levelValue);
    }

    //converts milliseconds into a String formatted as minutes and seconds, MM:SS
    private String getMinutesAndSeconds(long milliseconds){
        return String.format(Locale.US, "%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

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

                        //call method to add tea to database
                        addTeaToDatabase(teaName.getText().toString(),Integer.parseInt(teaMinutes.getText().toString()), Integer.parseInt(teaSeconds.getText().toString()));
                        //show toast that tea was added
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
    //adds tea to the database, takes the name, and time in minutes and seconds as String parameters.
    //converts minutes and seconds into milliseconds as an int before adding to database
    private void addTeaToDatabase(String teaName, int minutes, int seconds){
        int milliseconds = (minutes * 60000) + (seconds * 1000);
        new TeaProfile(teaName, milliseconds).save();
        setTeaProfileAdapter();
    }

    //sets the text for the TextViews associated with showing the tea profile's name and brew time.
    //this is called when the user starts the timer
    /**
    private void displayTeaProfile(String teaName, String time){
        TextView teaProfileSelected = findViewById(R.id.tv_tea_profile_value);
        TextView brewTime = findViewById(R.id.tv_time_remaining_value);

        teaProfileSelected.setText(teaName);
        brewTime.setText(time);
    } */
    //MAJOR ITEMS
    //TODO Whatever I do to text, when the user clicks off the delete button and text should return to normal if the user did not delete the profile
    //TODO consider still finding a way for text to scroll inside the spinner
}

