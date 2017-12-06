package com.addisondalton.teatime;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by Owner on 12/5/2017.
 */

public class LongClickSpinner extends AppCompatSpinner {
    public LongClickSpinner(Context context, int mode){
        super(context, mode);
    }

    public LongClickSpinner(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public LongClickSpinner(Context context, AttributeSet attributeSet, int defStyleAttr){
        super(context, attributeSet, defStyleAttr);
    }

    public LongClickSpinner(Context context, AttributeSet attributeSet, int defStyleAttr, int mode){
        super(context, attributeSet, defStyleAttr, mode);
    }

    @Override
    public void onDetachedFromWindow(){
        super.onDetachedFromWindow();
    }
}
