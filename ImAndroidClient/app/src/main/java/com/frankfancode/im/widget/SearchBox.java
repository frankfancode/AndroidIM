package com.frankfancode.im.widget;

/**
 * Created by Frank on 2016/2/8.
 */
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.frankfancode.im.R;

/**
 * SearchText
 * 搜索编辑框
 * @author chrishao
 */
public class SearchBox extends LinearLayout {
    //两个按钮
    private ImageView ib_searchtext_delete;
    private EditText et_searchtext_search;
    public SearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        //从一个打气筒获得一个view
        View view = LayoutInflater.from(context).inflate(R.layout.searchtext, null);
        //把获得的view加载到这个控件中
        addView(view);
        //把两个按钮从布局文件中找到
        ib_searchtext_delete = (ImageView) view.findViewById(R.id.ib_searchtext_delete);
        et_searchtext_search = (EditText) view.findViewById(R.id.et_searchtext_search);
        //给删除按钮添加点击事件
        ib_searchtext_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                et_searchtext_search.setText("");
            }
        });
        //给编辑框添加文本改变事件
        et_searchtext_search.addTextChangedListener(new MyTextWatcher());
    }

    //文本观察者
    private class MyTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }
        //当文本改变时候的操作
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            //如果编辑框中文本的长度大于0就显示删除按钮否则不显示
            if(s.length() > 0){
                ib_searchtext_delete.setVisibility(View.VISIBLE);
            }else{
                ib_searchtext_delete.setVisibility(View.GONE);
            }
        }

    }
}