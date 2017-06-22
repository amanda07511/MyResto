package iut.myresto.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import iut.myresto.R;
import iut.myresto.models.Comment;

/**
 * Created by amanda on 01/06/2017.
 */

public class MyAdapterComment extends RecyclerView.Adapter<MyAdapterComment.ViewHolder> {

    private Context mCtx;
    private ArrayList<Comment> mDataset;
    private ArrayList<Integer> icons;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements
        public TextView nomResto;
        public TextView message;
        public TextView date;
        public ImageView imgResto;
        public RatingBar ratingBar;


        public ViewHolder(View v) {
            super(v);

            nomResto = (TextView) v.findViewById(R.id.nom_resto);
            message = (TextView) v.findViewById(R.id.comment);
            date = (TextView) v.findViewById(R.id.date);
            imgResto = (ImageView) v.findViewById(R.id.photoResto);
            ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);

            //Adding a action when user make a long press in a row
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    //Call method showPopup
                    showPopup(v, getAdapterPosition());
                    return false;
                }
            });


        }
    }

    public MyAdapterComment(Context mCtx, ArrayList<Comment> comments ) {
        this.mCtx = mCtx;
        mDataset = comments;

    }

    @Override
    public MyAdapterComment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /// create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(MyAdapterComment.ViewHolder holder, int position) {
        Comment comment = mDataset.get(position);
        holder.nomResto.setText(comment.getResto().getNom());
        holder.message.setText(comment.getMessage());
        holder.date.setText(comment.getDate());
        holder.ratingBar.setRating((float) comment.getNote());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * Create a pop menu w
     * */
    public void showPopup(final View v, int pos) {

        PopupMenu popup = new PopupMenu(mCtx, v);
        popup.inflate(R.menu.menu_select);


        popup.show();
    }


}
