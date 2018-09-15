package com.example.berylsystems.watersupply.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.bean.UserBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;
import com.example.berylsystems.watersupply.utils.ParameterConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUp3Activity extends AppCompatActivity {
    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.submit)
    Button submit;
    @Bind(R.id.deliveryDistance)
    EditText deliveryDistance;
    @Bind(R.id.add_water)
    LinearLayout mAdd_water;
    @Bind(R.id.add_view)
    TextView mAdd_view;
    @Bind(R.id.remove_view)
    TextView mRemove_view;
    @Bind(R.id.openBooking)
    TextView openBooking;
    @Bind(R.id.openBookingIcon)
    ImageView openBookingIcon;
    @Bind(R.id.closeBooking)
    TextView closeBooking;
    @Bind(R.id.closeBookingIcon)
    ImageView closeBookingIcon;
    @Bind(R.id.deliveryTime)
    Spinner deliveryTime;
    @Bind(R.id.sunday)
    CheckBox sunday;
    @Bind(R.id.monday)
    CheckBox monday;
    @Bind(R.id.tuesday)
    CheckBox tuesday;
    @Bind(R.id.wednesday)
    CheckBox wednesday;
    @Bind(R.id.thursday)
    CheckBox thursday;
    @Bind(R.id.friday)
    CheckBox friday;
    @Bind(R.id.saturday)
    CheckBox saturday;

    @Bind(R.id.time_picker)
    TimePicker mTimePicker;

    List<String> typeRateList;
    View mConvertView;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    UserBean userBean;
    DatabaseReference databaseReference;
    Dialog dialog;
    AppUser appUser;
    List<String> spinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);
        ButterKnife.bind(this);
        spinnerList=new ArrayList<>();
        spinnerList.add("Delivery with in");
        Helper.initActionbar(this, getSupportActionBar(), "Sign Up", true);

        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,spinnerList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        deliveryTime.setAdapter(aa);

        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
            }

        });

        mAuth = FirebaseAuth.getInstance();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        databaseReference = FirebaseDatabase.getInstance().getReference(ParameterConstants.KEY);
        appUser = LocalRepositories.getAppUser(this);
        dialog = new Dialog(this);
        dialog();
        typeRateList = new ArrayList<>();
        userBean = SignUpActivity.userBean;
        progressDialog = new ProgressDialog(this);
        addView("Normal Water");
        openBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseBooking(openBooking);
            }
        });
        openBookingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseBooking(openBooking);
            }
        });
        closeBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseBooking(closeBooking);
            }
        });
        closeBookingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseBooking(closeBooking);
            }
        });

        mAdd_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addView("");
            }
        });
        mRemove_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView();
            }
        });
        checkbox(sunday);
        checkbox(monday);
        checkbox(tuesday);
        checkbox(wednesday);
        checkbox(thursday);
        checkbox(friday);
        checkbox(saturday);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (openBooking.getText().toString().contains("Open")) {
                    Snackbar.make(mainLayout, "Select Open Booking Time", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (closeBooking.getText().toString().contains("Close")) {
                    Snackbar.make(mainLayout, "Select Close Booking Time", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (deliveryDistance.getText().toString().isEmpty()) {
                    Snackbar.make(mainLayout, "Enter Delivery Service Distance", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (deliveryTime.getSelectedItemPosition() == 0) {
                    Snackbar.make(mainLayout, "Select Delivery Time", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (ParameterConstants.location == null) {
                    Snackbar.make(mainLayout, "Location not found", Snackbar.LENGTH_LONG).show();
                    return;
                }

                List list = new ArrayList();
                for (int i = 0; i < ((ViewGroup) mAdd_water).getChildCount(); i++) {
                    View v = ((ViewGroup) mAdd_water).getChildAt(i);
                    EditText waterType = v.findViewById(R.id.water_type);
                    EditText waterRate = v.findViewById(R.id.water_rate);
                    if (waterType.getText().toString().isEmpty()) {
                        Snackbar.make(mainLayout, "Enter Water Type", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (waterRate.getText().toString().isEmpty()) {
                        Snackbar.make(mainLayout, "Enter Water Rate", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    list.add(waterType.getText().toString() + "," + waterRate.getText().toString());
                }
                userBean.setLatitude("" + ParameterConstants.location.getLatitude());
                userBean.setLongitude("" + ParameterConstants.location.getLongitude());
                userBean.setOpenBooking(openBooking.getText().toString());
                userBean.setCloseBooking(closeBooking.getText().toString());
                userBean.setDeliveryDistance(deliveryDistance.getText().toString());
                userBean.setDeliveryTime(deliveryTime.getSelectedItem().toString());
                userBean.setTypeRate(list);
                retrieveKey(userBean.getMobile());
            }
        });
    }

    private void retrieveKey(String mobile) {
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        databaseReference.child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserBean bean = dataSnapshot.getValue(UserBean.class);
                if (bean == null) {
                    registerUser(userBean);
                } else {
                    progressDialog.dismiss();
                    Snackbar.make(mainLayout, "User with this Mobile number already registered, Please login", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void openCloseBooking(TextView textView) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String am_pm = "";
                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";
                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                        textView.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);
                        try {
                            List<String> list = Helper.deliverTimeList(Double.parseDouble(Helper.getTimeDifferent(openBooking.getText().toString(),closeBooking.getText().toString())));
                            list.add(0,"Delivery with in");
                            ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,list);
                            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            deliveryTime.setAdapter(aa);
                        }catch (Exception e){
                        }

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    void addView(String text) {
        mConvertView = getLayoutInflater().inflate(R.layout.dynamic_add_water, null);
        EditText editText = ((EditText) mConvertView.findViewById(R.id.water_type));
        editText.setText(text);
        editText.setHint("Enter Water Type");
        ((ViewGroup) mAdd_water).addView(mConvertView);
    }

    void removeView() {
        if (((ViewGroup) mAdd_water).getChildCount() == 1) {
            Toast.makeText(this, "can't remove", Toast.LENGTH_SHORT).show();
            return;
        }
        mConvertView = getLayoutInflater().inflate(R.layout.dynamic_add_water, null);
        EditText editText = ((EditText) mConvertView.findViewById(R.id.water_type));
        editText.setText("");
        editText.setHint("Enter Water Type");
        ((ViewGroup) mAdd_water).removeViewAt(((ViewGroup) mAdd_water).getChildCount() - 1);
    }

    void dialog() {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(true);
        EditText otp = dialog.findViewById(R.id.otp);
        Button submit = (Button) dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp.getText().toString().length() > 5) {
                    progressDialog.setMessage("Verifying OTP...");
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(getApplicationContext(), "invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // set the custom dialog components - text, image and button
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
//                          Auto Verified here
                            Toast.makeText(getApplicationContext(), "Verification done", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            userBean.setUserType(ParameterConstants.KEY);
                            databaseReference.child(userBean.getMobile()).setValue(userBean, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        progressDialog.dismiss();
                                        registerUser(userBean);
                                        appUser.user = userBean;
                                        LocalRepositories.saveAppUser(getApplicationContext(), appUser);
                                        Intent intent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "invalid opt", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void registerUser(UserBean userBean) {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(getApplicationContext(), "verification done", Toast.LENGTH_LONG).show();
                userBean.setUserType(ParameterConstants.KEY);
                databaseReference.child(userBean.getMobile()).setValue(userBean, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            progressDialog.dismiss();
                            appUser.user = userBean;
                            LocalRepositories.saveAppUser(getApplicationContext(), appUser);
                            Intent intent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                System.out.println("aaaaaaaaaaaaa  " + e);
                System.out.println("aaaaaaaaaaaaa  " + e.getMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(), "invalid mob no", Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(getApplicationContext(), "quota over", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Verification code sent", Toast.LENGTH_LONG).show();
                mVerificationId = verificationId;
                mResendToken = token;
                dialog.show();
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + userBean.getMobile(),             // Phone number to verify
                60,                      // Timeout duration
                TimeUnit.SECONDS,        // Unit of timeout
                SignUp3Activity.this,   // Activity (for callback binding)
                mCallbacks);         // OnVerificationStateChangedCallbacks
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    void checkbox(CheckBox checkBox){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(SignUp3Activity.this, ""+checkBox.getText().toString(), Toast.LENGTH_SHORT).show();
                if (checkBox.getText().toString().equals("Sunday")){
                    userBean.setSaturday(b);
                }
                if (checkBox.getText().toString().equals("Monday")){
                    userBean.setMonday(b);
                }
                if (checkBox.getText().toString().equals("Tuesday")){
                    userBean.setTuesday(b);
                }
                if (checkBox.getText().toString().equals("Wednesday")){
                    userBean.setWednesday(b);
                }
                if (checkBox.getText().toString().equals("Thursday")){
                    userBean.setThursday(b);
                }
                if (checkBox.getText().toString().equals("Friday")){
                    userBean.setFriday(b);
                }
                if (checkBox.getText().toString().equals("Saturday")){
                    userBean.setSaturday(b);
                }


            }
        });
    }
}
