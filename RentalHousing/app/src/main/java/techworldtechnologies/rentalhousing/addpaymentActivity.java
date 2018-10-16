package techworldtechnologies.rentalhousing;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class addpaymentActivity extends AppCompatActivity {
    EditText unit, amtp;
    TextView roomrent, electricbill, toolbartname;
    RelativeLayout done;
    String tenantname, housenumber;
    String actualroomrent, actualpriceperunit;
    FirebaseUser currentFirebaseUser;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog progressDialog;

    String month, amtpaid;
    int totalamttobepaid;
    int price_per_unit;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        toolbartname = (TextView) findViewById(R.id.toolbartname);
        tenantname = intent.getStringExtra("tenant_name");
        housenumber = intent.getStringExtra("house_no");
        toolbartname.setText(tenantname);
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        //Toast.makeText(getApplicationContext(), housenumber, Toast.LENGTH_LONG).show();


        getMonthForInt(month);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpayment);
        unit = (EditText) findViewById(R.id.unit);
        roomrent = (TextView) findViewById(R.id.roomrent_amt);
        //toolbartname.setText(tenantname);
        progressDialog = new ProgressDialog(addpaymentActivity.this, R.style.Custom);
        progressDialog.setMessage("Fetching Data...");
        amtp = (EditText) findViewById(R.id.amtp);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = mAuth.getCurrentUser();
        electricbill = (TextView) findViewById(R.id.electricbill);
        done = (RelativeLayout) findViewById(R.id.doneimg);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (unit.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Price Per Unit cannot be blank!!", Toast.LENGTH_LONG).show();
                } else {
                    intcheck();
                }

            }
        });

    }

    //on click tick
    public void alertdilog() {
        LayoutInflater layoutInflater = LayoutInflater.from(addpaymentActivity.this);
        View mView = layoutInflater.inflate(R.layout.backalertdialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(addpaymentActivity.this);
        alertDialogBuilderUserInput.setView(mView);
        totalamttobepaid = (price_per_unit * Integer.parseInt(actualpriceperunit)) + Integer.parseInt(actualroomrent);
        alertDialogBuilderUserInput.setMessage("Total amount to be paid : " + totalamttobepaid + "\n" + "Electric Bill : " + (price_per_unit * Integer.parseInt(actualpriceperunit) + "\n" + "Room Rent : " + Integer.parseInt(actualroomrent)));
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setNeutralButton("Save Reading", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).child("ereading").setValue(price_per_unit);
                        myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).child("To_Be_Paid").setValue(totalamttobepaid);
                        LayoutInflater layoutInflater = LayoutInflater.from(addpaymentActivity.this);
                        View mView = layoutInflater.inflate(R.layout.payment_alert_dialog, null);
                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(addpaymentActivity.this);
                        alertDialogBuilderUserInput.setView(mView);
                        alertDialogBuilderUserInput.setMessage("");
                        alertDialogBuilderUserInput
                                .setCancelable(false)
                                .setPositiveButton("Paid", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {

                                    }
                                })
                                .setNegativeButton("Later",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.cancel();
                                            }
                                        });

                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                        alertDialogAndroid.show();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public void getMonthForInt(int num) {
        month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        //checking is reading is present or not
        myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("ereading")) {
                    unit.setText(dataSnapshot.child("ereading").getValue().toString());
                    unit.setFocusable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fetchdata();

    }

    //Fetching data from firebase
    public void fetchdata() {
        progressDialog.show();
        myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                actualroomrent = dataSnapshot.child("amt").getValue().toString();
                actualpriceperunit = dataSnapshot.child("ebill").getValue().toString();
                roomrent.setText(actualroomrent);
                electricbill.setText(actualpriceperunit);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void intcheck() {
        try {
            price_per_unit = Integer.parseInt(unit.getText().toString().trim());
            alertdilog();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Price per unit must be NUMERIC!!", Toast.LENGTH_SHORT).show();


        }
    }

    public void intchandmakepayment() {
        try {

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Price per unit must be NUMERIC!!", Toast.LENGTH_SHORT).show();


        }
    }
}


