package com.example.berylsystems.watersupply.adapter.supplier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.bean.OrderBean;
import com.example.berylsystems.watersupply.fragment.supplier.DeliveredOrderFragment;
import com.example.berylsystems.watersupply.fragment.supplier.PendingOrderFragment;
import com.example.berylsystems.watersupply.utils.Helper;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeliveredOrderListAdapter extends RecyclerView.Adapter<DeliveredOrderListAdapter.ViewHolder> {

    private List<OrderBean> data;
    private Activity context;
    View mConvertView;
    ProgressDialog mProgressDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    public DeliveredOrderListAdapter(Activity context, List<OrderBean> data, SwipeRefreshLayout swipeRefreshLayout) {
        this.data = data;
        this.context = context;
        this.swipeRefreshLayout=swipeRefreshLayout;

    }


    @Override
    public DeliveredOrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_delivered_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeliveredOrderListAdapter.ViewHolder viewHolder, int position) {

        viewHolder.shopName.setText(data.get(position).getSupplier().getShopName());
        viewHolder.bookingTime.setText(data.get(position).getBookingDate());
        String[] dateAr = data.get(position).getDeliveryDate().split(" ");
        viewHolder.day.setText(dateAr[0]);
        viewHolder.month.setText(dateAr[1]);
        viewHolder.year.setText(dateAr[2]);
        viewHolder.supplierName.setText(data.get(position).getSupplier().getName());
        viewHolder.supplierMobile.setText(data.get(position).getUser().getMobile());
        viewHolder.address.setText(data.get(position).getAddress());
        viewHolder.total.setText(data.get(position).getAmount());
        viewHolder.orderId.setText(data.get(position).getOrderId().toUpperCase());
        if (data.get(position).getComment().trim().isEmpty()) {
            viewHolder.comment.setVisibility(View.GONE);
        } else {
            viewHolder.comment.setVisibility(View.VISIBLE);
            viewHolder.comment.setText(data.get(position).getComment());
        }
        removeAllViews(viewHolder);
        try {
            for (int i = 0; i < data.get(position).getWaterTypeQuantity().size(); i++) {
                String str = data.get(position).getWaterTypeQuantity().get(i);
                if (!str.contains("=")) {
                    String[] strAr = str.split(",");
                    String name ;
                    String wQty;
                    String bQty ;
                    String rate;
//                    Normal Water,10,1
                    if (strAr.length==3){
                        name = strAr[0];
                        wQty = strAr[2];
                        bQty = "0";
                        rate = strAr[1];
                    }else {
//                        Cold water,150'1
                        name = strAr[0];
                        wQty = "0";
                        bQty = strAr[1].split("'")[1];
                        rate = strAr[1].split("'")[0];
                    }
                    addView(name, wQty, bQty, rate, viewHolder);
                } else if (str.contains("=")) {
                    //Normal Water,10,1=Normal Water,150,1
                    String[] strAr = str.split("=");
                    String str1 = strAr[0];
                    String str2 = strAr[1];

                    String[] s1 = str1.split(",");
                    String[] s2 = str2.split(",");

                    String name = s1[0];
                    String wQty = s1[2];
                    String bQty = s2[1].split("'")[1];
                    String rate= String.valueOf(Double.valueOf(s1[1])+Double.valueOf(s2[1].split("'")[0]));
                    addView(name, wQty, bQty, rate, viewHolder);
                }
            }
        } catch (Exception e) {
        }
        viewHolder.deliver_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(context)
                        .setTitle("Cancel Confirmation")
                        .setMessage("Do you want to cancel ?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (!Helper.isNetworkAvailable(context)) {
                                Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mProgressDialog=new ProgressDialog(context);
                            mProgressDialog.setMessage("Please wait...");
                            swipeRefreshLayout.setRefreshing(true);
//                            mProgressDialog.show();
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Order");
                            OrderBean orderBean=data.get(position);
                            orderBean.setStatus(false);
                            database.child(data.get(position).getOrderId()).setValue(orderBean, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        swipeRefreshLayout.setRefreshing(false);
                                        mProgressDialog.dismiss();
                                        DeliveredOrderFragment.orderBeanList.remove(orderBean);
                                        PendingOrderFragment.orderBeanList.add(orderBean);
                                        PendingOrderFragment.mAdapter.notifyDataSetChanged();
                                        DeliveredOrderFragment.mAdapter.notifyDataSetChanged();
                                    } else {
                                        mProgressDialog.dismiss();
                                    }
                                }
                            });

                        })
                        .setNegativeButton("Cancel", null)
                        .show();




            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    void addView(String name, String wQty, String bQty, String rate, DeliveredOrderListAdapter.ViewHolder viewHolder) {
        mConvertView = context.getLayoutInflater().inflate(R.layout.dynamic_show_order, null);
        TextView orderName = ((TextView) mConvertView.findViewById(R.id.orderName));
        TextView waterQty = ((TextView) mConvertView.findViewById(R.id.waterQty));
        TextView bottleQty = ((TextView) mConvertView.findViewById(R.id.bottleQty));
        TextView orderRate = ((TextView) mConvertView.findViewById(R.id.orderRate));
        orderName.setText(name/*+" (\u20B9"+rate+")"*/);
        waterQty.setText(wQty);
        bottleQty.setText(bQty);
        orderRate.setText("" + rate/*Double.valueOf(qty) * Double.valueOf(rate)*/);
        ((ViewGroup) viewHolder.parentLayout).addView(mConvertView);
    }
    void removeAllViews(ViewHolder viewHolder) {
        mConvertView = context.getLayoutInflater().inflate(R.layout.dynamic_show_order, null);
        ((ViewGroup) viewHolder.parentLayout).removeAllViews();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.shopName)
        TextView shopName;
        @Bind(R.id.day)
        TextView day;
        @Bind(R.id.month)
        TextView month;
        @Bind(R.id.year)
        TextView year;
        @Bind(R.id.bookingTime)
        TextView bookingTime;
        @Bind(R.id.supplierName)
        TextView supplierName;
        @Bind(R.id.supplierMobile)
        TextView supplierMobile;
        @Bind(R.id.address)
        TextView address;
        @Bind(R.id.total)
        TextView total;
        @Bind(R.id.comment)
        TextView comment;
        @Bind(R.id.orderId)
        TextView orderId;
        @Bind(R.id.parentLayout)
        LinearLayout parentLayout;
        @Bind(R.id.deliver_layout)
        LinearLayout deliver_layout;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);

        }
    }
}
