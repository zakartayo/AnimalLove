package com.example.animallove.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.animallove.Classes.Recycler_item;
import com.example.animallove.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by 이승헌 on 2017-12-01.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Recycler_item> items;
    int item_layout;
    private FirebaseStorage storage;

    public RecyclerAdapter(Context context, List<Recycler_item> items, int item_layout) {
        this.context=context;
        this.items=items;
        this.item_layout=item_layout;
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview,null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Recycler_item item=items.get(position);
        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        StorageReference storageRef = storage.getReferenceFromUrl("gs://animallove-63f5c.appspot.com").child(item.getImage());
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.image.setImageBitmap(bitmap);
            }
        });

        holder.name.setText(item.getName());
        holder.region.setText(item.getRegion());
        holder.gender.setText(item.getGender());
        holder.kind.setText(item.getKind());

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,item.getTitle(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, region, gender, kind, desc;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.image);
            name=(TextView)itemView.findViewById(R.id.name);
            region=(TextView)itemView.findViewById(R.id.region);
            gender=(TextView)itemView.findViewById(R.id.gender);
            kind=(TextView)itemView.findViewById(R.id.kind);
            cardview=(CardView)itemView.findViewById(R.id.cardview);
        }
    }
}