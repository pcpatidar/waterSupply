package com.example.berylsystems.watersupply.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.activities.OrderActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EmptyBottleAdapter extends RecyclerView.Adapter<EmptyBottleAdapter.ViewHolder> {

    private List<String> data;
    private Context context;
    int mInteger = 0, totalAmount;
    OrderActivity object;
    public static Map<Integer,String> map;

    public EmptyBottleAdapter(Context context, List<String> data) {
        this.data = data;
        this.context = context;
        object = OrderActivity.context;
        map = new HashMap<>();
    }


    @Override
    public EmptyBottleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_empty_bottle_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmptyBottleAdapter.ViewHolder viewHolder, int position) {
        mInteger = 0;
        String[] arr = data.get(position).split(",");
        viewHolder.water_type.setText(arr[0]);
        viewHolder.water_rate.setText("(\u20B9" + arr[2] + ")");
        // viewHolder.water_type.setText(data.get(position).split(",")[0]+" ( ₹ "+data.get(position).split(",")[1]+")");

        viewHolder.layout_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInteger = Integer.parseInt(viewHolder.mQuantity.getText().toString());
                mInteger = mInteger + 1;
                viewHolder.mQuantity.setText("" + mInteger);
                object.setTotal(""+(Double.valueOf(object.getTotal()) + Double.valueOf(arr[2])));
                map.put(position,arr[0]+","+arr[2]+","+ mInteger);
            }
        });

        viewHolder.layout_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInteger = Integer.parseInt(viewHolder.mQuantity.getText().toString());
                if (mInteger > 0) {
                    mInteger = mInteger - 1;
                    viewHolder.mQuantity.setText("" + mInteger);
                    object.setTotal(""+(Double.valueOf(object.getTotal()) - Double.valueOf(arr[2])));
                    map.put(position,arr[0]+","+arr[2]+"'"+ mInteger);
                    if (viewHolder.mQuantity.getText().toString().equals("0")){
                        map.remove(position);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.water_type)
        TextView water_type;
        @Bind(R.id.water_rate)
        TextView water_rate;
        @Bind(R.id.layout_minus)
        TextView layout_minus;
        @Bind(R.id.layout_plus)
        TextView layout_plus;
        @Bind(R.id.text_quantity)
        TextView mQuantity;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }
}
