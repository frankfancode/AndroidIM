package com.frankfancode.im.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frankfancode.im.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatHomeActivityFragment extends Fragment {

    public ChatHomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_home, container, false);
    }
}
