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

// Сие есть небольшой класс для красивого ввода номера авто. Ну просто некрасиво выглядит,
// Когда в и так раздутом классе CarActivity будет такая жирная хрень
public class CarStateNumberListener implements TextWatcher {
    // Тут и так понятно
    private static final String SEPARATOR = " ";
    EditText self;

    // Разрешенные для ввода буквы (ну, те, которые в номерах могут быть))
    String characterSet = "авекмнорстух";

    // Фиииильтр
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // Если текст есть и он не содержит допустимые символы => содержит недопустимые
            // Меняем их на пустую строку
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
        // Если мы тут: "аа" или тут "аа111а", и текст вводится
        // или же мы тут "аа11" и символы убирают
        // то ставим тип вводимого текста - число
        if (((start + 1 == 2 || start + 1 == 6) && count == 0) || (start == 4 && count == 1)) {
            self.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)} );
            self.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        // Если тут "аа111" и текст пишут
        // Или тут "а" или тут "аа111" и текст удаляют
        // Меняем тип вводимого текста на буковы и ставим наш фильтр
        else if ((start + 1 == 5 && count == 0) || ((start == 1 || start == 5) && count == 1)) {
            self.setInputType(InputType.TYPE_CLASS_TEXT);
            self.setFilters(new InputFilter[] {filter} );
        }
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
