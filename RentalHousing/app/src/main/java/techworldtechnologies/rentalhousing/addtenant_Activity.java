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


public class addtenant_Activity extends AppCompatActivity {
RelativeLayout backcion;
Button next;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtennant);
        next=(Button)findViewById(R.id.next);
        backcion=(RelativeLayout)findViewById(R.id.backcion);
        backcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(addtenant_Activity.this);
                View mView = layoutInflater.inflate(R.layout.backalertdialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(addtenant_Activity.this);
                alertDialogBuilderUserInput.setView(mView);
                alertDialogBuilderUserInput.setMessage("Press Cancel to Discard or Back to jump into previous page!!");
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                Intent intent=new Intent(addtenant_Activity.this,main.class);
                                startActivity(intent);

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
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(addtenant_Activity.this,addtenanttwo_activity.class);
                startActivity(intent);
            }
        });


    }


}
