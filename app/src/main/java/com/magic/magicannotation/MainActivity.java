package com.magic.magicannotation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.magic.magic.Magic;
import com.magic.magic_annotations.OnlyAvailable;
import com.magic.magic_annotations.OnlyDebug;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_main_debug)
    @OnlyDebug()
    TextView tvDebug;

    @BindView(R.id.tv_main_release)
    @OnlyAvailable("release")
    TextView tvRelease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Magic.conjure(this, BuildConfig.BUILD_TYPE);

    }
}
