package com.thejakeofink.mountainviewgirlscamp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class StudyGuideActivity extends Activity {

    public static final int QUOTES = 0;
    public static final int FAITH = 1;
    public static final int REVELATION = 2;
    public static final int TEMPTATION = 3;
    public static final int THEME = 4;
    public static final String KEY_FILE_TO_LOAD = "fileToLoad";

    WebView studyGuideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_guide);

        Bundle bundle = getIntent().getExtras();

        studyGuideView = (WebView) findViewById(R.id.wbv_study_guide);

        if (bundle != null) {
            switch (bundle.getInt(KEY_FILE_TO_LOAD)) {
                case QUOTES:
                    studyGuideView.loadUrl("file:///android_asset/Temple Spotlight.htm");
                    break;
                case FAITH:
                    studyGuideView.loadUrl("file:///android_asset/FaithFriendshipsStudyGuide.htm");
                    break;
                case REVELATION:
                    studyGuideView.loadUrl("file:///android_asset/PersonalRevelationStudyGuide.htm");
                    break;
                case TEMPTATION:
                    studyGuideView.loadUrl("file:///android_asset/TemptationStudyGuide.htm");
                    break;
                case THEME:
                    studyGuideView.loadUrl("file:///android_asset/YoungWomenThemeStudyGuide.htm");
                    break;
            }
        }
    }
}
