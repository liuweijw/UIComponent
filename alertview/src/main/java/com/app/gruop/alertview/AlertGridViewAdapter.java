package com.app.gruop.alertview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bigkoo.alertview.R;

import java.util.List;

public class AlertGridViewAdapter extends BaseAdapter{
    private List<Drawable> mDatas;
    private List<String> mOthers;
    public AlertGridViewAdapter(List<Drawable> datas, List<String> mOthers){
        this.mDatas =datas;
        this.mOthers =mOthers;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Drawable data= mDatas.get(position);
        String text = mOthers.get(position);
        Holder holder=null;
        View view =convertView;
        if(view==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view=inflater.inflate(R.layout.alertview_item_alertgrid, null);
            holder=creatHolder(view);
            view.setTag(holder);
        }
        else{
            holder=(Holder) view.getTag();
        }
        holder.UpdateUI(parent.getContext(),data,text,position);
        return view;
    }
    public Holder creatHolder(View view){
        return new Holder(view);
    }
    class Holder {
        private TextView tvAlert;

        public Holder(View view){
            tvAlert = (TextView) view.findViewById(R.id.tvAlert);
        }
        public void UpdateUI(Context context,Drawable data,String text,int position){
            tvAlert.setCompoundDrawablesWithIntrinsicBounds(null, data, null, null);
            tvAlert.setText(text);
        }
    }
}