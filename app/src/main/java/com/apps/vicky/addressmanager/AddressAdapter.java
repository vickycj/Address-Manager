package com.apps.vicky.addressmanager;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vicky cj on 26-11-2017.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> implements Filterable {


    private List<UserData> userDataList;
    private List<UserData> userDataListFiltered;
    private LongClickInterface longClickInterface;

    public AddressAdapter(List<UserData> userDataList,LongClickInterface longClickInterface){
        this.userDataList=userDataList;
        userDataListFiltered=userDataList;
        this.longClickInterface=longClickInterface;
    }


    public interface LongClickInterface{
        void longClickEvent(int position);
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.address_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        AddressViewHolder addressViewHolder=new AddressViewHolder(view);


        return addressViewHolder;
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, final int position) {
        UserData userData;
        userData=userDataList.get(position);
        holder.addressName.setText(userData.getAddressName());
        holder.addressValue.setText(userData.getAddressValue());


        holder.cardView.setLongClickable(true);
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {

                longClickInterface.longClickEvent(position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    public void swapData(List<UserData> userDataListNew){

        userDataList = userDataListNew;
        userDataListFiltered=userDataListNew;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        userDataList.remove(position);

        notifyItemRemoved(position);
    }

    public void restoreItem(UserData userData, int position){
        userDataList.add(position, userData);

        notifyItemInserted(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString=constraint.toString();
                if(charString.isEmpty()){
                    userDataList=userDataListFiltered;
                }else{
                    List<UserData> filteredList=new ArrayList<>();
                    for(UserData data:userDataListFiltered){
                        if(data.getAddressName().toLowerCase().contains(charString.toLowerCase())
                                ||data.getAddressValue().toLowerCase().contains(charString.toLowerCase())){

                            filteredList.add(data);

                        }
                    }

                    userDataList=filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userDataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userDataList = (ArrayList<UserData>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class AddressViewHolder extends RecyclerView.ViewHolder{

        TextView addressName;

        TextView addressValue;

        public RelativeLayout backgroundView;
        public LinearLayout foregroundView;

        public CardView cardView;

        public AddressViewHolder(View itemView) {
            super(itemView);
            addressName=  itemView.findViewById(R.id.address_name);
            addressValue=  itemView.findViewById(R.id.address_text);
            backgroundView=itemView.findViewById(R.id.background_view);
            foregroundView=itemView.findViewById(R.id.foreground_view);
            cardView=itemView.findViewById(R.id.card_view);


        }
    }
}
