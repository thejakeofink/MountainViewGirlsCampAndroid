package com.thejakeofink.mountainviewgirlscamp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class StudyGuideSelectFragment extends Fragment implements View.OnClickListener {

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
        Intent studyIntent = new Intent(getActivity(), StudyGuideFragment.class);
        switch (v.getId()) {
            case R.id.btn_rev:
                studyIntent.putExtra(StudyGuideFragment.KEY_FILE_TO_LOAD, StudyGuideFragment.REVELATION);
                break;
            case R.id.btn_temptation:
                studyIntent.putExtra(StudyGuideFragment.KEY_FILE_TO_LOAD, StudyGuideFragment.TEMPTATION);
                break;
            case R.id.btn_faith:
                studyIntent.putExtra(StudyGuideFragment.KEY_FILE_TO_LOAD, StudyGuideFragment.FAITH);
                break;
            case R.id.btn_theme:
                studyIntent.putExtra(StudyGuideFragment.KEY_FILE_TO_LOAD, StudyGuideFragment.THEME);
                break;
        }
        startActivity(studyIntent);
    }
}
