package com.myp.water;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myp.water.tools.AgentApplication;
import com.myp.water.tools.WheelView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SelectCityWindow extends Activity implements OnClickListener{
    private Type type_list_user = new TypeToken<ArrayList<String>>() {
    }.getType();
    private Gson gson = new Gson();

    private ImageButton btn_cancel, btn_ok,btn_cancel1;
    private LinearLayout layout;

    private WheelView wva;
    private ProgressBar pbar;
    private TextView mytvwarning;

    private  String type;
    private  String detaildata;
    protected int activityCloseExitAnimation;
    protected int activityOpenExitAnimation;
    public static final String KEY_PROVINCE_ID="KEY_PROVINCE_ID";
    public static final String KEY_CITY_ID="KEY_CITY_ID";
    public static final String KEY_FACTORY_ID="KEY_FACTORY_ID";

  private static final String[] PROVINCES = new String[]{"江苏省", "浙江省", "山东省", "湖南省", "海南省"};
  //  private static final String[] CITYS = new String[]{"上海市", "北京市", "无锡市", "南京市", "苏州市"};
    List<String> plist=new ArrayList<String>();
    List<String> clist=new ArrayList<String>();
    List<String> flist=new ArrayList<String>();
    private  Thread networkTaskTh;
    MyHandler myHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_city_window);

       pbar=(ProgressBar) this.findViewById(R.id.loading);
        mytvwarning=(TextView)this.findViewById(R.id.txwarning);

        btn_cancel = (ImageButton) this.findViewById(R.id.btn_cancel);
        btn_ok = (ImageButton) this.findViewById(R.id.btn_ok);
        btn_cancel1=(ImageButton) this.findViewById(R.id.btn_cancel1);

        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_cancel1.setOnClickListener(this);

        layout=(LinearLayout)findViewById(R.id.pop_layout);

        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
              //  Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
              //          Toast.LENGTH_SHORT).show();
            }
        });
        //添加按钮监听
        myHandler=new MyHandler();


        wva = (WheelView) findViewById(R.id.main_wv);
        wva.setOffset(1);
  //      wva.setItems(Arrays.asList(PROVINCES));


        Intent intent=getIntent();
        type=intent.getStringExtra(DistributionActivity.KEY_TYPE_ID);
        detaildata=intent.getStringExtra(DistributionActivity.KEY_Detail_ID);
        networkTaskTh=  new Thread(networkTask);
        networkTaskTh.start();
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.e("onSelected", "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });

        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowAnimationStyle});
        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
        activityStyle.recycle();
        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[]{android.R.attr.activityCloseExitAnimation, android.R.attr.activityOpenExitAnimation});
        activityCloseExitAnimation = activityStyle.getResourceId(0, 0);
        activityOpenExitAnimation=activityStyle.getResourceId(1, 0);
        activityStyle.recycle();
    }
    Runnable networkTask;

    {
        networkTask = new Runnable() {

            @Override
            public void run() {
                // TODO
                // 在这里进行 http request.网络请求相关操作
            /*
            String[] strs=null;
            if(type.equals("province"))
            {
                Connection conn = MainActivity.conn;
                String sql = "select * from nodeuserinfo";
                Statement pst = null;
                try {
                    pst = conn.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ResultSet rs = null;

                try {
                    rs = pst.executeQuery(sql);
                    rs.last();
                    int rowCount = rs.getRow();
                    strs = new String[rowCount];
                    rs.beforeFirst();
                    int position = 0;
                    while(rs.next()){
                        String str =rs.getString("userName");
                        strs[position++]=str;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                wva.setItems(Arrays.asList(strs));
            }
            else if(type.equals("city"))
            {
                wva.setItems(Arrays.asList(CITYS));
            }*/


                String baseURL = "http://"+ AgentApplication.ipStr+":"+AgentApplication.portStr+"/mapservlet/Service";
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

                        conn.connect();
                        DataOutputStream dop=new DataOutputStream(conn.getOutputStream());
                        if(type.equals("province"))
                        {
                            dop.writeBytes("dealtype=1");//request all provinces
                            Log.e("propro","propro");
                        }
                        else if(type.equals("city"))
                        {
                           // dop.writeBytes("dealtype=2");//request all cities
                            Log.e("具体的", "dealdata=" + detaildata);
                            String temp="dealtype=2&dealdata="+detaildata;
                         //   dop.writeBytes();
                            dop.write(temp.getBytes());
                        }
                        else if(type.equals("factory"))
                        {
                            //dop.writeBytes("dealtype=3");//request all factories
                            String temp="dealtype=3&dealdata="+detaildata;
                            //   dop.writeBytes();
                            dop.write(temp.getBytes());

                        }
                        dop.flush();
                        dop.close();
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String res="";
                        String readLine="";

                        if(type.equals("province"))
                        {
                            while((readLine=br.readLine())!=null)
                           {//  PROVINCES[count++]=readLine;
                              res=res+readLine;
                           }

                            plist=gson.fromJson(res,type_list_user);
                            Log.e("my list", "love " + plist.get(1));


                        }
                        else if(type.equals("city"))
                        {
                            while((readLine=br.readLine())!=null)
                            {//  PROVINCES[count++]=readLine;
                                res=res+readLine;
                                //clist.add(readLine);
                            }
                            Log.e("my list res", "  "+res);
                            clist=gson.fromJson(res,type_list_user);
                        }
                        else if(type.equals("factory"))
                        {
                            while((readLine=br.readLine())!=null)
                            {//  PROVINCES[count++]=readLine;
                                res=res+readLine;
                                //clist.add(readLine);
                            }
                            flist=gson.fromJson(res,type_list_user);
                        }

                        br.close();
                        conn.disconnect();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                Message msg = new Message();
                msg.what=1;
                myHandler.sendMessage(msg);

            }
        };
    }   /*
                Bundle b = new Bundle();// 存放数据
                b.putString("curtype", "me");
                msg.setData(b);*/
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
         //   wva.setItems(Arrays.asList(PROVINCES));list\



              Log.e("handleMessage:","ipdate list");
             pbar.setVisibility(View.GONE);

            if(type.equals("province"))
            {
               // wva.setItems(Arrays.asList(PROVINCES));


                if(plist.isEmpty())
                {
                    mytvwarning.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.GONE);
                    btn_ok.setVisibility(View.GONE);
                    btn_cancel1.setVisibility(View.VISIBLE);
                    Log.e("isProvince",":Empty()");
                }
                else
                {
                    wva.setItems(plist);
                    wva.setVisibility(View.VISIBLE);
                }

            }
            else if(type.equals("city"))
            {
             //   wva.setItems(clist);
                if(clist.isEmpty())
                {
                    mytvwarning.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.GONE);
                    btn_ok.setVisibility(View.GONE);
                    btn_cancel1.setVisibility(View.VISIBLE);
                }
                else
                {
                    wva.setItems(clist);
                    wva.setVisibility(View.VISIBLE);
                }
            }
            else if(type.equals("factory"))
            {
            //    wva.setItems(flist);
                if(flist.isEmpty())
                {
                    mytvwarning.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.GONE);
                    btn_ok.setVisibility(View.GONE);
                    btn_cancel1.setVisibility(View.VISIBLE);
                }
                else
                {
                    wva.setItems(flist);
                    wva.setVisibility(View.VISIBLE);
                }
            }

        }
    }
    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event){
        //finish();
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                break;
            case R.id.btn_cancel1:
                break;
            case R.id.btn_ok:
                Intent intent=new Intent();
                if(type.equals("province"))
                {
                    intent.putExtra(KEY_PROVINCE_ID, wva.getSeletedItem());
                }
                else if(type.equals("city"))
                {
                    intent.putExtra(KEY_CITY_ID, wva.getSeletedItem());
                }
                else if(type.equals("factory"))
                {
                    intent.putExtra(KEY_FACTORY_ID, wva.getSeletedItem());
                    Log.e("eee","factory"+wva.getSeletedItem());
                }
                setResult(RESULT_OK, intent);
                break;

            default:
                break;
        }
        finish();
    }
    @Override

    public void finish() {

        Log.e("SelectCityWindow:", "finish");

        super.finish();

        overridePendingTransition(activityOpenExitAnimation, activityCloseExitAnimation);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("SelectCityWindow", "onDestory called.");
    }

}
