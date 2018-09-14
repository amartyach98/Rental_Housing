package techworldtechnologies.rentalhousing;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class addtenanttwo_activity extends AppCompatActivity {
    String tenantname;
    RelativeLayout backcion;
    EditText hno, rno, roomrent, priceperunit;
    Button addtenant;
    String house_no, room_no, room_rent, price_per_unit;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser currentFirebaseUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtenanttwo);
        backcion = (RelativeLayout) findViewById(R.id.backcion);
        hno = (EditText) findViewById(R.id.hno);
        rno = (EditText) findViewById(R.id.rno);
        roomrent = (EditText) findViewById(R.id.roomrent);
        priceperunit = (EditText) findViewById(R.id.priceperunit);
        addtenant = (Button) findViewById(R.id.addtenant);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = mAuth.getCurrentUser();
        backcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(addtenanttwo_activity.this);
                View mView = layoutInflater.inflate(R.layout.backalertdialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(addtenanttwo_activity.this);
                alertDialogBuilderUserInput.setView(mView);
                alertDialogBuilderUserInput.setMessage("Press Cancel to Discard or Back to jump into previous page!!");
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                Intent intent = new Intent(addtenanttwo_activity.this, addtenant_Activity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                }).setNeutralButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent  intent=new Intent(addtenanttwo_activity.this,main.class);
                                startActivity(intent);
                            }
                        });
                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });
        addtenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                house_no = hno.getText().toString().trim();
                room_no = rno.getText().toString().trim();
                room_rent = roomrent.getText().toString().trim();
                price_per_unit = priceperunit.getText().toString().trim();
                if (house_no.isEmpty() || room_no.isEmpty() || room_rent.isEmpty() || price_per_unit.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill required fields!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    intcheck();
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        tenantname = intent.getStringExtra("tenant_name");

    }

    public void intcheck() {
        try {
            int rr = Integer.parseInt(room_rent);
            int ppu = Integer.parseInt(price_per_unit);
            tenantadd();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Room rent & Price per unit must be NUMERIC!!",
                    Toast.LENGTH_SHORT).show();

        }
    }

    public void tenantadd() {

        HashMap<String, String> dataMap = new HashMap<String, String>();

        dataMap.put("House_no", house_no);
        dataMap.put("Room_no", room_no);
        dataMap.put("Room_rent", room_rent);
        dataMap.put("Price_per_unit", price_per_unit);

        myRef.child("Tenant").child(currentFirebaseUser.getUid()).child(tenantname).setValue(dataMap);
        myRef.child("Room_no").child(room_no).setValue(tenantname);
        Toast.makeText(getApplicationContext(), "Success",
                Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(),main.class);
        startActivity(intent);

    }
}
