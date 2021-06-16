package com.lookballs.app.pictureselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PictureSelectorMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_selector_main);
    }

    public void openActivity(View v) {
        startActivity(new Intent(this,TestActivity.class));
    }

    public void openFragment(View v) {
        startActivity(new Intent(this,TestFragmentActivity.class));
    }

}
