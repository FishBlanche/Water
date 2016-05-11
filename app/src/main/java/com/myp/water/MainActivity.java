package com.myp.water;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.myp.water.tools.AgentApplication;
import com.myp.water.tools.EditTextFocusListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btn_set;
    private Button btn_distribution;
    private Button btn_trend;
    private long exitTime = 0;
    private  Thread networkTaskTh;
    private  MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


           AgentApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_main);
        EditTextFocusListener ef=new EditTextFocusListener();
        EditText etip=(EditText) findViewById(R.id.et_ip);
        etip.setOnFocusChangeListener(ef);
        EditText etport=(EditText) findViewById(R.id.et_port);
        etport.setOnFocusChangeListener(ef);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String iptext = sharedPref.getString(getString(R.string.myip), "");
        EditText editText = (EditText) findViewById(R.id.et_ip);
        editText.setText(iptext);
        String porttext = sharedPref.getString(getString(R.string.myport), "");
        EditText porteditText = (EditText) findViewById(R.id.et_port);
        porteditText.setText(porttext);
        myHandler=new MyHandler();
/*
        btn_set=(Button) this.findViewById(R.id.mysetting);
        btn_distribution=(Button) this.findViewById(R.id.myDistribution);
        btn_trend=(Button) this.findViewById(R.id.myTrend);*/

        //check the state of Internet


    }
     private void CheckNetStatus()
    {
       ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
       boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
       boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
       if(wifi|internet){
        //执行相关操作

           networkTaskTh=  new Thread(networkTask);

           networkTaskTh.start();

       }else{
           Toast tt=Toast.makeText(getApplicationContext(),
                   "亲，网络连了么？", Toast.LENGTH_LONG);
           tt.setGravity(Gravity.CENTER, 0, 100);
           tt.show();
           setConnectBtnVisible();
       }
     }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
    public void setConnectBtnVisible()
    {
        View btnConn= findViewById(R.id.btn_login);
        btnConn.setVisibility(View.VISIBLE);
        View   mloadingView = findViewById(R.id.loading_spinner);
        mloadingView.setVisibility(View.GONE);
    }
    public void connectHandle(View view) {

        checkClick(findViewById(R.id.mycb));
        view.setVisibility(View.GONE);
        View   mloadingView = findViewById(R.id.loading_spinner);
        mloadingView.setVisibility(View.VISIBLE);

        EditText et1 = (EditText) findViewById(R.id.et_ip);
        AgentApplication.ipStr = et1.getText().toString();
        EditText et2 = (EditText) findViewById(R.id.et_port);
        AgentApplication.portStr = et2.getText().toString();



        View   mwarningView = findViewById(R.id.warning);
        mwarningView.setVisibility(View.GONE);
        CheckNetStatus();




                /*
        Intent intent=new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);*/
    }
    Runnable networkTask;

    {
        networkTask = new Runnable() {

            @Override
            public void run() {
                // TODO
                // 在这里进行 http request.网络请求相关操作


                String baseURL = "http://"+AgentApplication.ipStr+":"+AgentApplication.portStr+"/mapservlet/Service";
                URL url = null;

                try {
                    url = new URL(baseURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setConnectTimeout(8 * 1000);
                    conn.connect();

                    Message msg = new Message();
                    msg.what=2;
                    myHandler.sendMessage(msg);



                    /*
                    DataOutputStream dop=new DataOutputStream(conn.getOutputStream());
                    dop.writeBytes("dealtype=hello");//request all cities
                    dop.flush();
                    dop.close();

                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String res="";
                    String readLine="";
                    while((readLine=br.readLine())!=null)
                    {
                        res=res+readLine;
                    }
                    if(res.equals("world"))
                    {
                        View   mwarningView = findViewById(R.id.warning);
                        mwarningView.setVisibility(View.GONE);
                    }
                    else
                    {
                        View   mwarningView = findViewById(R.id.warning);
                        mwarningView.setVisibility(View.VISIBLE);
                    }
                    br.close();
                    conn.disconnect();*/

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("aaaaa", "connect error");
                    Message msg = new Message();
                    msg.what=1;
                    myHandler.sendMessage(msg);
                }


            }
        };
    }

    public void checkClick(View view) {
        CheckBox cb=(CheckBox)view ;
        if(cb.isChecked()){
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            EditText editText = (EditText) findViewById(R.id.et_ip);
            String ipmessage = editText.getText().toString();
            editor.putString(getString(R.string.myip), ipmessage);

            EditText editText1 = (EditText) findViewById(R.id.et_port);
            String portmessage = editText1.getText().toString();
            editor.putString(getString(R.string.myport), portmessage);
            editor.commit();
        }else{
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear().commit();
        }
    }



    //Activity创建或者从后台重新回到前台时被调用
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart", "onStart called.");
    }

    //Activity从后台重新回到前台时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart", "onRestart called.");
    }

    //Activity创建或者从被覆盖、后台重新回到前台时被调用
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "onResume called.");
    }

    //Activity窗口获得或失去焦点时被调用,在onResume之后或onPause之后
    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	Log.i(TAG, "onWindowFocusChanged called.");
    }*/

    //Activity被覆盖到下面或者锁屏时被调用
    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "onPause called.");
        //有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据
    }

    //退出当前Activity或者跳转到新Activity时被调用
    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop", "onStop called.");
    }

    //退出当前Activity时被调用,调用之后Activity就结束了
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("onDestroy", "onDestory called.");
        /*
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                conn = null;
            } finally {
                conn = null;
            }
        }*/
    }

    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Log.e("onSaveInstanceState", "onSaveInstanceState called. put param: ");
        super.onSaveInstanceState(outState);
    }

    /**
     * Activity被系统杀死后再重建时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死,用户又启动该Activity.
     * 这两种情况下onRestoreInstanceState都会被调用,在onStart之后.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        Log.e("onRestoreInstanceState", "onRestoreInstanceState called. get param: " );
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                checkClick(findViewById(R.id.mycb));
              //  finish();
                AgentApplication.getInstance().onTerminate();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法，接受数据
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            // 此处可以更新UI
            /*
            Bundle b = msg.getData();
            String curtype = b.getString("color");*/
            //   wva.setItems(Arrays.asList(PROVINCES));list
              switch(msg.what)
              {
               case 1:       View   mwarningView = findViewById(R.id.warning);
                             mwarningView.setVisibility(View.VISIBLE);
                             setConnectBtnVisible();
                               break;
                  case 2:   View   mwarningView1 = findViewById(R.id.warning);
                            mwarningView1.setVisibility(View.GONE);

                          Intent intent=new Intent(MainActivity.this, FuncSelectActivity.class);
                          startActivity(intent);
                          overridePendingTransition(R.anim.ap2, R.anim.ap1);// 淡出淡入动画效果
                           break;
              }
        }
    }
}
