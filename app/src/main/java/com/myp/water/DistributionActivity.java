package com.myp.water;

import android.app.Activity;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.myp.water.tools.AgentApplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class DistributionActivity extends AppCompatActivity {

    public static final String KEY_TYPE_ID="MainActivity_KEY_PROVINCE_ID";
    public static final String KEY_Detail_ID="KEY_Detail_ID";
    private Button btn_province;
   // private Button btn_city;
    private Button btn_factory;
    private DatagramSocket socket;
    private InetAddress remoteIP;
    private MySurfaceView sfv;
    public static Queue<String> queue=new LinkedList<String>();
/*
    private static final String REMOTE_IP = "192.168.0.226:3306";
    private static final String URL = "jdbc:mysql://" + REMOTE_IP + "/citysee_data";
    private static final String USER = "admin";
    private static final String PASSWORD = "citysee";
    public static Connection conn;*/

    private boolean isRun=false;
    private long exitTime = 0;
    MyHandler myHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_distribution);
        AgentApplication.getInstance().addActivity(this);
      //  queue.add("你好");
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        toolbar.setTitle("区域分布");
        toolbar.setTitleTextColor(0XFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.backward);*/
/*
        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        toolbar1.setTitle("Water Quality Node Distribution");
        toolbar1.setTitleTextColor(0XFFFFFFFF);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);*/
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        btn_province=(Button) this.findViewById(R.id.myProvince);
      //  btn_city=(Button) this.findViewById(R.id.myCity);
        btn_factory=(Button) this.findViewById(R.id.myFactory);
        sfv=(MySurfaceView) this.findViewById(R.id.sfv);
        //check the state of Internet
        CheckNetStatus();

        new Thread(networkTask).start();
        isRun=true;
    }
    private void CheckNetStatus()
    {
        ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if(wifi|internet){
            //执行相关操作
        }else{
            Toast tt=Toast.makeText(getApplicationContext(),
                    "亲，网络连了么？", Toast.LENGTH_LONG);
            tt.setGravity(Gravity.CENTER,0, 0);
            tt.show();

        }
    }



    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            try {
                /*
                String ipAddr="192.168.0.169";
                String[] ipStr = ipAddr.split("\\.");
                byte[] ipBuf = new byte[4];
                for(int i = 0; i < 4; i++){
                    ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
                }*/

                remoteIP= InetAddress.getByName(AgentApplication.ipStr);
                Log.e("remoteIP","8888"+remoteIP);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                socket = new DatagramSocket();

            } catch (SocketException e) {
                e.printStackTrace();
            }
            //connect database
            //  conn = Util.openConnection(URL, USER, PASSWORD);
            while(isRun)
            {
                if(!queue.isEmpty())
                {
                    String pro = queue.poll();
                    Log.e("propro","proaaaa"+pro);
                    byte[] outputData = pro.getBytes();
                    DatagramPacket outputPacket   = new DatagramPacket(outputData,outputData.length,remoteIP,8888);
                    try {
                        socket.send(outputPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
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
    public void comeback(View view) {
        finish();
    }
    public void showProvince(View view) {

        //发送意图标示为REQUSET=1
        Intent intent=new Intent(this, SelectCityWindow.class);
        intent.putExtra(KEY_TYPE_ID, "province");
        btn_province.setText("省份:");
       // btn_city.setText("城市:");
        btn_factory.setText("水厂:");
        sfv.setVisibility(View.INVISIBLE);
        startActivityForResult(intent, 1);
    }
    public void showCity(View view) {

        //发送意图标示为REQUSET=1
        Intent intent=new Intent(this, SelectCityWindow.class);
        intent.putExtra(KEY_TYPE_ID, "city");
        String [] temp = null;
        temp=btn_province.getText().toString().split(":");
       // Log.e("tempprint","mytemp"+temp.length);
        if(temp.length==1)
        {
            intent.putExtra(KEY_Detail_ID, "你好");
        }
        else
        {
            intent.putExtra(KEY_Detail_ID, temp[1]);
        }
     //   btn_city.setText("城市:");
        btn_factory.setText("水厂:");
        sfv.setVisibility(View.INVISIBLE);
        startActivityForResult(intent, 2);
    }
    public void showFactory(View view) {

        //发送意图标示为REQUSET=1
        Intent intent=new Intent(this, SelectCityWindow.class);
        intent.putExtra(KEY_TYPE_ID, "factory");
        String [] temp = null;
        temp=btn_province.getText().toString().split(":");
        if(temp.length==1)
        {
            intent.putExtra(KEY_Detail_ID, "你好");
        }
        else
        {
            intent.putExtra(KEY_Detail_ID, temp[1]);
        }
       btn_factory.setText("水厂:");
        sfv.setVisibility(View.INVISIBLE);
        startActivityForResult(intent, 3);
    }
    public void showCountry(View view) {
        btn_province.setText("省份:");
      //  btn_city.setText("城市:");
        btn_factory.setText("水厂:");
        sfv.setVisibility(View.VISIBLE);
        queue.add("country");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode标示请求的标示   resultCode表示有数据
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String str = "省份:"
                    + data.getStringExtra(SelectCityWindow.KEY_PROVINCE_ID);
            btn_province.setText(str);
            /*
            Message msg = new Message();
            Bundle mydata = new Bundle();
            mydata.putString("value", data.getStringExtra(SelectCityWindow.KEY_PROVINCE_ID));
            msg.setData(mydata);
            handler.sendMessage(msg);*/
            String sendStr="province:"+data.getStringExtra(SelectCityWindow.KEY_PROVINCE_ID);
            queue.add(sendStr);

        }
        else if(requestCode ==2 && resultCode == RESULT_OK)
        {
            String str = "城市:"
                    + data.getStringExtra(SelectCityWindow.KEY_CITY_ID);
        //    btn_city.setText(str);
            /*
            Message msg = new Message();
            Bundle mydata = new Bundle();
            mydata.putString("value", data.getStringExtra(SelectCityWindow.KEY_PROVINCE_ID));
            msg.setData(mydata);
            handler.sendMessage(msg);*/

            //    queue.add(data.getStringExtra(SelectCityWindow.KEY_CITY_ID));
        }
        else if(requestCode ==3 && resultCode == RESULT_OK)
        {
            String str = "水厂:"
                    + data.getStringExtra(SelectCityWindow.KEY_FACTORY_ID);
            btn_factory.setText(str);
            /*
            Message msg = new Message();
            Bundle mydata = new Bundle();
            mydata.putString("value", data.getStringExtra(SelectCityWindow.KEY_PROVINCE_ID));
            msg.setData(mydata);
            handler.sendMessage(msg);*/
            String sendStr="factory:"+data.getStringExtra(SelectCityWindow.KEY_FACTORY_ID);
            queue.add(sendStr);
        }
        /*
        Toast.makeText(
                this,
                "requestCode=" + requestCode + ":" + "resultCode=" + resultCode,
                Toast.LENGTH_LONG).show();*/
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
        isRun=false;
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
    public boolean onTouchEvent(MotionEvent event) {
     //  Log.e("disTouch","disTouch]]]");
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
            //    finish();
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
                case 1:break;
            }
        }
    }

}
