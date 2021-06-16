package com.lookballs.app.pictureselector;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

public class TestFragmentActivity extends FragmentActivity {

    private TestFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);

        // 在部分低端手机，调用单独拍照时内存不足时会导致activity被回收，所以不重复创建fragment
        if (savedInstanceState == null) {
            //添加显示第一个fragment
            fragment = new TestFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.tab_content, fragment, "TestFragment").show(fragment).commitAllowingStateLoss();
        } else {
            fragment = (TestFragment) getSupportFragmentManager().findFragmentByTag("TestFragment");
        }
    }

}
