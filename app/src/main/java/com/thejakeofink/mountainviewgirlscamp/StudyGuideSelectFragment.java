package com.thejakeofink.mountainviewgirlscamp;

import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class StudyGuideSelectFragment extends Fragment implements View.OnClickListener {

    private static final String FRAGMENT_TAG = "StudyFragmentTag";

    private boolean buttonsHidden = false;

    protected Button revelationBtn;
    protected Button temptationBtn;
    protected Button faithBtn;
    protected Button themeBtn;
    protected View rootView;
    protected View buttonContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_study_guide_select, container, false);

        revelationBtn = (Button) rootView.findViewById(R.id.btn_rev);
        temptationBtn = (Button) rootView.findViewById(R.id.btn_temptation);
        faithBtn = (Button) rootView.findViewById(R.id.btn_faith);
        themeBtn = (Button) rootView.findViewById(R.id.btn_theme);
        buttonContainer = rootView.findViewById(R.id.button_container);

        revelationBtn.setOnClickListener(this);
        temptationBtn.setOnClickListener(this);
        faithBtn.setOnClickListener(this);
        themeBtn.setOnClickListener(this);

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
        ft.setCustomAnimations(R.anim.up_from_bottom, R.anim.out_through_bottom, R.anim.up_from_bottom, R.anim.out_through_bottom);
        ft.add(R.id.study_guide_frag_container, frag, FRAGMENT_TAG);
        ft.commit();
    }

    public void swapButtonVisibility() {
        if (buttonsHidden) {
            setVisibilityOnButtons(1.0f);
        } else {
            setVisibilityOnButtons(0.0f);
        }
    }

    private void setVisibilityOnButtons(float alpha) {
        buttonContainer.animate().alpha(alpha).setDuration(1000);

        if (alpha == 1.0f) {
            buttonsHidden = false;
        } else {
            buttonsHidden = true;
        }
    }
}
