package mx.rdy.android.airpp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Date;
import java.util.UUID;


/**
 * Created by Seca on 8/17/15.
 */
public class SaveFragment extends Fragment {
    private static final String TAG="android.rdy.mx.airpp";
    static final int SAVE_BATHROOM_CODE = 1;
    static final int CANCEL_BATHROOM_CODE = 1;

    public static final String EXTRA_CRIME_ID= "com.bignerdranch.android.criminalintent.crime_id";
    private static final String DIALOG_DATE= "date";
    private static final int REQUEST_DATE = 0;

    //private Crime mCrime;
    public TextView mLocation;
    public TextView mDirection;
    private Switch mMix;
    private Switch mHandicap;
    private Switch mBaby;
    private Switch mPaper;
    private Switch mFree;
    private Switch mWater;
    private RatingBar mStars;

    Button mOkButton;
    Button mCancelButton;

    private Bathroom b;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getActivity().getIntent();
        b=(Bathroom)myIntent.getSerializableExtra("bathroom");
        //UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        //mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_bathroom_layout, parent,false);



                //CheatActivity
       /* mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        /*mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateText();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                //DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(fm,DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });*/

        cacheElements(v);
        return v;

    }
    public void cacheElements(View v)
    {
        mLocation = (TextView) v.findViewById(R.id.inputLocation);
        mDirection = (TextView) v.findViewById(R.id.inputDirection);

        mMix = (Switch)v.findViewById(R.id.mix_switch);
        mHandicap = (Switch)v.findViewById(R.id.handicap_switch);
        mBaby = (Switch)v.findViewById(R.id.baby_switch);
        mPaper = (Switch)v.findViewById(R.id.paper_switch);
        mFree = (Switch)v.findViewById(R.id.free_switch);
        mWater = (Switch)v.findViewById(R.id.water_switch);
        mStars = (RatingBar)v.findViewById(R.id.starsBar);

        mOkButton = (Button) v.findViewById(R.id.saveButton);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });


        mCancelButton = (Button) v.findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelData();
            }
        });
    }

    public static SaveFragment newInstance()
    {
        Bundle args = new Bundle();
        //args.putSerializable(EXTRA_CRIME_ID,crimeId);

        SaveFragment fragment = new SaveFragment();
        fragment.setArguments(args);


        return fragment;
    }
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        Log.i(TAG,"onm activity result");
        //b= (Bathroom)data.getSerializableExtra("bathroom");
    }
    public void saveData()
    {
        Log.i(TAG,"save");
        if(checkForm())
        {
            b.save();
            returnData();
        }
    }

    public void cancelData()
    {
        //getActivity().setResult(1);
        Log.i(TAG,"close");
        Intent returnIntent = new Intent();
        getActivity().setResult(CANCEL_BATHROOM_CODE, returnIntent);
        getActivity().finish();
    }

    public boolean checkForm()
    {
        boolean error= false;
        b.setLocation(mLocation.getText().toString());
        b.setDirection(mDirection.getText().toString());
        b.setMix(mMix.isChecked());
        b.setHandicap(mHandicap.isChecked());
        b.setBaby(mBaby.isChecked());
        b.setPaper(mPaper.isChecked());
        b.setFree(mFree.isChecked());
        b.setWater(mWater.isChecked());
        b.setStars((int)(mStars.getRating()));

        if(b.getLocation().length()<3)
        {
            error=true;
            mLocation.setError("Obligatorio >3char");
        }
        if(b.getDirection().length()<5)
        {
            error=true;
            mDirection.setError("Obligatorio >3char");
        }

        return !error;
    }
    public void returnData()
    {
        Intent returnIntent = new Intent();

        returnIntent.putExtra("bathroom",(Bathroom)b);

        getActivity().setResult(SAVE_BATHROOM_CODE, returnIntent);
        getActivity().finish();
    }
}
