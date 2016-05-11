package com.myp.water;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.myp.water.tools.AgentApplication;
import com.myp.water.tools.EuclidListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncSelectActivity extends AppCompatActivity {
    private ListView mListView;
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_func_select);
        AgentApplication.getInstance().addActivity(this);

        initList();

    }
    public void initList()
    {
        Map<String, Object> profileMap;
        List<Map<String, Object>> profilesList = new ArrayList<>();
        int[] avatars = {R.mipmap.area,R.mipmap.trend
        };
        String[] NameS = new String[]{"area", "trend"};

        for (int i = 0; i < avatars.length; i++) {
            profileMap = new HashMap<>();
            profileMap.put(EuclidListAdapter.KEY_AVATAR, avatars[i]);
            profileMap.put(EuclidListAdapter.KEY_FUNCNAME,NameS[i]);
            profilesList.add(profileMap);
        }
        EuclidListAdapter la=new EuclidListAdapter(this, R.layout.list_item, profilesList);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(la);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, Object> a = (Map<String, Object>) parent.getItemAtPosition(position);
               if(a.get(EuclidListAdapter.KEY_FUNCNAME).equals("area"))
               {
                   Log.e("area","area selected");

                   Intent intent=new Intent(FuncSelectActivity.this, DistributionActivity.class);
                   startActivity(intent);
                   overridePendingTransition(R.anim.ap2, R.anim.ap1);// 淡出淡入动画效果
               }
                else if(a.get(EuclidListAdapter.KEY_FUNCNAME).equals("trend"))
               {
                   Log.e("trend","trend selected");

                   Intent intent=new Intent(FuncSelectActivity.this, TrendActivity.class);
                   startActivity(intent);
                   overridePendingTransition(R.anim.ap2, R.anim.ap1);// 淡出淡入动画效果
               }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {

               // finish();
                AgentApplication.getInstance().onTerminate();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
