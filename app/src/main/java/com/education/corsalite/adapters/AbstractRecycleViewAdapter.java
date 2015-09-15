package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish Kumar on 12/09/15.
 */
public abstract class AbstractRecycleViewAdapter<V, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K>{
    private final String TAG = AbstractRecycleViewAdapter.class.getSimpleName();

    protected List<V> data = new ArrayList<V>();

    public boolean addItem(V item, int pos){
        boolean added = data.add(item);
        notifyItemInserted(pos);
        return added;
    }

    public void addAll(List<V> data){
        if(data == null || data.size() == 0){
            return;
        }
        int i = this.data.size();
        for(V item : data){
            addItem(item, i++);
        }
    }

    public V removeItem(int pos){
        V item = data.remove(pos);
        notifyItemRemoved(pos);
        return item;
    }

    public void removeAll(){
        if(data.size() == 0){
            return;
        }
        for(int i = this.data.size() - 1; i > 0; --i){
            removeItem(i);
        }
    }

    public void setData(List<V> data){
        if(data == null || data.size() == 0){
            return;
        }

        //remove old data
        removeAll();

        //add new data
        addAll(data);
    }

    public V getItem(int position){
        return data.get(position);
    }

    public int getItemPosition(V item){
        return data.indexOf(item);
    }

    public boolean isEmpty(){
        return data.size() == 0;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
