package com.myp.water;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.myp.water.tools.AgentApplication;
import com.myp.water.wefika.calendar.CollapseCalendarView;
import com.myp.water.wefika.calendar.manager.CalendarManager;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;

public class TrendActivity extends AppCompatActivity {
    private long exitTime = 0;
    private Button btn_myElectricity;
    private Button btn_mylv;
    private Button btn_myturbidity;
    private Button btn_myacid;
    private Button btn_mytemperature;
    private Button btn_myoxygen;
    private Button btn_currentSelected;
    private String curPara;
    private boolean isRun=false;
    private DatagramSocket socket;
    private InetAddress remoteIP;
    public static Object lock = new Object();
    CollapseCalendarView calendarView;
    public static Queue<String> queue=new LinkedList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_trend);

        CalendarManager manager = new CalendarManager(LocalDate.now(), CalendarManager.State.MONTH, LocalDate.now().minusYears(2), LocalDate.now().plusYears(3));
        calendarView  = (CollapseCalendarView) findViewById(R.id.calendar);
        calendarView.init(manager);

        btn_myElectricity=(Button) this.findViewById(R.id.myElectricity);
        btn_mylv=(Button) this.findViewById(R.id.mylv);
        btn_myturbidity=(Button) this.findViewById(R.id.myturbidity);
        btn_myacid=(Button) this.findViewById(R.id.myacid);
        btn_mytemperature=(Button) this.findViewById(R.id.mytemperature);
        btn_myoxygen=(Button) this.findViewById(R.id.myoxygen);
        btn_myElectricity.setSelected(true);
        btn_currentSelected=btn_myElectricity;
        curPara="electricity";

        AgentApplication.getInstance().addActivity(this);

        new Thread(networkTask).start();
        isRun=true;
     //   CheckNetStatus();
    }
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
                /*
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
                }*/
                synchronized(lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(isRun== false){
                        break;
                    }
                }
                while(!queue.isEmpty())
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
    public static void pollOnce(){
        synchronized(lock){
            lock.notify();
        }
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
    public void sendPlay(View view) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String dateSelect=format1.format(calendarView.getSelectedDate().toDate());
        Log.e("selectDate", curPara + "++++++" + dateSelect);
        String sendStr="trend:"+curPara+":"+dateSelect;
        queue.add(sendStr);
        pollOnce();
    }

    public void selectElecttricty(View view) {
        btn_currentSelected.setSelected(false);
        btn_myElectricity.setSelected(true);
        btn_currentSelected= btn_myElectricity;
        curPara="electricity";
    }
    public void selectLv(View view) {
        btn_currentSelected.setSelected(false);
        btn_mylv.setSelected(true);
        btn_currentSelected= btn_mylv;
        curPara="lv";
    }
    public void selectTurbidity(View view) {
        btn_currentSelected.setSelected(false);
        btn_myturbidity.setSelected(true);
        btn_currentSelected=btn_myturbidity;
        curPara="turbidity";
    }
    public void selectAcid(View view) {
        btn_currentSelected.setSelected(false);
        btn_myacid.setSelected(true);
        btn_currentSelected= btn_myacid;
        curPara="acid";
    }
    public void selectTemperature(View view) {
        btn_currentSelected.setSelected(false);
        btn_mytemperature.setSelected(true);
        btn_currentSelected= btn_mytemperature;
        curPara="temperature";
    }
    public void selectOxygen(View view) {
        btn_currentSelected.setSelected(false);
        btn_myoxygen.setSelected(true);
        btn_currentSelected= btn_myoxygen;
        curPara="oxygen";
    }
    public void comeback(View view) {
        finish();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun=false;
        Log.e("onDestroy", "onDestory called.");

    }

}
