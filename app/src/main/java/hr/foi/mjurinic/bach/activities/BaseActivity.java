package hr.foi.mjurinic.bach.activities;

import android.support.v7.app.AppCompatActivity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

public class BaseActivity extends AppCompatActivity {

    public boolean hasNavBar() {
        return !((KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK) && KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)));
    }

    public int convertDpToPx(int dp) {
        return (int) ((dp * getResources().getDisplayMetrics().density) + 0.5);
    }
}
