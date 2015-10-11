package com.frankfann.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {

	protected final String TAG = this.getClass().getSimpleName();
 
	protected Context mContext;
	protected List<T> mValues;
	protected LayoutInflater mInflater;

	public BaseListAdapter(Context context, List<T> values) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mValues = values;
	}

	public Context getContext() {
		return mContext;
	}

	@Override
	public int getCount() {
		if (mValues != null)
			return mValues.size();
		return 0;
	}

	@Override
	public T getItem(int position) {
		if (position == getCount() - 1 || mValues == null) {
			return null;
		}
		return mValues.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItemView(convertView, position);
	}

	protected abstract View getItemView(View convertView, int position);

	public void update(List<T> values) {
		mValues = values;
		notifyDataSetInvalidated();
		notifyDataSetChanged();
	}
}
