package iut.myresto;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import iut.myresto.models.Comment;
import iut.myresto.models.Resto;
import iut.myresto.models.User;
import iut.myresto.tools.GoogleAddress;
import iut.myresto.tools.MyAdapterComment;
import iut.myresto.tools.db.DatabaseHandler;

public class RestoData extends AppCompatActivity implements OnMapReadyCallback {

    //VAR
    Resto r;
    private String addres;
    private String city;
    private String token;
    private String comment;
    private double note;

    // UI elements
    public TextView user;
    public TextView titre;
    public TextView address;
    public TextView type;
    public TextView rate;
    public ImageView photoUser;
    public TableRow photoResto;
    public TableLayout table;
    public EditText message;
    public RatingBar raiting;

    //Components
    private MyAdapterComment mAdapter;
    private ArrayList<Comment> initialComments = new ArrayList<>();
    private DatabaseHandler database;

    // UI elements
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_data);

        //Adding Back button
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String resto = getIntent().getStringExtra("resto");
        Gson gson = new Gson();
        r = gson.fromJson(resto,Resto.class);

        //Getting kept data of the database
        database = new DatabaseHandler(RestoData.this);
        database.open();
        token = database.getToken();
        database.close();

        //Initializing items
        user = (TextView) findViewById(R.id.user);
        titre = (TextView) findViewById(R.id.titre);
        address = (TextView) findViewById(R.id.address);
        type = (TextView) findViewById(R.id.type);
        rate = (TextView) findViewById(R.id.rate);
        photoUser = (ImageView) findViewById(R.id.photoUser);
        photoResto = (TableRow) findViewById(R.id.photoResto);
        table = (TableLayout) findViewById(R.id.row);


        GoogleAddress direction = new GoogleAddress(r.getLat(), r.getLng(), this);
        addres = direction.getAddress();
        city = direction.getCity();
        if( r.getUser() != null){
            user.setText(r.getUser().getNom());
        }else {
            user.setVisibility(View.GONE);
            photoUser.setVisibility(View.GONE);
        }

        titre.setText(r.getNom());
        address.setText(addres+", "+city);
        type.setText(r.getType());
        rate.setText(String.valueOf(r.getNote()));

        fb = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_comment);

        getComments("https://myrestoapp.herokuapp.com/notes/get/"+r.getId());

        //Setting configurations
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Initializing adapter
        mAdapter = new MyAdapterComment(this, initialComments);
        mRecyclerView.setAdapter(mAdapter);


        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddComment();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(r.getLat(), r.getLng());
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title(r.getNom()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo( 17.0f ));
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home ) {
            Intent i=new Intent(RestoData.this, MainActivity.class);
            startActivity(i);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    private void getComments(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Convert the response to a JSONObject
                Log.d("Response", response);
                Gson gson = new Gson();
                Comment[] comment = gson.fromJson(response,Comment[].class);

                for (int i=0; i<comment.length;i++){
                    initialComments.add(comment[i]);
                }


                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.headers.containsValue("NETWORK 404")) {
                    message("Oh, something was wrong...");
            }
                Log.d("Respuesta! D:", error.getMessage());


            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

        };
        queue.add(sr);


    }

    public void message(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void dialogAddComment(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RestoData.this);
        LayoutInflater inflater = RestoData.this.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.activity_comment_dialog, null);

        builder.setView(dialoglayout);

        message = (EditText) dialoglayout.findViewById(R.id.comment);
        raiting = (RatingBar) dialoglayout.findViewById(R.id.ratingBar);


        builder.setPositiveButton("Create",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        comment = message.getText().toString();
                        note = raiting.getRating();
                        createComment();
                    }
                });

        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void createComment() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.POST, "https://myrestoapp.herokuapp.com/notes/create", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Convert the response to a JSONObject
                Log.d("Response", response);
                message("Your comment was correctly sumited");
                initialComments.removeAll(initialComments);
                getComments("https://myrestoapp.herokuapp.com/notes/get/"+r.getId());
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.headers.containsValue("NETWORK 404")) {
                    message("Oh, something was wrong...");
                }
                Log.d("Respuesta! D:", error.getMessage());


            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("note", String.valueOf(note));
                params.put("message", comment);
                params.put("resto", String.valueOf(r.getId()));

                return params;
            }

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
}
