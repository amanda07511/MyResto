package iut.myresto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;

import iut.myresto.models.Resto;
import iut.myresto.models.User;
import iut.myresto.tools.MyAdapter;
import iut.myresto.tools.db.DatabaseHandler;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Components
    private MyAdapter mAdapter;
    private ArrayList<String> myDataset = new ArrayList<>();
    private ArrayList<Resto> initialRestos = new ArrayList<>();
    private DatabaseHandler database;
    private User mUser;
    // UI elements
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton fb;
    private TextView mName;
    private TextView mEmail;
    //Variables
    Boolean showButton = true;
    String token;
    String url = "https://myrestoapp.herokuapp.com/resto/";
    String restoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        getRestos(url);
        //Initializing UI
        fb = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mName = (TextView) header.findViewById(R.id.bar_name);
        mEmail = (TextView) header.findViewById(R.id.bar_email);

        //Setting configurations
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        fb.setVisibility(View.INVISIBLE);

        //Initializing adapter
        mAdapter = new MyAdapter(this, initialRestos);
        mRecyclerView.setAdapter(mAdapter);

        //Getting kept data of the database
        database = new DatabaseHandler(this);
        database.open();
        token = database.getToken();
        mUser = database.getUser();
        database.close();

        //Setting configurations MainView
        if (token == null) {
            Log.d("Token", "Estoy vacio :( ");
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();
            showButton = true;
        } else {
            Log.d("Token", token);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
            mName.setText(mUser.getNom() + " " + mUser.getPrenom());
            mEmail.setText(mUser.getEmail());
            showButton = false;
        }

        database.close();


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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_signup).setVisible(showButton);


        //Get menu item, input text in the search view
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        //Get search view
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Get the changes of the search view
        searchView.setQueryHint(getResources().getString(R.string.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Text recive
                restoName=query;
                //Call data by the method getDataFromUrl
                getResto(url+"get/"+restoName);
                //Empty the input
                searchView.setQuery("", false);
                searchView.setIconified(true);
                //Clear the cities list for preparete new ones
                initialRestos.removeAll(initialRestos);
                //Initializing adapter
                mAdapter = new MyAdapter(MainActivity.this, initialRestos);
                mRecyclerView.setAdapter(mAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Get new text if it changes
                restoName=newText;
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_signup) {
            Intent i = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.putExtra("Type", "Edit");
            startActivity(i);
            finish();
        } else if (id == R.id.nav_categories) {
            Intent i = new Intent(MainActivity.this, Categories.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_restaurants) {
            Intent i = new Intent(MainActivity.this, MyRestos.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_comment) {
            Intent i = new Intent(MainActivity.this, MyComments.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_logout) {
            database.open();
            database.removeObj(1);
            database.close();
            recreate();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(sr);


    }

    private void getResto(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response",response);
                //Convert the response to a JSONObject
                if(response == "Resto not Found"){
                    message();
                }
                else{
                    Gson gson = new Gson();
                    Resto resto = gson.fromJson(response,Resto.class);
                    initialRestos.add(resto);
                    mAdapter.notifyDataSetChanged();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Respuesta! D:", String.valueOf(error.networkResponse.headers));
                if (error.networkResponse.headers.containsValue("NETWORK 404")||error.networkResponse.headers.containsValue("NETWORK 505")) {
                    message();
                    getRestos("https://myrestoapp.herokuapp.com/resto/");
                }



            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(sr);


    }


    public void message(){
        Toast.makeText(this, "No coincidences", Toast.LENGTH_SHORT).show();
    }

}
