package iut.myresto;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import iut.myresto.models.Resto;
import iut.myresto.tools.MyAdapter;
import iut.myresto.tools.db.DatabaseHandler;

/**
 * Created by amanda on 22/06/2017.
 */

public class RestoCategorie  extends AppCompatActivity {

    //Components
    private MyAdapter mAdapter;
    private ArrayList<String> myDataset = new ArrayList<>();
    private ArrayList<Resto> initialRestos = new ArrayList<>();

    // UI elements
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton fb;
    private TextView textResto;

    //VAR
    private String categorie;
    private String name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        //Adding Back button
        getSupportActionBar().setHomeButtonEnabled(true);

        //Gettin Intent Information
        Bundle extras = getIntent().getExtras();
        categorie = extras.getString("Type");

        try {
            name = java.net.URLEncoder.encode(categorie, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getRestos("https://myrestoapp.herokuapp.com/resto/getType/"+name);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        textResto = (TextView) findViewById(R.id.textResto);

        //Setting configurations
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setPadding(0,35,0,0);

    }

    private void getRestos(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response asdf",response);
                //Convert the response to a JSONObject
                Gson gson = new Gson();
                Resto[] resto = gson.fromJson(response,Resto[].class);

                for (int i=0; i<resto.length;i++){
                    initialRestos.add(resto[i]);
                }


                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", String.valueOf(error.networkResponse.headers));
                if (error.networkResponse.headers.containsValue("NETWORK 404")) {
                    textResto.setText("Theres any resto in this categorie, you coul add a new one!");
                    textResto.setVisibility(View.VISIBLE);
                }
                Log.d("Respuesta! D:", error.getMessage());


            }
        });
        queue.add(sr);


    }

    public void message(){
        Toast.makeText(this, "No coincidences", Toast.LENGTH_SHORT).show();
    }




}
