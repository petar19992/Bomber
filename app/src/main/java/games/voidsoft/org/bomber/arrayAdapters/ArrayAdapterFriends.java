package games.voidsoft.org.bomber.arrayAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import games.voidsoft.org.bomber.R;
import games.voidsoft.org.bomber.objectItems.ObjectItemFriend;

/**
 * Created by Petar on 7/4/2015.
 */
public class ArrayAdapterFriends extends ArrayAdapter<ObjectItemFriend> {

    Context mContext;
    int layoutResourceId;
    ObjectItemFriend data[] = null;

    public ArrayAdapterFriends(Context context, int resource, ObjectItemFriend[] objects) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.mContext = context;
        this.data = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }
        /*Context con = null;
        View vi=convertView;
        if(convertView==null){
            convertView = getActivity().getLayoutInflater().inflate(R.layout.stores_listview_layout, parent, false);
        }*/



        // object item based on the position
        ObjectItemFriend objectItem = data[position];

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewFriendsUsername);
//        textViewItem.setText(objectItem.itemName);
        textViewItem.setTag(objectItem.friendID);
        textViewItem.setText(objectItem.friendUsername);
        ImageView avatar=(ImageView) convertView.findViewById(R.id.imageViewFriendsAvatar);
        avatar.setImageBitmap(objectItem.friendAvatar);

        //return vi;
        return convertView;

    }
}
