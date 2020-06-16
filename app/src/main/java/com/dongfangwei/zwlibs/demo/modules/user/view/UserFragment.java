package com.dongfangwei.zwlibs.demo.modules.user.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dongfangwei.zwlibs.demo.R;
import com.dongfangwei.zwlibs.demo.modules.user.entity.UserInfo;

public class UserFragment extends Fragment {
    private static final String ER_DATA = "DATA";
    private ImageView photoIv;
    private TextView usernameTv;
    private UserInfo userInfo;

    public static UserFragment newInstance(UserInfo info) {
        Bundle args = new Bundle();
        args.putParcelable(ER_DATA, info);
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
            userInfo = bundle.getParcelable(ER_DATA);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoIv = view.findViewById(R.id.user_photo_iv);
        usernameTv = view.findViewById(R.id.user_username_tv);
        photoIv.setImageResource(R.mipmap.ic_launcher_round);
        usernameTv.setText(userInfo.getUsername());
    }
}
