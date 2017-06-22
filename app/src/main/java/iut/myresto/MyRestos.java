package iut.myresto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import iut.myresto.models.Resto;
import iut.myresto.tools.MyAdapter;
import iut.myresto.tools.db.DatabaseHandler;

/**
 * Created by amanda on 01/06/2017.
 */

public class MyRestos extends AppCompatActivity {

    //Components
    private MyAdapter mAdapter;
    private ArrayList<String> myDataset = new ArrayList<>();
    private ArrayList<Resto> initialRestos = new ArrayList<>();
    private DatabaseHandler database;

    // UI elements
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton fb;
    private TextView textResto;

    //VAR
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        //Adding Back button
        getSupportActionBar().setHomeButtonEnabled(true);

        //Getting kept data of the database
        database = new DatabaseHandler(this);
        database.open();
        token = database.getToken();
        database.close();

        getRestos("https://myrestoapp.herokuapp.com/resto/get/");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        fb = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        textResto = (TextView) findViewById(R.id.textResto);

        //Setting configurations
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setPadding(0,35,0,0);
        fb.setVisibility(View.VISIBLE);

        //Initializing adapter
        mAdapter = new MyAdapter(this, initialRestos);
        mRecyclerView.setAdapter(mAdapter);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyRestos.this, NewResto.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home ) {
            Intent i=new Intent(MyRestos.this, MainActivity.class);
            startActivity(i);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    private void getRestos(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Convert the response to a JSONObject
                Gson gson = new Gson();
                Resto[] resto = gson.fromJson(response,Resto[].class);
                if(resto.length == 0){
                    textResto.setVisibility(View.VISIBLE);
                }
                for (int i=0; i<resto.length;i++){
                    initialRestos.add(resto[i]);
                }


                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.headers.containsValue("NETWORK 404")) {
                    message();
                }
                Log.d("Respuesta! D:", error.getMessage());


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("token",token);
                return params;
            }
        };
        queue.add(sr);


    }

    public void message(){
        Toast.makeText(this, "No coincidences", Toast.LENGTH_SHORT).show();
    }
}
