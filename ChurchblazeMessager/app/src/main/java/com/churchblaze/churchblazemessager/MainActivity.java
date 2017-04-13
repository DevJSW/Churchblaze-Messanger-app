package com.churchblaze.churchblazemessager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends AppCompatActivity {

    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItems(R.menu.bottombar_menu);
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    // The user selected item number one.



                }

                if (menuItemId == R.id.bottomBarItemTwo) {
                    // The user reselected item number one, scroll your content to top.

                    //ProfileFragment profileFragment = new ProfileFragment();
                   // FragmentManager manager = getSupportFragmentManager();
                   // manager.beginTransaction().replace(R.id.relativelayout_for_fragments, profileFragment).commit();
                }

                if (menuItemId == R.id.bottomBarItemThree) {
                    // The user reselected item number one, scroll your content to top.

                    startActivity(new Intent(MainActivity.this, CameraActivity.class));
                }

                if (menuItemId == R.id.bottomBarItemFour) {
                    // The user reselected item number one, scroll your content to top.


                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    // The user reselected item number one, scroll your content to top.


                }

                if (menuItemId == R.id.bottomBarItemTwo) {
                    // The user reselected item number one, scroll your content to top.

                  //  ProfileFragment profileFragment = new ProfileFragment();
                   // FragmentManager manager = getSupportFragmentManager();
                   // manager.beginTransaction().replace(R.id.relativelayout_for_fragments, profileFragment).commit();
                }

                if (menuItemId == R.id.bottomBarItemThree) {

                    startActivity(new Intent(MainActivity.this, CameraActivity.class));

                }
            }


        });

    }
}
