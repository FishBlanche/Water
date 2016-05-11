package com.myp.water.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.myp.water.R;

import java.util.List;
import java.util.Map;

/**
 * Created by myp on 2016/2/26.
 */
public class EuclidListAdapter extends ArrayAdapter<Map<String, Object>> {

    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_FUNCNAME = "funcname";

    private final LayoutInflater mInflater;
    private List<Map<String, Object>> mData;

    public EuclidListAdapter(Context context, int layoutResourceId, List<Map<String, Object>> data) {
        super(context, layoutResourceId, data);
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mListItemAvatar = (ImageView) convertView.findViewById(R.id.image_view_avatar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mListItemAvatar.setBackgroundResource((Integer) mData.get(position).get(KEY_AVATAR));
        return convertView;
    }

    static class ViewHolder {

        ImageView mListItemAvatar;

    }
}
