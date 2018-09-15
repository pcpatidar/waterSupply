package com.example.berylsystems.watersupply.fragment.customer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.adapter.OrderListAdapter;
import com.example.berylsystems.watersupply.bean.OrderBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;
import com.example.berylsystems.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderListFragment extends Fragment {
    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    boolean isExit;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    LinearLayoutManager linearLayoutManager;
    OrderListAdapter mAdapter;
    List<OrderBean> orderBeanList;
    ValueEventListener firstValueListener;
    AppUser appUser;
    String mobileNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_order_list_fragment, container, false);
        ButterKnife.bind(this, view);
        ButterKnife.bind(getActivity());
        appUser = LocalRepositories.getAppUser(getActivity());
        mobileNumber=appUser.user.getMobile();
        orderBeanList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
//        progressDialog.setCancelable(false);
        if (Helper.isNetworkAvailable(getActivity())) {
            progressDialog.show();
        }
        getAllRecord(ParameterConstants.ORDER);
        databaseReference.addValueEventListener(firstValueListener);
        return view;
    }

    void setAdapter() {
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        Collections.reverse(orderBeanList);
        mAdapter = new OrderListAdapter(getActivity(), orderBeanList);
        mRecyclerView.setAdapter(mAdapter);
    }

    void getAllRecord(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(key);
        firstValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                int i=0;
                orderBeanList.clear();
                if (dataSnapshot.getValue() != null) {
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    long count = dataSnapshot.getChildrenCount();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    while (iterator.hasNext()) {
                        i++;
                        DataSnapshot snapshot = iterator.next();
                        final OrderBean orderBean = (OrderBean) snapshot.getValue(OrderBean.class);
                        if (orderBean.getUser().getMobile().equals(mobileNumber)){
                            orderBeanList.add(orderBean);
                        }
                        if (i == count) {
                            setAdapter();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

    }
}
