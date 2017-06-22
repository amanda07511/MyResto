package iut.myresto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AuthActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSingup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //Adding Back button
        getSupportActionBar().setHomeButtonEnabled(true);

        btnLogin = (Button) findViewById(R.id.login);
        btnSingup = (Button) findViewById(R.id.signup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AuthActivity.this, LoginActivity.class);
                i.putExtra("Type","Login");
                startActivity(i);
            }
        });

        btnSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AuthActivity.this, LoginActivity.class);
                i.putExtra("Type","Signup");
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home ) {
            Intent i=new Intent(AuthActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
