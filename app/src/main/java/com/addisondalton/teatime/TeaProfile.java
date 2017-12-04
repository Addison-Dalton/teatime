package com.addisondalton.teatime;

import com.orm.SugarRecord;

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
}
