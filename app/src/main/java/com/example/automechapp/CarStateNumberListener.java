package com.example.automechapp;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CarStateNumberListener implements TextWatcher {
    private static final String SEPARATOR = " ";
    EditText self;

    String characterSet = "авекмнорстух";

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && !characterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    CarStateNumberListener(EditText self) {
        this.self = self;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
        if (((start + 1 == 2 || start + 1 == 6) && count == 0) || (start == 6 && count == 1)) {
            self.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)} );
            self.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        else if ((start + 1 == 5 && count == 0) || ((start == 1 || start == 5) && count == 1)) {
            self.setInputType(InputType.TYPE_CLASS_TEXT);
            self.setFilters(new InputFilter[] {filter} );
        }
        Toast.makeText(self.getContext(), start + " " + count, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public InputFilter getFilter() {
        return filter;
    }

    public void setFilter(InputFilter filter) {
        this.filter = filter;
    }
}
