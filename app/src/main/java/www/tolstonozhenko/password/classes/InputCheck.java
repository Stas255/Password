package www.tolstonozhenko.password.classes;

import android.widget.EditText;

public class InputCheck {

    public static boolean Check(EditText editText, int leng, String regex){
        if(leng != 0 && editText.getText().toString().length() < leng){
            editText.setError("You need input more than "+leng);
            return false;
        }
        return true;
    }
}
