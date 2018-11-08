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
    EditText unit;
    TextView roomrent, electricbill, toolbartname;
    RelativeLayout done;
    String tenantname, housenumber;
    String actualroomrent, actualpriceperunit;
    String due;
    FirebaseUser currentFirebaseUser;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    int tbp;
    ProgressDialog progressDialog;

    String month;

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
                    myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tbp = Integer.parseInt(roomrent.getText().toString().trim()) + Integer.parseInt(unit.getText().toString().trim()) * Integer.parseInt(electricbill.getText().toString().trim());


                            LayoutInflater layoutInflater = LayoutInflater.from(addpaymentActivity.this);
                            View mView = layoutInflater.inflate(R.layout.backalertdialog, null);
                            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(addpaymentActivity.this);
                            alertDialogBuilderUserInput.setView(mView);
                            if (dataSnapshot.hasChild("Paid")) {
                                alertDialogBuilderUserInput.setMessage("Total amount to be paid :" + dataSnapshot.child("Tobepaid").getValue().toString() + "\n" + "Room-Rent :" + dataSnapshot.child("amt").getValue().toString() + "\n" + "Electric Bill : " + (Integer.parseInt(unit.getText().toString()) * Integer.parseInt(dataSnapshot.child("ebill").getValue().toString()) + "\n" + "Paid :" + dataSnapshot.child("Paid").getValue().toString() + "\n" + "Due :" + dataSnapshot.child("Due").getValue().toString()));
                            } else {
                                alertDialogBuilderUserInput.setMessage("Total amount to be paid :" + tbp + "\n" + "Room-Rent :" + dataSnapshot.child("amt").getValue().toString() + "\n" + "Electric Bill : " + (Integer.parseInt(unit.getText().toString()) * Integer.parseInt(dataSnapshot.child("ebill").getValue().toString()) + "\n" + "Paid :" + "0"));
                            }

                            alertDialogBuilderUserInput
                                    .setCancelable(false)
                                    .setPositiveButton("Save Reading", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            //save to db
                                            myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).child("Ereading").setValue(unit.getText().toString().trim());
                                            myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).child("Tobepaid").setValue(tbp);

                                            dialogBox.dismiss();
                                            paymentdialog(tbp);
                                        }
                                    })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogBox, int id) {
                                                    dialogBox.cancel();
                                                }
                                            });

                            AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                            alertDialogAndroid.show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            }
        });

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
                if (dataSnapshot.hasChild("Ereading")) {
                    unit.setText(dataSnapshot.child("Ereading").getValue().toString());
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

    public void paymentdialog(final int tbp) {
        //for validating input
        myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                LayoutInflater layoutInflater = LayoutInflater.from(addpaymentActivity.this);
                View mView = layoutInflater.inflate(R.layout.payment_alert_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(addpaymentActivity.this);
                alertDialogBuilderUserInput.setView(mView);
                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Paid", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if (userInputDialogEditText.getText().toString().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Please fill the amount", Toast.LENGTH_LONG).show();
                                } else if (dataSnapshot.hasChild("Paid")) {
                                    if (Integer.parseInt(dataSnapshot.child("Due").getValue().toString()) < Integer.parseInt(userInputDialogEditText.getText().toString().trim())) {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid Input", Toast.LENGTH_LONG).show();
                                        dialogBox.dismiss();
                                    } else {
                                        myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).child("Paid").setValue(Integer.parseInt(dataSnapshot.child("Paid").getValue().toString()) + Integer.parseInt(userInputDialogEditText.getText().toString().trim()));
                                        int due = tbp-(Integer.parseInt(dataSnapshot.child("Paid").getValue().toString()) + Integer.parseInt(userInputDialogEditText.getText().toString().trim()));
                                        myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).child("Due").setValue(due);
                                        dialogBox.dismiss();
                                    }
                                } else {
                                    if (Integer.parseInt(dataSnapshot.child("Tobepaid").getValue().toString()) < Integer.parseInt(userInputDialogEditText.getText().toString().trim())) {
                                        Toast.makeText(getApplicationContext(), "Please Enter Valid Input..", Toast.LENGTH_LONG).show();
                                        dialogBox.dismiss();
                                    } else {
                                        myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).child("Paid").setValue(userInputDialogEditText.getText().toString().trim());
                                        int due = tbp - Integer.parseInt(userInputDialogEditText.getText().toString().trim());
                                        myRef.child("RoomRent").child(currentFirebaseUser.getUid()).child(housenumber.trim()).child(month.trim()).child("Due").setValue(due);
                                        dialogBox.dismiss();
                                    }
                                    //Toast.makeText(getApplicationContext(), Integer.toString(test), Toast.LENGTH_LONG).show();
                                }

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}


