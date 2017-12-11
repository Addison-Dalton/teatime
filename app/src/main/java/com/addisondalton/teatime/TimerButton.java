package com.addisondalton.teatime;

import android.widget.Button;

/**
 * Created by Owner on 12/10/2017.
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
                this.timerButton.setText(R.string.btn_start);
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
