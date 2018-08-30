package techworldtechnologies.rentalhousing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login_Activity extends AppCompatActivity {
    EditText txt_email, pass;
    Button signin, googlelogin;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog progressDialog;
    FirebaseUser currentFirebaseUser;
    private FirebaseAuth mAuth;
    RelativeLayout rl_showpassimg;
    ImageView showimgicon;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String email, password;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    TextView cna,fp;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onStart() {
        super.onStart();
        currentFirebaseUser = mAuth.getCurrentUser();
        if (currentFirebaseUser != null) {
            Intent intent = new Intent(Login_Activity.this, main.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cna=(TextView) findViewById(R.id.cna);
        fp=(TextView) findViewById(R.id.fp);
        txt_email = (EditText) findViewById(R.id.txt_email);
        googlelogin = (Button) findViewById(R.id.googlelogin);
        pass = (EditText) findViewById(R.id.pass);
        rl_showpassimg=(RelativeLayout)findViewById(R.id.rl_showpassimg);
        showimgicon=(ImageView)findViewById(R.id.showimgicon);
        progressDialog = new ProgressDialog(Login_Activity.this, R.style.Custom);

        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signin = (Button) findViewById(R.id.signin);
        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_Activity.this, forgetpass_Activity.class);
                startActivity(intent);
            }
        });
        cna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_Activity.this, Signup_Activity.class);
                startActivity(intent);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loging In..");
                progressDialog.show();
                email = txt_email.getText().toString().trim();
                password = pass.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please fill required fields!!",
                            Toast.LENGTH_SHORT).show();
                } else if (password.length() < 5) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Password must be of 6 characters!!",
                            Toast.LENGTH_SHORT).show();
                } else if (email.matches(emailPattern)) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent intent = new Intent(Login_Activity.this, main.class);
                                        startActivity(intent);
                                        //  Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please input a valid email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //show password
        rl_showpassimg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    showimgicon.setBackgroundResource(R.drawable.visible);
                    pass.setTransformationMethod(null);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    showimgicon.setBackgroundResource(R.drawable.invisible);

                    pass.setTransformationMethod(new PasswordTransformationMethod());
                }
                return true;
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("AccountID", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(Login_Activity.this, main.class);
                            startActivity(intent);
                        } else {

                            Log.w("failure", "signInWithCredential:failure", task.getException());

                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
