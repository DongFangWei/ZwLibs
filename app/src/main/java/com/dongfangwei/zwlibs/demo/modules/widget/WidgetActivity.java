package com.dongfangwei.zwlibs.demo.modules.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.dongfangwei.zwlibs.demo.R;
import com.dongfangwei.zwlibs.widget.CircleProgressBar;
import com.dongfangwei.zwlibs.widget.CircleSeekBar;

public class WidgetActivity extends Activity implements View.OnClickListener {
    private CircleProgressBar progressBar;
    private CircleSeekBar seekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        progressBar = findViewById(R.id.circleProgressBar);
        seekBar = findViewById(R.id.circleSeekBar);
        findViewById(R.id.progress_subtract_tv).setOnClickListener(this);
        findViewById(R.id.progress_add_tv).setOnClickListener(this);
        RadioGroup group = findViewById(R.id.progress_type_rg);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.progress_type_circle_rb) {
                    progressBar.setProgressType(CircleProgressBar.TYPE_CIRCLE);
                    seekBar.setProgressType(CircleProgressBar.TYPE_CIRCLE);
                } else {
                    progressBar.setProgressType(CircleProgressBar.TYPE_ARC);
                    seekBar.setProgressType(CircleProgressBar.TYPE_ARC);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.progress_subtract_tv:
                progressBar.setProgress(progressBar.getProgress() - 5);
                break;
            case R.id.progress_add_tv:
                progressBar.setProgress(progressBar.getProgress() + 5);
                break;
        }
    }
}
