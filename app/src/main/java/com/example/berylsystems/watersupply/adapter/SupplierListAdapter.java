package com.example.berylsystems.watersupply.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.activities.OrderActivity;
import com.example.berylsystems.watersupply.bean.UserBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SupplierAdaper extends RecyclerView.Adapter<SupplierAdaper.ViewHolder> {

    private List<UserBean> data;
    private Context context;
    AppUser appUser;

    public SupplierAdaper(Context context, List<UserBean> data) {
        this.data = data;
        this.context = context;
        appUser = LocalRepositories.getAppUser(context);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
    }


    @Override
    public SupplierAdaper.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_supplier_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SupplierAdaper.ViewHolder viewHolder, int position) {
        viewHolder.status.setText("Deliver Up to "+data.get(position).getDeliveryTime());
        viewHolder.time.setText(data.get(position).getOpenBooking() + " - " + data.get(position).getCloseBooking());
        viewHolder.name.setText(data.get(position).getName());
        viewHolder.shop_name.setText(data.get(position).getShopName());
        viewHolder.mobile.setText(data.get(position).getMobile());
        viewHolder.address.setText(data.get(position).getAddress());
        viewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUser.supplier = data.get(position);
                LocalRepositories.saveAppUser(context, appUser);
                context.startActivity(new Intent(context, OrderActivity.class));
            }
        });

    }


    @Override
    public int getItemCount() {
        return data.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.main_layout)
        LinearLayout main_layout;
        @Bind(R.id.shop_name)
        TextView shop_name;
        @Bind(R.id.mobile)
        TextView mobile;
        @Bind(R.id.address)
        TextView address;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.status)
        TextView status;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);

        }
    }
}
