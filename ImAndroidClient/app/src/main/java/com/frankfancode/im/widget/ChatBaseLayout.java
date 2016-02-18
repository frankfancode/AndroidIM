package com.frankfancode.im.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frankfancode.im.R;


public class ChatBaseLayout extends RelativeLayout {

	private TextView tv_header, tv_header_left,tv_header_right;
	public View header_bar, ll_header_left,ll_header_right;


	public ChatBaseLayout(Context context, int layoutResourceId) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		header_bar = layoutInflater.inflate(R.layout.header, null);
		header_bar.setId(R.id.header_bar);
		ll_header_left = header_bar.findViewById(R.id.ll_header_left);
		tv_header_left = (TextView) header_bar.findViewById(R.id.tv_header_left);
		ll_header_right=header_bar.findViewById(R.id.ll_header_right);
		tv_header_right=(TextView)header_bar.findViewById(R.id.tv_header_right);
		tv_header_left.setText("返回");
		tv_header = (TextView) header_bar.findViewById(R.id.tv_header_middle);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		addView(header_bar, params);
		View view = layoutInflater.inflate(layoutResourceId, null);
		params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.addRule(RelativeLayout.BELOW, R.id.header_bar);
		addView(view, params);
		ll_header_left.setVisibility(View.VISIBLE);
	}


	public void setTitle(String title) {
		if (title != null) {
			tv_header.setVisibility(View.VISIBLE);
			tv_header.setText(title);
		} else {
			tv_header.setVisibility(View.GONE);
		}
	}
	
	public void setLeft(String left) {
		tv_header_left.setText(left);
	}
	
	public void setRight1(String title) {
		if (title != null) {
			ll_header_right.setVisibility(View.VISIBLE);
			tv_header_right.setVisibility(View.VISIBLE);
			tv_header_right.setText(title);
		} else {
			ll_header_right.setVisibility(View.GONE);
			tv_header_right.setVisibility(View.GONE);
		}
	}
	/**
	 * 右上角为图片,文本为空
	 * @param title
	 */
	public void setRight1Drawable(int id) {
		Drawable drawable=getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		tv_header_right.setCompoundDrawables(null, null, drawable,null);
		ll_header_right.setVisibility(View.VISIBLE);
		tv_header_right.setVisibility(View.VISIBLE);
		tv_header_right.setGravity(Gravity.CENTER);
		tv_header_right.setPadding(32, 0, 32, 0);
		tv_header_right.setText("");
	}
	/**
	 * 设置右上角的文字颜色
	 * @param color
	 */
	public void setRight1TextColor(int color){
		tv_header_right.setTextColor(color);
	}
}
