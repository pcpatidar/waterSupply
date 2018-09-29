package com.example.berylsystems.watersupply.adapter;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.adapter.customer.OrderListAdapter;
import com.example.berylsystems.watersupply.bean.OrderBean;
import com.example.berylsystems.watersupply.utils.ParameterConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private List<OrderBean> data;
    private Activity context;
    View mConvertView;

    public HistoryListAdapter(Activity context, List<OrderBean> data, Boolean b) {
        this.data = data;
        this.context = context;
    }


    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (ParameterConstants.KEY.contains("C")){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_history_customer, viewGroup, false);
        }else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_history_supplier, viewGroup, false);
        }

        return new HistoryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryListAdapter.ViewHolder viewHolder, int position) {

        viewHolder.shopName.setText(data.get(position).getSupplier().getShopName());
        viewHolder.bookingTime.setText(data.get(position).getBookingDate());
        String[] dateAr = data.get(position).getDeliveryDate().split(" ");
        viewHolder.day.setText(dateAr[0]);
        viewHolder.month.setText(dateAr[1]);
        viewHolder.year.setText(dateAr[2]);
        viewHolder.supplierName.setText(data.get(position).getUser().getName());
        viewHolder.supplierMobile.setText(data.get(position).getSupplier().getMobile());
        viewHolder.userMobile.setText(data.get(position).getUser().getMobile());
        viewHolder.address.setText(data.get(position).getAddress());
        if (data.get(position).isStatus()) {
            viewHolder.status.setText("Delivered");
        } else {
            viewHolder.status.setText("Pending");
        }
        viewHolder.bind(data.get(position).isStatus());

        viewHolder.amount.setText("\u20B9" + data.get(position).getAmount());
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
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    void addView(String name, String wQty, String bQty, String rate, HistoryListAdapter.ViewHolder viewHolder) {
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

    void removeAllViews(HistoryListAdapter.ViewHolder viewHolder) {
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
        @Bind(R.id.userMobile)
        TextView userMobile;
        @Bind(R.id.address)
        TextView address;
        @Bind(R.id.status)
        TextView status;
        @Bind(R.id.comment)
        TextView comment;
        @Bind(R.id.orderId)
        TextView orderId;
        @Bind(R.id.parentLayout)
        LinearLayout parentLayout;
        @Bind(R.id.amount)
        TextView amount;


        private ObjectAnimator anim;

        @SuppressLint("WrongConstant")
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            anim = ObjectAnimator.ofFloat(status, "alpha", 0.2f, 1f);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(800);
        }

        public void bind(Boolean dataObject) {
            anim.start();
        }
    }

}
