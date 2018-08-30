package techworldtechnologies.rentalhousing;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;

public class forgetpass_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button rsp;
    EditText txt_email;
    String email;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(forgetpass_Activity.this, R.style.Custom);
        progressDialog.setMessage("Processing..");
        txt_email=(EditText)findViewById(R.id.txt_email);
        email = txt_email.getText().toString().trim();
        rsp=(Button)findViewById(R.id.rsp);
        rsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please fill required fields!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(txt_email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "We've sent you an email to Reset your Password!!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(forgetpass_Activity.this, Login_Activity.class);
                                startActivity(intent);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Your Email is not Registered with us!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
