package com.addisondalton.teatime;

import android.widget.Button;

/**
 * This class handles the switching of states within a button that controls a timer.(A CountdownTimer for this app).
 * This is done by holding the button's state within the state parameter, and by setting the button's text to reflect the state it is in.
 * The states are:
 * TIMER_FINISH - When the timer has finished.
 * TIMER_RUNNING - When the timer is actively running.
 * TIMER_PAUSED - When the timer has been paused.
 * TIMER_NOT_RUNNING - When the timer is not running. This either occurs at the start of the app, or right after TIMER_FINISH.
 *
 * It should be noted that the states handle code different then what their name implies.
 * For example, TIMER_RUNNING will set the button's text to 'PAUSE/STOP' because when the timer is running, the button should show the text to pause it.
 */

public class TimerButton {
    private Button timerButton;
    int state;
    public static final int TIMER_FINISHED = 0;
    public static final int TIMER_RUNNING = 1;
    public static final int TIMER_PAUSED = 2;
    public static final int TIMER_NOT_RUNNING = 3;
    public TimerButton(Button timerButton){
        this.timerButton = timerButton;
    }


    //method to assign buttons current state, the parameters that should be passed are
    // the static variables above. This method also changes the text of the button to reflect the state
    //the button will enter when pressed.
    public void setState(int state){
        this.state = state;

        switch (this.state){
            case TIMER_FINISHED:
                this.timerButton.setText(R.string.btn_stop);
                break;

            case TIMER_RUNNING:
                this.timerButton.setText(R.string.btn_pause);
                break;

            case TIMER_PAUSED:
                this.timerButton.setText(R.string.btn_resume);
                break;

            case TIMER_NOT_RUNNING:
                this.timerButton.setText(R.string.btn_start);
        }
    }

    public Button getButton(){
        return this.timerButton;
    }

    public int getState(){
        return this.state;
    }
}
