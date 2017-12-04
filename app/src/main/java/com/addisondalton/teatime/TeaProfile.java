package com.addisondalton.teatime;

import com.orm.SugarRecord;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Owner on 12/4/2017.
 */

public class TeaProfile extends SugarRecord<TeaProfile> {
    String name;
    int milliseconds;

    //default constructor
    public TeaProfile(){

    }

    public TeaProfile(String name, int milliseconds){
        this.name = name;
        this.milliseconds = milliseconds;
    }

    public String getFullString(){
        return (this.name + ", " + getMinutesAndSeconds());
    }

    private String getMinutesAndSeconds(){
        return String.format(Locale.US, "%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(this.milliseconds),
                                                                                TimeUnit.MILLISECONDS.toSeconds(this.milliseconds) -
                                                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this.milliseconds)));
    }
}
