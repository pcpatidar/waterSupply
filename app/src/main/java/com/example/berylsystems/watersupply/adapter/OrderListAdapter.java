package com.example.berylsystems.watersupply.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.bean.OrderBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.LocalRepositories;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private List<OrderBean> data;
    private Activity context;

    View mConvertView;


    public OrderListAdapter(Activity context, List<OrderBean> data) {
        this.data = data;
        this.context = context;
    }


    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_order_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.ViewHolder viewHolder, int position) {

        viewHolder.shopName.setText(data.get(position).getSupplier().getShopName());
        viewHolder.bookingTime.setText(data.get(position).getBookingDate());
        String[] dateAr = data.get(position).getDeliveryDate().split(" ");
        viewHolder.day.setText(dateAr[0]);
        viewHolder.month.setText(dateAr[1]);
        viewHolder.year.setText(dateAr[2]);
        viewHolder.supplierName.setText(data.get(position).getSupplier().getName());
        viewHolder.supplierMobile.setText(data.get(position).getSupplier().getMobile());
        viewHolder.address.setText(data.get(position).getAddress());
        if (data.get(position).isStatus()) {
            viewHolder.status.setText("Delivered");
        } else {
            viewHolder.status.setText("Pending");
        }
       viewHolder.bind();

        viewHolder.amount.setText("\u20B9" + data.get(position).getAmount());
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

        Animation blink;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            blink = AnimationUtils.loadAnimation(context,
                    R.anim.blink);
        }
        public void bind(){
            status.startAnimation(blink);
        }
    }
}
