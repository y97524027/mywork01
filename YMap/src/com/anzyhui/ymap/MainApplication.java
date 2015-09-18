package com.anzyhui.ymap;

import android.app.Application;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by anzyhui on 2015/9/15
 * Project name: YMap
 * Email: anzyhui@163.com
 * no pains ,no gains.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        SDKInitializer.initialize(this);
        super.onCreate();
    }
}
