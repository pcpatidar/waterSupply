package com.example.berylsystems.watersupply.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.adapter.WaterDetailAdapter;
import com.example.berylsystems.watersupply.bean.OrderBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;
import com.example.berylsystems.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderActivity extends AppCompatActivity {

    @Bind(R.id.address)
    EditText mAddress;
    @Bind(R.id.checkbox_address)
    CheckBox mCheckboxAddress;
    @Bind(R.id.checkbox_delivery)
    CheckBox mCheckboxDelivery;
    @Bind(R.id.today)
    TextView mToday;
    @Bind(R.id.tomorrow)
    TextView mTomorrow;
    @Bind(R.id.date_time)
    TextView mDate_time;
    @Bind(R.id.comment)
    EditText mComment;
    @Bind(R.id.open_calender)
    LinearLayout mOpen_calender;
    @Bind(R.id.coordinatorLayout)
    LinearLayout coordinatorLayout;
    @Bind(R.id.total)
    TextView total;
    @Bind(R.id.empty_bottle_checkbox)
    CheckBox empty_bottle_checkbox;
    @Bind(R.id.empty_bottle_rate)
    TextView empty_bottle_rate;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;
    WaterDetailAdapter mAdapter;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Dialog dialog;

    AppUser appUser;
    int endTime = 15;
    public static OrderActivity context;
    boolean isKeyPadOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        context = this;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Booking...");
        progressDialog.setCancelable(false);
        appUser = LocalRepositories.getAppUser(this);
        Helper.initActionbar(this, getSupportActionBar(), "make order", true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        databaseReference = FirebaseDatabase.getInstance().getReference("Order");

        mAddress.setText(appUser.user.getAddress().trim());
        mAddress.setEnabled(false);
        mCheckboxAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mAddress.setText(appUser.user.getAddress().trim());
                    mAddress.setEnabled(false);
                } else {
                    mAddress.setEnabled(true);
                    mAddress.setText(ParameterConstants.ADDRESS);
                }
            }
        });

        mCheckboxDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCheckboxDelivery.setChecked(true);
                Snackbar.make(coordinatorLayout, "Online payment is under development", Snackbar.LENGTH_SHORT).show();
            }
        });

        empty_bottle_rate.setVisibility(View.GONE);
        empty_bottle_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    showPopup(empty_bottle_rate, total);
                    empty_bottle_rate.setVisibility(View.VISIBLE);
                } else {
                    try {
                        String rate[]=empty_bottle_rate.getText().toString().split("=");
                        total.setText("" + (Double.valueOf(total.getText().toString()) - Double.valueOf(rate[1])));
                        empty_bottle_rate.setVisibility(View.GONE);
                    }catch (Exception e){

                    }
                    empty_bottle_rate.setText("0.0");

                }
            }
        });


        String[] hms = today()[3].split(":");
        if (!checkDate()) {
            tomorrowView();
            String[] dateArr = tomorrow();
            mDate_time.setText("" + dateArr[2] + " " + dateArr[1] + " " + dateArr[dateArr.length - 1] + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());

        } else {
            todayView();
            String[] dateArr = today();
            mDate_time.setText("" + dateArr[2] + " " + dateArr[1] + " " + dateArr[dateArr.length - 1] + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());
        }


        mToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkDate()) {
                    Snackbar.make(coordinatorLayout, "Booking Closed", Toast.LENGTH_SHORT).show();
                    return;
                }
                todayView();
                String[] dateArr = today();
                mDate_time.setText("" + dateArr[2] + " " + dateArr[1] + " " + dateArr[dateArr.length - 1] + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());

            }
        });

        mTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomorrowView();
                String[] dateArr = tomorrow();
                mDate_time.setText("" + dateArr[2] + " " + dateArr[1] + " " + dateArr[dateArr.length - 1] + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());
            }
        });

        dateDialog(mOpen_calender, mDate_time);
        setAdapter();

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                isKeyPadOpen = isOpen;
            }
        });

    }


    void setAdapter() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new WaterDetailAdapter(this, appUser.supplier.getTypeRate());
        mRecyclerView.setAdapter(mAdapter);
    }

    DatePickerDialog datePickerDialog;
    String dateString;

    void dateDialog(View view, TextView date) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(OrderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                        dateString = sdf.format(myCalendar.getTime());
                        date.setText(dateString + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());

                        if (Integer.valueOf(today()[2]) == dayOfMonth) {
                            todayView();
                        } else if (Integer.valueOf(tomorrow()[2]) == dayOfMonth) {
                            tomorrowView();
                        } else {
                            mToday.setTextColor(getResources().getColor(R.color.black));
                            mToday.setBackground(getResources().getDrawable(R.drawable.black_border));
                            mTomorrow.setTextColor(getResources().getColor(R.color.black));
                            mTomorrow.setBackground(getResources().getDrawable(R.drawable.black_border));
                        }
                    }

                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                String[] h = today()[3].split(":");
                Calendar cal = Calendar.getInstance();
                if (Integer.valueOf(h[0]) > endTime) {
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                    cal.add(Calendar.DAY_OF_YEAR, 6);
                    datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                } else {
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    cal.add(Calendar.DAY_OF_YEAR, 7);
                    datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                }
            }

        });

    }

    String[] today() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        return String.valueOf(today).split(" ");
    }

    String[] tomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        return String.valueOf(tomorrow).split(" ");
    }

    void todayView() {
        mToday.setTextColor(getResources().getColor(R.color.colorPrimary));
        mToday.setBackground(getResources().getDrawable(R.drawable.orange_border));
        mTomorrow.setTextColor(getResources().getColor(R.color.black));
        mTomorrow.setBackground(getResources().getDrawable(R.drawable.black_border));
    }

    void tomorrowView() {
        mToday.setTextColor(getResources().getColor(R.color.black));
        mToday.setBackground(getResources().getDrawable(R.drawable.black_border));
        mTomorrow.setTextColor(getResources().getColor(R.color.colorPrimary));
        mTomorrow.setBackground(getResources().getDrawable(R.drawable.orange_border));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        CustomerHomeActivity.bool = true;
        return super.onOptionsItemSelected(item);
    }

    public void submit(View view) {

        OrderBean orderBean = new OrderBean();
        List list = new ArrayList();
        if (WaterDetailAdapter.map.size() == 0) {
            Snackbar.make(coordinatorLayout, "Please Select an Bottle quantity", Snackbar.LENGTH_SHORT).show();
            return;
        }
        Set<Integer> set = WaterDetailAdapter.map.keySet();
        for (Integer key : set) {
            list.add(WaterDetailAdapter.map.get(key));
        }
        progressDialog.show();
        orderBean.setUser(appUser.user);
        orderBean.setSupplier(appUser.supplier);
        orderBean.setAmount("10");
        String text = mDate_time.getText().toString();
        String date = text.substring(0, text.indexOf("B"));
        orderBean.setCashOnDelivery(true);
        orderBean.setBookingDate(today()[2] + " " + today()[1] + " " + today()[today().length - 1] + " " + today()[3]);
        orderBean.setDeliveryDate(date);
        orderBean.setComment(mComment.getText().toString().trim());
        orderBean.setWaterTypeQuantity(list);
        orderBean.setAmount(total.getText().toString());
        orderBean.setAddress(mAddress.getText().toString());
        String key = databaseReference.push().getKey();
        orderBean.setOrderId(key);
        databaseReference.child(key).setValue(orderBean, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                progressDialog.dismiss();
                if (databaseError == null) {
                    CustomerHomeActivity.bool = false;
                    Toast.makeText(OrderActivity.this, "Your order is successfully submitted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(OrderActivity.this, "Sorry, Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public String getTotal() {
        return total.getText().toString();
    }

    public void setTotal(String total_amount) {
        total.setText(total_amount);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CustomerHomeActivity.bool = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    boolean checkDate() {
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        String startDate = sdf.format(date);
        String endDate = appUser.supplier.getCloseBooking();
        String diff = Helper.getTimeDifferent(startDate, endDate);
        if (Double.valueOf(diff) >= Double.valueOf(appUser.supplier.getDeliveryTime().split(" ")[0])) {
            return true;
        } else {
            return false;
        }
    }


    void showPopup(TextView emptyBottle, TextView total) {
        dialog = new Dialog(OrderActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.empty_bottle_rate_dialog);
        dialog.setCancelable(false);
        EditText quantity = (EditText) dialog.findViewById(R.id.quantity);
        TextView rate = (TextView) dialog.findViewById(R.id.rate);
        appUser.supplier.setEmptyBottleRate("150");
        LocalRepositories.saveAppUser(getApplicationContext(), appUser);
        appUser = LocalRepositories.getAppUser(getApplicationContext());
        rate.setText(appUser.supplier.getEmptyBottleRate());
        quantity.setText("1");

        Button submit = (Button) dialog.findViewById(R.id.dialogSubmit);
        LinearLayout close = (LinearLayout) dialog.findViewById(R.id.close);

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!quantity.getText().toString().isEmpty()) {
                    if (Integer.valueOf(quantity.getText().toString()) == 0) {
                        quantity.setText("1");
                    }
                    rate.setText("" + (Integer.valueOf(appUser.supplier.getEmptyBottleRate()) * Integer.valueOf(quantity.getText().toString())));
                } else {
                    rate.setText(appUser.supplier.getEmptyBottleRate());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empty_bottle_checkbox.setChecked(false);
                dialog.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity.getText().toString().isEmpty()) {
                    Toast.makeText(OrderActivity.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                emptyBottle.setText(quantity.getText().toString()+"×"+appUser.supplier.getEmptyBottleRate()+"="+rate.getText().toString());
                total.setText("" + (Double.valueOf(total.getText().toString()) + Double.valueOf(rate.getText().toString())));
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Helper.closeKeyPad(OrderActivity.this, isKeyPadOpen);
            }
        });
        dialog.show();
    }
}
