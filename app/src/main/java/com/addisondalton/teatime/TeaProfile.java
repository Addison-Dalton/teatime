package com.addisondalton.teatime;

import com.orm.SugarApp;
import com.orm.SugarRecord;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * This class extends SugarRecord from SugarORM to store a tea profile as an entity in a database.
 */

public class TeaProfile extends SugarRecord<TeaProfile> {
    String name;
    long milliseconds;

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

    public String getMinutesAndSeconds(){
        return String.format(Locale.US, "%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(this.milliseconds),
                                                                                TimeUnit.MILLISECONDS.toSeconds(this.milliseconds) -
                                                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this.milliseconds)));
    }
}
