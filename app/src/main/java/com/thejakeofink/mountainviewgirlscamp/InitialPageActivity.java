package com.thejakeofink.mountainviewgirlscamp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;


public class InitialPageActivity extends ActionBarActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, ActionBar.TabListener {

    public static final int PAGE_STUDY = 0;
    public static final int PAGE_PHOTOS = 1;
    public static final int PAGE_QUOTES = 2;
    public static final int PAGE_GAME = 3;
    public static final int NUM_ITEMS = 4;

    private static int CURRENT_VISIBLE_FRAGMENT = 0;
    ViewPager awesomePager;
    ActionBar actionBar;
    InitialPageAdapter awesomeAdapter;
    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_view_pager);

        setupActionBar();

        awesomePager = (ViewPager) findViewById(R.id.initial_pager);
        awesomeAdapter = new InitialPageAdapter(this.getSupportFragmentManager());

        awesomePager.setAdapter(awesomeAdapter);
        awesomePager.setOnPageChangeListener(this);
        awesomePager.setOffscreenPageLimit(100);
        awesomeAdapter.notifyDataSetChanged();

        getSupportFragmentManager().addOnBackStackChangedListener(getListener());

        background = (ImageView) findViewById(R.id.temple_background);
        Bitmap b = blurImage(BitmapFactory.decodeResource(getResources(), R.drawable.salt_lake_temple), 25);
        background.setImageBitmap(b);
    }

    private void setupActionBar() {

        actionBar = getSupportActionBar();
        actionBar.removeAllTabs();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = actionBar.newTab()
                .setIcon(android.R.drawable.ic_menu_agenda)
                .setTag(PAGE_STUDY)
                .setTabListener(this);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setIcon(android.R.drawable.ic_menu_gallery)
                .setTag(PAGE_PHOTOS)
                .setTabListener(this);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setIcon(android.R.drawable.ic_menu_view)
                .setTag(PAGE_QUOTES)
                .setTabListener(this);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setIcon(android.R.drawable.ic_menu_compass)
                .setTag(PAGE_GAME)
                .setTabListener(this);
        actionBar.addTab(tab);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_study_guides:
//                Intent studyIntent = new Intent(this, StudyGuideSelectFragment.class);
//                startActivity(studyIntent);
//                break;
//            case R.id.btn_pictures:
//                Intent albumsIntent = new Intent(this, FlickrPhotoAlbumFragment.class);
//                startActivity(albumsIntent);
//                break;
//            case R.id.btn_quotes:
//                Intent quotesIntent = new Intent(this, StudyGuideFragment.class);
//                quotesIntent.putExtra(StudyGuideFragment.KEY_FILE_TO_LOAD, StudyGuideFragment.QUOTES);
//                startActivity(quotesIntent);
//                break;
//            case R.id.btn_game:
//                Intent gameIntent = new Intent(this, TriviaGameFragment.class);
//                startActivity(gameIntent);
//                break;
//        }
    }

    private void setCurrentItem(int position) {
        if (awesomePager != null && awesomePager.getCurrentItem() != position) {
            awesomePager.setCurrentItem(position);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static interface OnPageChanged {
        void onEnteringPage(InitialPageActivity activity);

        void onLeavingPage(InitialPageActivity activity);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);

        Fragment oldFragment = (awesomeAdapter != null) ? awesomeAdapter.getFragmentById(CURRENT_VISIBLE_FRAGMENT) : null;
        Fragment newFragment = (awesomeAdapter != null) ? awesomeAdapter.getFragmentById(position) : null;

        // Notify the last fragment it is no longer visible.
        if (oldFragment != null && oldFragment instanceof OnPageChanged) {
            ((OnPageChanged) oldFragment).onLeavingPage(this);
        }

        // Notify the new fragment it is now visible.
        if (newFragment != null && newFragment instanceof OnPageChanged) {
            ((OnPageChanged) newFragment).onEnteringPage(this);
        }

        CURRENT_VISIBLE_FRAGMENT = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {

        FlickrPhotoAlbumFragment frag = (FlickrPhotoAlbumFragment) getSupportFragmentManager().findFragmentByTag(InitialPageAdapter.makeFragmentName(InitialPageActivity.PAGE_PHOTOS));

        if (frag.albumRecyclerView.getAdapter() instanceof FlickrPhotoAlbumFragment.PhotoAdapter && awesomePager.getCurrentItem() == PAGE_PHOTOS) {
            ((FlickrPhotoAlbumFragment.PhotoAdapter) frag.albumRecyclerView.getAdapter()).clear();
            frag.cancelPhotosTask();
            frag.albumRecyclerView.setAdapter(frag.albumAdapter);
            frag.loadAlbums();
        } else {
            super.onBackPressed();
        }
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();

                if (manager != null) {
                    StudyGuideSelectFragment currFrag = (StudyGuideSelectFragment) manager.findFragmentByTag(InitialPageAdapter.makeFragmentName(PAGE_STUDY));
                    if (currFrag != null) {
                        currFrag.swapButtonVisibility();
                    }
                }
            }
        };

        return result;
    }

    public Bitmap blurImage(Bitmap input, float radius) {
        RenderScript rsScript = RenderScript.create(this);
        Allocation alloc = Allocation.createFromBitmap(rsScript, input);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, Element.U8_4(rsScript));
        blur.setRadius(radius);
        blur.setInput(alloc);

        Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
        Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);
        blur.forEach(outAlloc);
        outAlloc.copyTo(result);

        rsScript.destroy();
        return result;
    }
}
