package com.moyo.carzrideon.Activitites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;;import com.moyo.carzrideon.R;

public class Confirmed extends AppCompatActivity implements View.OnClickListener{
    private Button home;
    private String flag;
    private ImageView confirmedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle extras = getIntent().getExtras();
        flag = extras.getString("flag");

        home = (Button) findViewById(R.id.home);
        confirmedImage = (ImageView) findViewById(R.id.confirmedImage);

       /* if (flag.equalsIgnoreCase("taken")) {
            Log.d("taken","taken");
            confirmedImage.setImageResource(R.drawable.rideonlogo);
        } else if (flag.equalsIgnoreCase("offered")) {
            Log.d("offered","offered");
            confirmedImage.setImageResource(R.drawable.rideonlogo2);
        }*/
        home.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.home) {
            Intent intent = new Intent(Confirmed.this,SearchAndPlace.class);
            startActivity(intent);
            finish();
        }
    }
}
