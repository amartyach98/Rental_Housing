package techworldtechnologies.rentalhousing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Iterator;

import techworldtechnologies.rentalhousing.Adapter.CustomAdapter;
import techworldtechnologies.rentalhousing.Pojo.CustomPojo;

public class main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txt_cuser_email, txt_cuser_name;
    FirebaseUser currentFirebaseUser;
    private FirebaseAuth mAuth;
    public int valcount;
    RelativeLayout profilepic;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    int i = 0;
    String[] sampleString;
    ProgressDialog progressDialog;
    private ArrayList<CustomPojo> listContentArr = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();


    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        progressDialog = new ProgressDialog(main.this, R.style.Custom);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //txt_cuser_email = (TextView) findViewById(R.id.txt_cuser_email);
        // txt_cuser_name = (TextView) findViewById(R.id.txt_cuser_name);
        mAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = mAuth.getCurrentUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        profilepic = (RelativeLayout) findViewById(R.id.profilepic);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                Toast.makeText(main.this, "Single Click on position :" + position,
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(main.this, "Long press on position :" + position,
                        Toast.LENGTH_LONG).show();
            }
        }));

        adapter = new CustomAdapter(main.this);

        populateRecyclerViewValues();


    }


    public void populateRecyclerViewValues() {
        myRef.child("Tenant").child(currentFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // txt_cuser_email.setText(dataSnapshot.child("Email").getValue().toString());
                //txt_cuser_name.setText(dataSnapshot.child("Full_name").getValue().toString());
                valcount = (int) dataSnapshot.getChildrenCount();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                sampleString = new String[valcount];
                while (i < valcount) {
                    sampleString[i] = iterator.next().getKey().toString();
                    Log.d(Integer.toString(i), sampleString[i]);
                    String hno = dataSnapshot.child(sampleString[i]).child("House_no").getValue().toString();
                    Log.d("HNO:::::::", hno);
                    CustomPojo pojoObject = new CustomPojo();

                    pojoObject.setName(sampleString[i]);
                    pojoObject.setContent("House Number: " + hno);


                    listContentArr.add(pojoObject);
                    adapter.setListContent(listContentArr);

                    recyclerView.setAdapter(adapter);
                    i++;
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_addtenant) {
            Intent intent = new Intent(main.this, addtenant_Activity.class);
            startActivity(intent);
        } else if (id == R.id.nav_edittenant) {

        } else if (id == R.id.nav_profit) {

        } else if (id == R.id.nav_logout) {
            if (mAuth.getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(main.this, Login_Activity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_contact) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}

