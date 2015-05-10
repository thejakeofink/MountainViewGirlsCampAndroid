package com.thejakeofink.mountainviewgirlscamp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by Jacob Stokes on 5/9/15.
 */
public class InitialPageAdapter extends FragmentPagerAdapter {

	final FragmentManager mFragmentManager;
	private FragmentTransaction mCurTransaction = null;
	private Fragment mCurrentPrimaryItem = null;

	SparseArray<Fragment> fragments = new SparseArray<>();

	public InitialPageAdapter(FragmentManager fm) {
		super(fm);
		mFragmentManager = fm;
		notifyDataSetChanged();
	}

	@Override
	 public int getCount() {
		return InitialPageActivity.NUM_ITEMS;
	}

	public synchronized Fragment getFragmentById(int id) {
		return getOrInstantiateItem(id);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public synchronized Fragment getItem(int position) {
		long itemId = getItemId(position);
		Fragment frag = getOrInstantiateItem(itemId);
		return frag;
	}

	private Fragment getOrInstantiateItem(long itemId) {
		Fragment frag = fragments.get((int) itemId);

		if (frag == null && mFragmentManager != null) {
			frag = mFragmentManager.findFragmentByTag(makeFragmentName(itemId));
		}

		if (frag == null) {
			if (itemId == InitialPageActivity.PAGE_PHOTOS) {
				frag = new FlickrPhotoAlbumFragment();
			} else if (itemId == InitialPageActivity.PAGE_STUDY) {
				//TODO: Add study fragment
			} else if (itemId == InitialPageActivity.PAGE_QUOTES) {
				//TODO: Add Quotes fragment
			} else if (itemId == InitialPageActivity.PAGE_GAME) {
				//TODO: Add Game fragment
			}
		}

		fragments.put((int)itemId, frag);
		return frag;
	}

	public static String makeFragmentName(long id) {
		return "thejakeofink:mountainview:pager:" + getNameForId(id);
	}

	private static String getNameForId(long id) {
		return ""+id;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}

		final long itemId = getItemId(position);

		// Do we already have this fragment?
		String name = makeFragmentName(itemId);
		Fragment fragment = mFragmentManager.findFragmentByTag(name);

		if (fragment != null && fragment.getId() != container.getId()) {
			// The containers aren't the same, our fragment must have been added
			// to another parent.  Remove the fragment before taking it back.
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.remove(fragment);
			ft.commitAllowingStateLoss();
			mFragmentManager.executePendingTransactions();
			fragments.put((int)itemId, fragment); /* Make sure it retrievable below */
			fragment = null;
		}

		if (fragment != null) {
			Log.v("Jake", "Attaching item #" + itemId + ": f=" + fragment);
			mCurTransaction.attach(fragment);
		} else {
			fragment = getItem(position);
			Log.v("Jake", "Adding item #" + itemId + ": f=" + fragment);
			if (fragment != null) {
				mCurTransaction.add(container.getId(), fragment, makeFragmentName(itemId));
			}
		}
		if (fragment != mCurrentPrimaryItem) {
			fragment.setMenuVisibility(false);
			fragment.setUserVisibleHint(false);
		}

		if (fragment != null) {
			fragments.put((int)itemId, fragment);
		}

		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		Log.v("Jake", "Detaching item #" + getItemId(position) + ": f=" + object
				+ " v=" + ((Fragment)object).getView());
		mCurTransaction.detach((Fragment)object);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		Fragment fragment = (Fragment)object;
		if (fragment != mCurrentPrimaryItem) {
			if (mCurrentPrimaryItem != null) {
				mCurrentPrimaryItem.setMenuVisibility(false);
				mCurrentPrimaryItem.setUserVisibleHint(false);
			}
			if (fragment != null) {
				fragment.setMenuVisibility(true);
				fragment.setUserVisibleHint(true);
			}
			mCurrentPrimaryItem = fragment;
		}
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
			mFragmentManager.executePendingTransactions();
		}
	}
}
