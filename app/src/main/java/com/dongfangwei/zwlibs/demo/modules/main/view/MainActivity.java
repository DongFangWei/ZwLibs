package com.dongfangwei.zwlibs.demo.modules.main.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dongfangwei.zwlibs.navigation.NavigationBar;
import com.dongfangwei.zwlibs.demo.R;
import com.dongfangwei.zwlibs.demo.modules.home.view.HomeFragment;
import com.dongfangwei.zwlibs.demo.modules.main.presenter.MainPresenter;
import com.dongfangwei.zwlibs.demo.modules.main.presenter.MainPresenterImpl;
import com.dongfangwei.zwlibs.demo.modules.user.entity.UserInfo;
import com.dongfangwei.zwlibs.demo.modules.user.view.UserFragment;
import com.dongfangwei.zwlibs.demo.publics.Content;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView {
    private MainPresenter mPresenter;
    private Fragment currentFragment;
    private Fragment[] fragments;
    private UserInfo userInfo;

    public static void start(Context context, UserInfo info) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra(Content.EXTRA_DATA, info);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenterImpl(this);
        initView();
        userInfo = getIntent().getParcelableExtra(Content.EXTRA_DATA);
        getData();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.main_header_tb);
        setSupportActionBar(toolbar);
        fragments = new Fragment[2];
        fragments[0] = currentFragment = HomeFragment.newInstance();

        NavigationBar navigationBar = findViewById(R.id.main_navigation_nb);
        navigationBar.setSelectedItemAt(0, false);
        navigationBar.setOnSelectedListener(new NavigationBar.OnSelectedListener() {
            @Override
            public boolean onSelectedChanged(View newView, View oldView) {
                if (newView.getId() == R.id.main_home_ni) {
                    changeFragment(0);
                } else {
                    changeFragment(1);
                }
                return true;
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.main_fragment_fl, currentFragment);
        ft.commitAllowingStateLoss();
    }

    private void changeFragment(int showPosition) {
        Fragment fragment = fragments[showPosition];
        if (currentFragment != fragment) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(currentFragment);
            if (fragment == null) {
                fragments[showPosition] = fragment = UserFragment.newInstance(userInfo);
                ft.add(R.id.main_fragment_fl, fragment);
            } else {
                ft.show(fragment);
            }
            ft.commitAllowingStateLoss();
            currentFragment = fragment;
        }
    }

    private void getData() {

    }

    @Override
    public void getDataSuccess(List<String> list) {

    }

    @Override
    public void getDataFailure(int code, String msg) {

    }

}
