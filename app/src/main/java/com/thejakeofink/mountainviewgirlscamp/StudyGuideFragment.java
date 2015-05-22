package com.thejakeofink.mountainviewgirlscamp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;


public class StudyGuideFragment extends Fragment implements View.OnClickListener {

    public static final int FAITH = 1;
    public static final int REVELATION = 2;
    public static final int TEMPTATION = 3;
    public static final int THEME = 4;
    public static final String KEY_FILE_TO_LOAD = "fileToLoad";

    WebView studyGuideView;
	Button studyDone;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.activity_study_guide, container, false);

		studyGuideView = (WebView) rootView.findViewById(R.id.wbv_study_guide);
		studyDone = (Button) rootView.findViewById(R.id.btn_study_done);

		studyDone.setOnClickListener(this);

		Bundle bundle = getArguments();

		if (bundle != null) {
            switch (bundle.getInt(KEY_FILE_TO_LOAD)) {
                case FAITH:
                    studyGuideView.loadUrl("file:///android_asset/FaithFriendshipsStudyGuide.htm");
                    break;
                case REVELATION:
                    studyGuideView.loadUrl("file:///android_asset/PersonalRevelationTempleStudyGuide.htm");
                    break;
                case TEMPTATION:
                    studyGuideView.loadUrl("file:///android_asset/TemptationStudyGuide.htm");
                    break;
                case THEME:
                    studyGuideView.loadUrl("file:///android_asset/YoungWomenThemeStudyGuide.htm");
                    break;
            }
        } else {
			studyGuideView.loadUrl("file:///android_asset/Temple Spotlight.htm");
			studyDone.setVisibility(View.GONE);
		}

		return rootView;
	}

	@Override
	public void onClick(View v) {
		if (v == studyDone) {
			getFragmentManager().popBackStack();
		}
	}
}
