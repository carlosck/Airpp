package mx.rdy.android.airpp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Created by Seca on 9/9/15.
 */
public class DetailFragment extends Fragment {
    private static final String TAG="android.rdy.mx.airpp";
    static final int DETAIL_BATHROOM_CODE = 2;
    private Bathroom b;

    LinearLayout mContainer ;
    TextView mTitle_text;
    TextView mLocation_text;
    TextView mDirection_text;

    TableLayout iconContainer;
    TextView mMix_text;
    TextView mHandicap_text;
    TextView mBaby_text;
    TextView mPaper_text;
    TextView mFree_text;
    TextView mWater_text;

    RatingBar mStars;

    TableLayout switchContainer;
    Switch mMix_switch;
    Switch mHandicap_switch;
    Switch mBaby_switch;
    Switch mPaper_switch;
    Switch mFree_switch;
    Switch mWater_switch;

    Button mReviewButton;
    Button mCancelButton;
    Button mOkButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getActivity().getIntent();
        b=(Bathroom)myIntent.getSerializableExtra("bathroom");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_layout, container, false);
        cacheElements(v);
        return v;
    }

    public static DetailFragment newInstance()
    {
        Bundle args= new Bundle();
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public void cacheElements(View v)
    {
        mContainer = (LinearLayout) v.findViewById(R.id.detail_container);
        mTitle_text = (TextView) v.findViewById(R.id.detail_title);
        mLocation_text = (TextView) v.findViewById(R.id.detail_location);
        mDirection_text = (TextView) v.findViewById(R.id.detail_direction);

        iconContainer = (TableLayout) v.findViewById(R.id.detail_table_icons);
        mMix_text= (TextView) v.findViewById(R.id.detail_icon_mix);
        mHandicap_text= (TextView) v.findViewById(R.id.detail_icon_handicap);
        mBaby_text= (TextView) v.findViewById(R.id.detail_icon_baby);
        mPaper_text= (TextView) v.findViewById(R.id.detail_icon_paper);
        mFree_text= (TextView) v.findViewById(R.id.detail_icon_free);
        mWater_text= (TextView) v.findViewById(R.id.detail_icon_water);

        mStars= (RatingBar) v.findViewById(R.id.starsBar);
        mReviewButton= (Button) v.findViewById(R.id.detailReview);
        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReviewForm();
            }
        });

        switchContainer = (TableLayout) v.findViewById(R.id.detail_table_review);
        mMix_switch = (Switch)v.findViewById(R.id.mix_switch);
        mHandicap_switch = (Switch)v.findViewById(R.id.handicap_switch);
        mBaby_switch = (Switch)v.findViewById(R.id.baby_switch);
        mPaper_switch = (Switch)v.findViewById(R.id.paper_switch);
        mFree_switch = (Switch)v.findViewById(R.id.free_switch);
        mWater_switch = (Switch)v.findViewById(R.id.water_switch);
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

        printData();
    }
    public void printData()
    {
        switch(b.getStars())
        {
            case 1:
            case 2: mContainer.setBackgroundColor(getResources().getColor(R.color.bad));
                mTitle_text.setText(getResources().getString(R.string.bad_title));
                break;
            case 3:
            case 4:
                mContainer.setBackgroundColor(getResources().getColor(R.color.good));
                mTitle_text.setText(getResources().getString(R.string.good_title));
                break;
            case 5:
                mContainer.setBackgroundColor(getResources().getColor(R.color.perfect));
                mTitle_text.setText(getResources().getString(R.string.perfect_title));
                break;

        }
        mLocation_text.setText(b.getLocation());
        mDirection_text.setText(b.getDirection());

        if(!b.isMix()) mMix_text.setVisibility(View.GONE);
        if(!b.isHandicap()) mHandicap_text.setVisibility(View.GONE);
        if(!b.isBaby()) mBaby_text.setVisibility(View.GONE);
        if(!b.isPaper()) mPaper_text.setVisibility(View.GONE);
        if(!b.isFree()) mFree_text.setVisibility(View.GONE);
        if(!b.isWater()) mWater_text.setVisibility(View.GONE);
        mStars.setRating(b.getStars());

        mMix_switch.setChecked(b.isMix());
        mHandicap_switch.setChecked(b.isHandicap());
        mBaby_switch.setChecked(b.isBaby());
        mPaper_switch.setChecked(b.isPaper());
        mFree_switch.setChecked(b.isFree());
        mWater_switch.setChecked(b.isWater());


    }
    public void saveData()
    {
        Log.i(TAG, "save");
        b.setMix(mMix_switch.isChecked());
        b.setHandicap(mHandicap_switch.isChecked());
        b.setBaby(mBaby_switch.isChecked());
        b.setPaper(mPaper_switch.isChecked());
        b.setFree(mFree_switch.isChecked());
        b.setWater(mWater_switch.isChecked());
        b.setStars((int)(mStars.getRating()));
        b.saveDetails();
        returnData();
    }

    public void cancelData()
    {
        //getActivity().setResult(1);
        Log.i(TAG,"close");
        Intent returnIntent = new Intent();
        getActivity().setResult(DETAIL_BATHROOM_CODE, returnIntent);
        getActivity().finish();
    }
    public void returnData()
    {
        Intent returnIntent = new Intent();

        returnIntent.putExtra("bathroom",(Bathroom)b);

        getActivity().setResult(DETAIL_BATHROOM_CODE, returnIntent);
        getActivity().finish();
    }
    public void openReviewForm()
    {
        iconContainer.setVisibility(View.GONE);
        mReviewButton.setVisibility(View.GONE);
        mOkButton.setVisibility(View.VISIBLE);
        switchContainer.setVisibility(View.VISIBLE);
        mStars.setIsIndicator(false);

    }
}
