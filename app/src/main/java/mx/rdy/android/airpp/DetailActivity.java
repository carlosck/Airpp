package mx.rdy.android.airpp;

import android.support.v4.app.Fragment;

/**
 * Created by Seca on 9/9/15.
 */
public class DetailActivity extends SingleFragmentActivity{
    private static final String TAG="android.rdy.mx.airpp";
    @Override
    protected Fragment createFragment() {
        // return new CrimeFragment();
        return new DetailFragment();
        //return SaveFragment.newInstance();
    }
}
