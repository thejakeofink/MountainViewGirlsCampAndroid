package com.thejakeofink.mountainviewgirlscamp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class StudyGuideSelectActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_guide_select);

        findViewById(R.id.btn_rev).setOnClickListener(this);
        findViewById(R.id.btn_temptation).setOnClickListener(this);
        findViewById(R.id.btn_faith).setOnClickListener(this);
        findViewById(R.id.btn_theme).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent studyIntent = new Intent(this, StudyGuideActivity.class);
        switch (v.getId()) {
            case R.id.btn_rev:
                studyIntent.putExtra(StudyGuideActivity.KEY_FILE_TO_LOAD, StudyGuideActivity.REVELATION);
                break;
            case R.id.btn_temptation:
                studyIntent.putExtra(StudyGuideActivity.KEY_FILE_TO_LOAD, StudyGuideActivity.TEMPTATION);
                break;
            case R.id.btn_faith:
                studyIntent.putExtra(StudyGuideActivity.KEY_FILE_TO_LOAD, StudyGuideActivity.FAITH);
                break;
            case R.id.btn_theme:
                studyIntent.putExtra(StudyGuideActivity.KEY_FILE_TO_LOAD, StudyGuideActivity.THEME);
                break;
        }
        startActivity(studyIntent);
    }
}
