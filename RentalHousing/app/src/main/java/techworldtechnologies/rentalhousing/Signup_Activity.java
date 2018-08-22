package techworldtechnologies.rentalhousing;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup_Activity extends AppCompatActivity {
    EditText regfullname, regemail, regpassword;
    Button login;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    String email;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        regfullname = (EditText) findViewById(R.id.reg_fullname);
        regemail = (EditText) findViewById(R.id.reg_email);
        regpassword = (EditText) findViewById(R.id.reg_password);
        login = (Button) findViewById(R.id.signin);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Signup_Activity.this, R.style.Custom);
        progressDialog.setMessage("Registering..");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                email = regemail.getText().toString().trim();
                if (email.isEmpty() || regpassword.getText().toString().trim().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please fill required fields!!",
                            Toast.LENGTH_SHORT).show();
                } else if (regpassword.getText().toString().trim().length() < 5) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Password must be 6 character!!",
                            Toast.LENGTH_SHORT).show();
                } else if (email.matches(emailPattern)) {
                    mAuth.createUserWithEmailAndPassword(email, regpassword.getText().toString().trim())
                            .addOnCompleteListener(Signup_Activity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        HashMap<String, String> dataMap = new HashMap<String, String>();

                                        dataMap.put("Full_name", regfullname.getText().toString().trim());
                                        dataMap.put("Email", email);

                                        myRef.child("Users").child(user.getUid()).setValue(dataMap);

                                        Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Already Registered with this email", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please input a valid email", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}
