package com.example.user.lstapp;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;


public class HomeActivity extends ActionBarActivity implements
        SettingsFragment.OnFragmentInteractionListener, ActionBar.TabListener {

    private Fragment mSettingFragment = null;
    private Fragment mMapFragment = null;

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static boolean mTracking = false;
    private static int mMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up the action bar to show tabs.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // for each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText(R.string.action_settings)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.action_map)
                .setTabListener(this));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, show the tab contents in the
        // container view.
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag(tab.getText().toString());
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
//            mFragment = Fragment.instantiate(mActivity, mClass.getName());
//            ft.add(android.R.id.content, mFragment, mTaint position = getSupportActionBar().getSelectedNavigationIndex ();
            int position = getSupportActionBar().getSelectedNavigationIndex ();
            if (position == 0){
                mFragment = SettingsFragment.newInstance("str1", "str2");
            } else {
                mFragment = new MapFragment();
            }
            Bundle args = new Bundle();
//        args.putInt(DummySectionFragment.ARG_SECTION_NUMBER,
//                tab.getPosition() + 1);
            mFragment.setArguments(args);
            FragmentTransaction f = getSupportFragmentManager().beginTransaction();
            f.add(R.id.container, mFragment, tab.getText().toString());
            //f.addToBackStack(tab.getText().toString());
            f.commit();
        } else {
            // If it exists, simply attach it in order to show it
            ft.show(mFragment);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag(tab.getText().toString());
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.hide(mFragment);
            //ft.addToBackStack(tab.getText().toString());
            //ft.commit();
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
        int i = 0;
        i++;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void notifyTracking(boolean nStatus){
    }
}
