package com.thejakeofink.mountainviewgirlscamp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class StudyGuideSelectFragment extends Fragment implements View.OnClickListener {

	private static final String FRAGMENT_TAG = "StudyFragmentTag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_study_guide_select, container, false);

        rootView.findViewById(R.id.btn_rev).setOnClickListener(this);
        rootView.findViewById(R.id.btn_temptation).setOnClickListener(this);
        rootView.findViewById(R.id.btn_faith).setOnClickListener(this);
        rootView.findViewById(R.id.btn_theme).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
		Bundle studyBundle = new Bundle();
        switch (v.getId()) {
            case R.id.btn_rev:
                studyBundle.putInt(StudyGuideFragment.KEY_FILE_TO_LOAD, StudyGuideFragment.REVELATION);
                break;
            case R.id.btn_temptation:
                studyBundle.putInt(StudyGuideFragment.KEY_FILE_TO_LOAD, StudyGuideFragment.TEMPTATION);
                break;
            case R.id.btn_faith:
                studyBundle.putInt(StudyGuideFragment.KEY_FILE_TO_LOAD, StudyGuideFragment.FAITH);
                break;
            case R.id.btn_theme:
                studyBundle.putInt(StudyGuideFragment.KEY_FILE_TO_LOAD, StudyGuideFragment.THEME);
                break;
        }

		StudyGuideFragment frag = new StudyGuideFragment();
		frag.setArguments(studyBundle);

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.addToBackStack(FRAGMENT_TAG);
        ft.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom, R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
		ft.add(R.id.study_guide_frag_container, frag, FRAGMENT_TAG);
		ft.commit();
    }
}
