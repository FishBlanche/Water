package com.myp.water.tools;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myp on 2016/2/26.
 */
public class AgentApplication extends Application {

    public static String ipStr="";
    public static String portStr="";



    private List<Activity> activities = new ArrayList<Activity>();
    private static AgentApplication instance=new AgentApplication();
    /**
     *  获得实例
     * @return
     */
    public static AgentApplication getInstance(){
        return instance;
    }
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        for (Activity activity : activities) {
            activity.finish();
        }

        System.exit(0);
    }

}
