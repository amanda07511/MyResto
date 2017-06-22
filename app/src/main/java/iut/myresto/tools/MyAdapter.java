package iut.myresto.tools;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import iut.myresto.R;
import iut.myresto.RestoData;
import iut.myresto.models.Resto;


/**
 * Created by amanda on 30/05/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    //COMPONENTS
    private Context mCtx;
    private ArrayList<Resto> mDataset;
    //VAR
    private String address;
    private String city;
    private ArrayList<Integer> id;


    public class ViewHolder  extends RecyclerView.ViewHolder{
        // UI elements
        public TextView user;
        public TextView titre;
        public TextView address;
        public TextView type;
        public TextView rate;
        public ImageView photoUser;
        public TableRow photoResto;
        public TableLayout table;

        public ViewHolder(View v) {
            super(v);

            //Initializing items
            user = (TextView) v.findViewById(R.id.user);
            titre = (TextView) v.findViewById(R.id.titre);
            address = (TextView) v.findViewById(R.id.address);
            type = (TextView) v.findViewById(R.id.type);
            rate = (TextView) v.findViewById(R.id.rate);
            photoUser = (ImageView) v.findViewById(R.id.photoUser);
            photoResto = (TableRow) v.findViewById(R.id.photoResto);
            table = (TableLayout) v.findViewById(R.id.row);

            //Adding a action when user press in a row
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new Gson();
                    String json = gson.toJson(mDataset.get(getAdapterPosition()));
                    //Start Dtails Activity
                    Intent intent = new Intent(mCtx, RestoData.class);
                    intent.putExtra("resto", json );
                    //intent.putExtra("id",id.get(getAdapterPosition()));
                    mCtx.startActivity(intent);

                }
            });

        }
    }

    //Constructor
    public MyAdapter(Context ctx, ArrayList<Resto> restos) {
        mCtx = ctx;
        mDataset = restos;
    }
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        Resto r = mDataset.get(position);
        GoogleAddress direction = new GoogleAddress(r.getLat(), r.getLng(), mCtx);
        address = direction.getAddress();
        city = direction.getCity();
        //id.add(r.getId());
        if(r.getUser() != null){
            holder.user.setText(r.getUser().getNom());
        }

        holder.titre.setText(r.getNom());
        holder.address.setText(address+", "+city);
        holder.type.setText(r.getType());
        holder.rate.setText(String.valueOf(r.getNote()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }




}
