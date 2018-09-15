package com.example.berylsystems.watersupply.fragment.customer;

import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.adapter.customer.SupplierListAdapter;
import com.example.berylsystems.watersupply.bean.UserBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SupplierListFragment extends Fragment {
    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    LinearLayoutManager linearLayoutManager;
    SupplierListAdapter mAdapter;
    public static List<UserBean> userBeanList;
    ValueEventListener valueEventListener;
    AppUser appUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.supplier_list_fragment, container, false);
        ButterKnife.bind(this, view);
        ButterKnife.bind(getActivity());
        appUser = LocalRepositories.getAppUser(getActivity());
        userBeanList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
//        progressDialog.setCancelable(false);
        if (Helper.isNetworkAvailable(getActivity())) {
            progressDialog.show();
        }
        getAllRecord("Supplier");
        databaseReference.addValueEventListener(valueEventListener);
        return view;
    }

    void getAllRecord(String key) {
        databaseReference = FirebaseDatabase.getInstance().getReference(key);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                userBeanList.clear();
                if (dataSnapshot.getValue() != null) {
                    int i=0;
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    long count = dataSnapshot.getChildrenCount();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    while (iterator.hasNext()) {
                        i++;
                        DataSnapshot snapshot = iterator.next();
                        final UserBean userBean = (UserBean) snapshot.getValue(UserBean.class);
                        Location lStart = new Location(LocationManager.NETWORK_PROVIDER);
                        lStart.setLatitude(Double.parseDouble(appUser.user.getLatitude()));
                        lStart.setLongitude(Double.parseDouble(appUser.user.getLongitude()));

                        Location lEnd = new Location(LocationManager.NETWORK_PROVIDER);
                        lEnd.setLatitude(Double.parseDouble(userBean.getLatitude()));
                        lEnd.setLongitude(Double.parseDouble(userBean.getLongitude()));
                        if (lStart.distanceTo(lEnd) <= Double.valueOf(userBean.getDeliveryDistance().trim().split(" ")[0])*1000) {
                            userBeanList.add(userBean);
                        }
                    }
                    if (i == count) {
                        setAdapter();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    void setAdapter() {
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new SupplierListAdapter(getActivity(), userBeanList);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }
}
