package com.hackforchange.views.participationsactive;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class AbstractTextValidator implements TextWatcher{
  private EditText editText;
  
  public AbstractTextValidator(EditText editText){
    this.editText = editText;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
  }

  public abstract void validate(EditText editText);

  @Override
  public void afterTextChanged(Editable s) {
    validate(editText);
  }

}
