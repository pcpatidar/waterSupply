package com.example.berylsystems.watersupply.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class PendingOrderListAdapter extends RecyclerView.Adapter<PendingOrderListAdapter.ViewHolder> {

    private List<OrderBean> data;
    private Activity context;
    View mConvertView;
    ProgressDialog mProgressDialog;

    public PendingOrderListAdapter(Activity context, List<OrderBean> data) {
        this.data = data;
        this.context = context;
       

    }


    @Override
    public PendingOrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_pending_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PendingOrderListAdapter.ViewHolder viewHolder, int position) {

        viewHolder.shopName.setText(data.get(position).getSupplier().getShopName());
        viewHolder.bookingTime.setText(data.get(position).getBookingDate());
        String[] dateAr = data.get(position).getDeliveryDate().split(" ");
        viewHolder.day.setText(dateAr[0]);
        viewHolder.month.setText(dateAr[1]);
        viewHolder.year.setText(dateAr[2]);
        viewHolder.supplierName.setText(data.get(position).getSupplier().getName());
        viewHolder.supplierMobile.setText(data.get(position).getSupplier().getMobile());
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
        for (int i = 0; i < data.get(position).getWaterTypeQuantity().size(); i++) {
            String[] orderDetail = data.get(position).getWaterTypeQuantity().get(i).split(",");
            addView(orderDetail[0], orderDetail[2], orderDetail[1], viewHolder);
        }
        viewHolder.deliver_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(context)
                        .setTitle("Delivery Confirmation")
                        .setMessage("Are you sure you have delivered this order ?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {

                            if (!Helper.isNetworkAvailable(context)) {
                                Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mProgressDialog=new ProgressDialog(context);
                            mProgressDialog.setMessage("Please wait...");
                            mProgressDialog.show();
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Order");
                            OrderBean orderBean=data.get(position);
                            orderBean.setStatus(true);
                            database.child(data.get(position).getOrderId()).setValue(orderBean, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        mProgressDialog.dismiss();
                                        PendingOrderFragment.orderBeanList.remove(orderBean);
                                        DeliveredOrderFragment.orderBeanList.add(orderBean);
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

    void addView(String name, String qty, String rate, ViewHolder viewHolder) {
        mConvertView = context.getLayoutInflater().inflate(R.layout.dynamic_show_order, null);
        TextView orderName = ((TextView) mConvertView.findViewById(R.id.orderName));
        TextView orderQty = ((TextView) mConvertView.findViewById(R.id.orderQty));
        TextView orderRate = ((TextView) mConvertView.findViewById(R.id.orderRate));
        orderName.setText(name/*+" (\u20B9"+rate+")"*/);
        orderQty.setText(qty);
        orderRate.setText("" + Double.valueOf(qty) * Double.valueOf(rate));
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
