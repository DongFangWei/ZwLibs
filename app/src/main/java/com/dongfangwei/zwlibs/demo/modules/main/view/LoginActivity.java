package com.dongfangwei.zwlibs.demo.modules.main.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dongfangwei.zwlibs.demo.R;
import com.dongfangwei.zwlibs.demo.modules.user.entity.UserInfo;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView usernameEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEt = findViewById(R.id.login_username_et);
        findViewById(R.id.login_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        login();
    }

    private void login() {
        String username = usernameEt.getText().toString();
        if (username.length() == 0) {
            Toast.makeText(this, R.string.login_hint_username, Toast.LENGTH_SHORT).show();
            return;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        MainActivity.start(this, userInfo);
        finish();
    }
}
