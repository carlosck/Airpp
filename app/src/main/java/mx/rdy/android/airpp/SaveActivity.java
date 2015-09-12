package mx.rdy.android.airpp;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.UUID;


public class SaveActivity extends SingleFragmentActivity {

    private static final String TAG="android.rdy.mx.airpp";
    @Override
    protected Fragment createFragment() {
        // return new CrimeFragment();
        return new SaveFragment();
        //return SaveFragment.newInstance();
    }



}