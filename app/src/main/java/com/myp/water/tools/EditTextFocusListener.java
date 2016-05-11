package com.myp.water.tools;

import android.view.View;
import android.widget.EditText;

/**
 * Created by myp on 2016/1/8.
 */

public class EditTextFocusListener implements  View.OnFocusChangeListener {
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText textView = (EditText)v;
        String hint;

        if (hasFocus) {
            hint = textView.getHint().toString();
            textView.setTag(hint);
            textView.setHint("");
        } else {
            hint = textView.getTag().toString();
            textView.setHint(hint);
        }
    }
}