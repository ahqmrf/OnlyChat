package apps.ahqmrf.onlychat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import apps.ahqmrf.onlychat.R;
import apps.ahqmrf.onlychat.model.Message;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lenovo on 2/22/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final int LEFT = 1;
    private final int RIGHT = 2;
    private Context context;
    private String id;
    private ArrayList<Message> itemList;
    private Bitmap avatar;
    private int chatColor;

    public void setChatColor(int chatColor) {
        this.chatColor = chatColor;
    }

    public MessageAdapter(Context context, String id, ArrayList<Message> itemList, int chatColor) {
        this.context = context;
        this.id = id;
        this.itemList = itemList;
        this.avatar = null;
        this.chatColor = chatColor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case RIGHT:
                holder = new ViewHolder(inflater.inflate(R.layout.sent_msg_item, parent, false));
                break;
            default:
                holder = new ViewHolder(inflater.inflate(R.layout.received_msg_item, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = itemList.get(position);
        holder.msg.setText(message.getText());
        if(holder.getItemViewType() == RIGHT) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, chatColor));
        }
        if (message.getReadBySentTo().equals("1") && holder.getItemViewType() == RIGHT) {
            if (avatar != null) {
                holder.seen.setImageBitmap(avatar);
                holder.seen.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
            else {
                holder.seen.setImageResource(R.drawable.ic_account_circle_black_24dp);
                holder.seen.setBackgroundColor(ContextCompat.getColor(context, chatColor));
            }
        } else if (message.getReadBySentTo().equals("0") && holder.getItemViewType() == RIGHT) {
            holder.seen.setImageResource(R.drawable.ic_done_white_24dp);
            holder.seen.setBackgroundColor(ContextCompat.getColor(context, chatColor));
        }
        if (holder.getItemViewType() == LEFT) {
            if (avatar != null) {
                holder.seen.setImageBitmap(avatar);
            }
            else holder.seen.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position).getId().equals(id)) {
            return RIGHT;
        }
        return LEFT;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView msg;
        public CircleImageView seen;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.tv_msg);
            seen = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            cardView = (CardView) itemView.findViewById(R.id.card);
        }
    }
}
