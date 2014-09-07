package com.thejakeofink.mountainviewgirlscamp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class InitialPageActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);

        findViewById(R.id.btn_study_guides).setOnClickListener(this);
        findViewById(R.id.btn_pictures).setOnClickListener(this);
        findViewById(R.id.btn_quotes).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_study_guides:
                // TODO: go to study guides...
                Intent studyIntent = new Intent(this, StudyGuideSelectActivity.class);
                startActivity(studyIntent);
                break;
            case R.id.btn_pictures:
                // TODO: go to pictures...
                Intent albumsIntent = new Intent(this, FlickrPhotoAlbumActivity.class);
                startActivity(albumsIntent);
                break;
            case R.id.btn_quotes:
                // TODO: go to quotes...
                Intent quotesIntent = new Intent(this, StudyGuideActivity.class);
                quotesIntent.putExtra(StudyGuideActivity.KEY_FILE_TO_LOAD, StudyGuideActivity.QUOTES);
                startActivity(quotesIntent);
                break;
        }
    }
}
