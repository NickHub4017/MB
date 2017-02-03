package moba.moba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nrv on 2/2/17.
 */

public class SearchResultAdapter extends BaseAdapter {

    ArrayList<SearchResultUtil> res;
    Context context;

    public SearchResultAdapter(ArrayList<SearchResultUtil> data,Context con) {
        res=data;
        context=con;
    }

    @Override
    public int getCount() {
        return res.size();
    }

    @Override
    public Object getItem(int i) {
        return res.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.search_row, null);//set layout for displaying items
        TextView resnumber = (TextView) view.findViewById(R.id.resnumber);//get id for image view
        TextView restopic = (TextView) view.findViewById(R.id.restopic);//get id for image view
        TextView resdesc = (TextView) view.findViewById(R.id.resdesc);//get id for image view

        resnumber.setAllCaps(true);

        resnumber.setText(""+(i+1));
        restopic.setText(res.get(i).getTitile());
        resdesc.setText(res.get(i).getDescription());
        return view;
    }
}
