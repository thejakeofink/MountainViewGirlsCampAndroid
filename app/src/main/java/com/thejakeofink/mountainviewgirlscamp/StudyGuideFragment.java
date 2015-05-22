package com.thejakeofink.mountainviewgirlscamp;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


public class StudyGuideFragment extends Fragment {

    public static final int QUOTES = 0;
    public static final int FAITH = 1;
    public static final int REVELATION = 2;
    public static final int TEMPTATION = 3;
    public static final int THEME = 4;
    public static final String KEY_FILE_TO_LOAD = "fileToLoad";

    WebView studyGuideView;
    ActionBar actionBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.activity_study_guide, container, false);

		studyGuideView = (WebView) rootView.findViewById(R.id.wbv_study_guide);

		studyGuideView.loadUrl("file:///android_asset/Temple Spotlight.htm");

		return rootView;
	}

//	@Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_study_guide);
//
//        Bundle bundle = getIntent().getExtras();
//
//        studyGuideView = (WebView) findViewById(R.id.wbv_study_guide);
//
//        actionBar = getActionBar();
//
//        String title = "";
//
//        if (bundle != null) {
//            switch (bundle.getInt(KEY_FILE_TO_LOAD)) {
//                case QUOTES:
//                    studyGuideView.loadUrl("file:///android_asset/Temple Spotlight.htm");
//                    title = getResources().getString(R.string.quotes);
//                    break;
//                case FAITH:
//                    studyGuideView.loadUrl("file:///android_asset/FaithFriendshipsStudyGuide.htm");
//                    title = getResources().getString(R.string.faith_friendships);
//                    break;
//                case REVELATION:
//                    studyGuideView.loadUrl("file:///android_asset/PersonalRevelationTempleStudyGuide.htm");
//                    title = getResources().getString(R.string.personal_rev);
//                    break;
//                case TEMPTATION:
//                    studyGuideView.loadUrl("file:///android_asset/TemptationStudyGuide.htm");
//                    title = getResources().getString(R.string.tempation);
//                    break;
//                case THEME:
//                    studyGuideView.loadUrl("file:///android_asset/YoungWomenThemeStudyGuide.htm");
//                    title = getResources().getString(R.string.theme);
//                    break;
//            }
//        }
//
//        if (actionBar != null) {
//            actionBar.setTitle(title);
//        }
//    }
}
