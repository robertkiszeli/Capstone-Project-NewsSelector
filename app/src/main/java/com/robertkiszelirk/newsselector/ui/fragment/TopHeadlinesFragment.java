package com.robertkiszelirk.newsselector.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.robertkiszelirk.newsselector.R;

import java.util.ArrayList;
import java.util.List;

public class TopHeadlinesFragment extends Fragment {

    private View mainView;

    @SuppressLint("StaticFieldLeak")
    private static SwipeRefreshLayout headlinesSwipeRefreshLayout;

    private Toolbar toolbar;

    public TopHeadlinesFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Set fragment layout
        mainView = inflater.inflate(R.layout.fragment_top_headlines, container, false);

        // Set swipe refresh
        headlinesSwipeRefreshLayout = mainView.findViewById(R.id.top_headlines_swipe_refresh_layout);
        headlinesSwipeRefreshLayout.setProgressViewOffset(false,0,300);
        headlinesSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                setArticlesList();

            }
        });

        // Set toolbar
        toolbar = mainView.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryText));
        // Load SharedPreferences data
        if(getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("country", Context.MODE_PRIVATE);
            // Set toolbar title
            toolbar.setTitle(getString(R.string.top_headlines_title) + sharedPreferences.getString("country", ""));
        }else{
            toolbar.setTitle(getString(R.string.top_headlines_title));
        }
        // Check if getActivity is getting back activity
        if(getActivity() != null) {

            toolbar.hasFocus();
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            assert actionbar != null;
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        setArticlesList();

        return mainView;
    }

    private void setArticlesList() {

        // Add fragments to the viewPager
        ViewPager viewPager = mainView.findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        // Add viewPager to tabLayout
        TabLayout tabLayout = mainView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {

        // Default values
        String country = "United States";
        String countryId = "us";

        // Create new viewPager adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());

        // Check activity and context
        if((getActivity() != null)&&(getContext() != null)) {

            // Load SharedPreferences data
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getContext().getString(R.string.shared_pref_name), Context.MODE_PRIVATE);

            // Get mani data from SharedPreferences
            country = sharedPreferences.getString("country", "United States");
            countryId = sharedPreferences.getString("countryId", "us");

            // Set toolbar title
            toolbar.setTitle(getString(R.string.top_headlines_title) + country);

            // Set all category that is selected
            for(int i = 0; i < getResources().getStringArray(R.array.categories).length; i++){
                if (sharedPreferences.getBoolean(getResources().getStringArray(R.array.categories)[i], true)) {
                    adapter.addFragment(new CategoriesFragment().newCategoriesFragment(countryId, getResources().getStringArray(R.array.categories)[i]), getResources().getStringArray(R.array.categories_text)[i]);
                }
            }

        }else{

            // Set default toolbar text
            toolbar.setTitle(getString(R.string.top_headlines_title) + country);

            // Set default categories
            for(int i = 0; i < getResources().getStringArray(R.array.categories).length; i++){
                adapter.addFragment(new CategoriesFragment().newCategoriesFragment(countryId, getResources().getStringArray(R.array.categories)[i]), getResources().getStringArray(R.array.categories_text)[i]);
            }
        }

        // Add adapter to viewPager
        viewPager.setAdapter(adapter);
    }

    // Custom ViewPagerAdapter
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        // Main data list of fragments and category names
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // Handle swipe refresh progress bar visibility
    public static void swipeProgressGone(){
        headlinesSwipeRefreshLayout.setRefreshing(false);
    }

}
