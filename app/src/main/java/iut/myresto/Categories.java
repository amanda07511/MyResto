package iut.myresto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;

import iut.myresto.tools.MyAdapterCategories;

public class Categories extends AppCompatActivity {
    //Components
    private MyAdapterCategories mAdapter;
    // UI elements
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<String> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        //Adding Back button
        getSupportActionBar().setHomeButtonEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view2);
        mDataset = new ArrayList<String>(Arrays.asList("American","Asian","Bar & Grill", "Bakeries, Donuts, Snacks & Coffee","Chicken"
                ,"FastFood","Pizza, Pasta & Italian", "Steak, Seafood & Fish", "Mexican", "Other ethnic (European, etc.)"));

        //Setting configurations
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Initializing adapter
        mAdapter = new MyAdapterCategories(this,mDataset);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home ) {
            Intent i=new Intent(Categories.this, MainActivity.class);
            startActivity(i);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}