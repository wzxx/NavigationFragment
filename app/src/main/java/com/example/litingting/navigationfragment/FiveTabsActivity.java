package com.example.litingting.navigationfragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.bottomnavigation.MainNavigationTabBar;
import com.example.litingting.navigationfragment.fragment.FavoriteFragment;
import com.example.litingting.navigationfragment.fragment.FoodFragment;
import com.example.litingting.navigationfragment.fragment.FriendFragment;
import com.example.litingting.navigationfragment.fragment.NearByFragment;
import com.example.litingting.navigationfragment.fragment.RecentFragment;

/**
 * Created by wzxx on 16/8/10.
 */
public class FiveTabsActivity extends AppCompatActivity{

    private static final String TAG_PAGE_RECENT = "Recents";
    private static final String TAG_PAGE_FAVORITE = "Favorites";
    private static final String TAG_PAGE_NEARBY = "Nearby";
    private static final String TAG_PAGE_FRIEND = "Friends";
    private static final String TAG_PAGE_FOOD = "Food";

    private MainNavigationTabBar mainNavigationTabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_show);

        mainNavigationTabBar = (MainNavigationTabBar) findViewById(R.id.mainTabBar);

        mainNavigationTabBar.onRestoreInstanceState(savedInstanceState);

        mainNavigationTabBar.addTab(RecentFragment.class,new MainNavigationTabBar.TabParam(R.drawable.ic_recents,R.drawable.ic_recents,TAG_PAGE_RECENT));
        mainNavigationTabBar.addTab(FavoriteFragment.class,new MainNavigationTabBar.TabParam(R.drawable.ic_favorites,R.drawable.ic_favorites,TAG_PAGE_FAVORITE));
        mainNavigationTabBar.addTab(NearByFragment.class,new MainNavigationTabBar.TabParam(R.drawable.ic_nearby,R.drawable.ic_nearby,TAG_PAGE_NEARBY));

//        mainNavigationTabBar.setSelectedTextColor(Color.YELLOW);
//        mainNavigationTabBar.setTabTextColor(Color.BLUE);

        mainNavigationTabBar.addTab(FriendFragment.class,new MainNavigationTabBar.TabParam(R.drawable.ic_friends,R.drawable.ic_friends,TAG_PAGE_FRIEND));
        mainNavigationTabBar.addTab(FoodFragment.class,new MainNavigationTabBar.TabParam(R.drawable.ic_restaurants,R.drawable.ic_restaurants,TAG_PAGE_FOOD));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       mainNavigationTabBar.onSaveInstanceState(outState);
    }
}
