package com.example.berylsystems.watersupply.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.fragment.supplier.DeliveredOrderFragment;
import com.example.berylsystems.watersupply.fragment.supplier.PendingOrderFragment;
import com.example.berylsystems.watersupply.notification.service.Config;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shadab Azam Farooqui on 6/25/2018.
 */

public class SupplierHomeActivity extends AppCompatActivity {
    @Bind(R.id.viewpager)
    ViewPager mHeaderViewPager;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    boolean isExit;
    AppUser appUser;
    public static boolean bool;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_supplier);
        ButterKnife.bind(this);

        Helper.initActionbar(this, getSupportActionBar(), "Home Page", false);
        appUser = LocalRepositories.getAppUser(this);
        setupViewPager(mHeaderViewPager);
        mTabLayout.setupWithViewPager(mHeaderViewPager);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

//                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
//                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
//                    displayFirebaseRegId();
//                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//                    String message = intent.getStringExtra("message");
//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
//                }
            }
        };

//        displayFirebaseRegId();

    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        if (!TextUtils.isEmpty(regId))
            Toast.makeText(this, "Firebase Reg Id: "+regId, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Firebase Reg Id: "+regId, Toast.LENGTH_SHORT).show();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PendingOrderFragment(), "Pending Order");
        adapter.addFragment(new DeliveredOrderFragment(), "Delivered Order");
        viewPager.setAdapter(adapter);
    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        buildAlertMessageNoGps();

        mHeaderViewPager.setCurrentItem(1, true);

        if (!Helper.isNetworkAvailable(getApplicationContext())) {
            Snackbar.make(coordinatorLayout, "Please check your network connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        if (bool) {
            mHeaderViewPager.setCurrentItem(1, true);
        } else {
            mHeaderViewPager.setCurrentItem(0, true);
        }
        versionCheck();
    }

    void versionCheck() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("version");
        databaseReference.child("version").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String version = dataSnapshot.getValue(String.class);
                        PackageInfo packageInfo = null;
                        try {
                            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String versionName = packageInfo.versionName;
                        if (!version.equals(versionName)) {
                            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(SupplierHomeActivity.this);
                            alert.setCancelable(false);
                            alert.setMessage("A new version of Water Supply is available.Please update to version " + version + " now.");
                            alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse("https://play.google.com/store/apps"));
                                    startActivity(i);
                                }
                            });
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                            alert.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (isExit) {
            super.onBackPressed();
        }
        isExit = true;
        Toast.makeText(this, "Press back again to Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isExit = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            appUser.user = null;
            LocalRepositories.saveAppUser(getApplicationContext(), appUser);
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void buildAlertMessageNoGps() {
        LocationManager service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            @SuppressLint("RestrictedApi") final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            return;
        }

    }

}
