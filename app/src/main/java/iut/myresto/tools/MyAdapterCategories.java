package iut.myresto.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import iut.myresto.R;
import iut.myresto.RestoCategorie;
import iut.myresto.RestoData;
import iut.myresto.models.Resto;

/**
 * Created by amanda on 01/06/2017.
 */

public class MyAdapterCategories extends RecyclerView.Adapter<MyAdapterCategories.ViewHolder> {

    private Context mCtx;
    private ArrayList<String> mDataset;
    private ArrayList<Integer> icons;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements
        public TextView categorie;

        public ViewHolder(View v) {
            super(v);

            categorie = (TextView) v.findViewById(R.id.categorie);

            //Adding a action when user press in a row
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mCtx, RestoCategorie.class);
                    intent.putExtra("Type", mDataset.get(getAdapterPosition()) );
                    //intent.putExtra("id",id.get(getAdapterPosition()));
                    mCtx.startActivity(intent);

                }
            });
        }
    }

    public MyAdapterCategories(Context mCtx,  ArrayList<String> names ) {
        this.mCtx = mCtx;
        mDataset = names;
        icons = new ArrayList<>(Arrays.asList(R.drawable.ic_breakfast,R.drawable.ic_sushi,R.drawable.ic_barbecue,R.drawable.ic_cake,R.drawable.ic_chicken,R.drawable.ic_fast_food, R.drawable.ic_pizza, R.drawable.ic_fish, R.drawable.ic_spicy_food, R.drawable.ic_sausages_1));
    }

    @Override
    public MyAdapterCategories.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /// create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_categories, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(MyAdapterCategories.ViewHolder holder, int position) {
        String name = mDataset.get(position);
        Drawable icon = mCtx.getResources().getDrawable(icons.get(position), null);
        holder.categorie.setText(name);
        holder.categorie.setCompoundDrawablesWithIntrinsicBounds(icon,null,null,null);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
