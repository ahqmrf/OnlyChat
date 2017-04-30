package apps.ahqmrf.onlychat.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import apps.ahqmrf.onlychat.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lenovo on 3/7/2017.
 */

public class ChatColorAdapter extends RecyclerView.Adapter<ChatColorAdapter.ColorViewHolder>{

    private Context mContext;
    private ArrayList<Integer> mColorItems;
    private Callback mCallback;


    public interface Callback {
        void onColorClick(int color);
    }

    public ChatColorAdapter(Context mContext, ArrayList<Integer> mColorItems, Callback callback) {
        this.mContext = mContext;
        this.mColorItems = mColorItems;
        this.mCallback = callback;
    }

    @Override
    public ChatColorAdapter.ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.color_item, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatColorAdapter.ColorViewHolder holder, int position) {
        //holder.colorView.setBackgroundColor(ContextCompat.getColor(mContext, mColorItems.get(position)));
        holder.colorView.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mContext, mColorItems.get(position))));
    }

    @Override
    public int getItemCount() {
        return mColorItems.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView colorView;

        public ColorViewHolder(View itemView) {
            super(itemView);
            colorView = (CircleImageView) itemView.findViewById(R.id.civ_color);
            colorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onColorClick(mColorItems.get(getAdapterPosition()));
                }
            });
        }
    }
}
