package com.myp.water;

/**
 * Created by myp on 2016/2/23.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


public class MySurfaceView extends SurfaceView implements Callback, Runnable {
    private SurfaceHolder sfh;
    private Paint paint;
    private Thread th;
    private boolean flag;
    private Canvas canvas;
    private int screenW, screenH;
    /*
    //定义两个圆形的中心点坐标与半径
    private float smallCenterX = 250, smallCenterY =1500, smallCenterR =80;
    private float BigCenterX = 250, BigCenterY =1500, BigCenterR = 200;*/

    private float smallCircleX = 250, smallCircleY =1400, smallCircleR =65;
    private float bigCircleX = 100, bigCircleY =1250, bigCircleR = 215;

    private float scalebarX=760,scalebarY=1250;
    private float scaleballX=780,scaleballY=1430,scaleballR=50,originalScaleY=1430;

    private float maxScaleY=1681,minScaleY=1250;

    private Bitmap bmpBg;//菜单背景
    private Bitmap bigCircle;
    private Bitmap smallCircle;

    private Bitmap scalebar;
    private Bitmap scaleball;
    /**
     * SurfaceView初始化函数
     */
    public MySurfaceView(Context context,AttributeSet attrs) {
        super(context,attrs);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setColor(Color.BLUE);

        paint.setAntiAlias(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        bmpBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bg);
        bigCircle=BitmapFactory.decodeResource(context.getResources(), R.mipmap.bigcircle);
        smallCircle=BitmapFactory.decodeResource(context.getResources(), R.mipmap.smallcircle);

        scalebar=BitmapFactory.decodeResource(context.getResources(), R.mipmap.scalebar);
        scaleball=BitmapFactory.decodeResource(context.getResources(), R.mipmap.scaleball);
    }

    /**
     * SurfaceView视图创建，响应此函数
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenW = this.getWidth();
        screenH = this.getHeight();
        flag = true;
        //实例线程
        th = new Thread(this);
        //启动线程
        th.start();
    }

    /**
     * 游戏绘图
     */
    public void myDraw() {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
              //   canvas.drawColor(Color.WHITE);
                 canvas.drawBitmap(bmpBg, 0, -Math.abs(getHeight() - bmpBg.getHeight()), paint);
/*
                //绘制大圆
            //    paint.setAlpha(0x77);
                canvas.drawCircle(BigCenterX, BigCenterY, BigCenterR, paint);
                //绘制小圆
                canvas.drawCircle(smallCenterX, smallCenterY, smallCenterR, paint);*/

                canvas.drawBitmap(bigCircle, bigCircleX, bigCircleY, paint);
                canvas.drawBitmap(smallCircle,smallCircleX,smallCircleY, paint);

                canvas.drawBitmap(scalebar, scalebarX,scalebarY, paint);
                canvas.drawBitmap(scaleball,scaleballX,scaleballY, paint);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 触屏事件监听
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //当用户手指抬起，应该恢复小圆到初始位置

        if (event.getAction() == MotionEvent.ACTION_UP) {
            smallCircleX = bigCircleX+bigCircleR-smallCircleR;
            smallCircleY=bigCircleY+bigCircleR-smallCircleR;

            scaleballY =originalScaleY;
          //  DistributionActivity.queue.add("stopmove");
        } else {
            int pointX = (int) event.getX();
            int pointY = (int) event.getY();
            if (Math.sqrt(Math.pow((scaleballX+scaleballR - (int) event.getX()), 2) + Math.pow((scaleballY+scaleballR - (int) event.getY()), 2)) <= scaleballR) {
                //让小圆跟随用户触点位置移动
             //   smallCircleX = pointX-smallCircleR;
                scaleballY = pointY-scaleballR;
                if(scaleballY>=maxScaleY)
                {
                    scaleballY=maxScaleY;
                }
                else if(scaleballY<=minScaleY)
                {
                    scaleballY=minScaleY;
                }
                if(scaleballY>originalScaleY+10)
                {
                    DistributionActivity.queue.add("larger");
                    DistributionActivity.pollOnce();
                }
                else if(scaleballY<originalScaleY-10)
                {
                    DistributionActivity.queue.add("smaller");
                    DistributionActivity.pollOnce();
                }
            } else {
              //  setSmallCircleXY(bigCircleX+bigCircleR, bigCircleY+bigCircleR, bigCircleR, getRad(bigCircleX+bigCircleR, bigCircleY+bigCircleR, pointX, pointY));
                scaleballY =originalScaleY;
            }
            if(Math.sqrt(Math.pow((smallCircleX+smallCircleR - (int) event.getX()), 2) + Math.pow((smallCircleY+smallCircleR - (int) event.getY()), 2)) <= smallCircleR||Math.sqrt(Math.pow((bigCircleX+bigCircleR - (int) event.getX()), 2) + Math.pow((bigCircleY+bigCircleR - (int) event.getY()), 2)) <= bigCircleR)
            {
                double currad=getRad(bigCircleX+bigCircleR, bigCircleY+bigCircleR, pointX, pointY);
                Log.e("myrad", "myrad" + currad);
                if(currad>-2.25&&currad<-0.5)
                {
                    DistributionActivity.queue.add("upper");
                    DistributionActivity.pollOnce();
                }
                else if(currad>0.5&&currad<2.75)
                {
                    DistributionActivity.queue.add("down");
                    DistributionActivity.pollOnce();
                }
                else if(currad>-0.5&&currad<0.5)
                {
                    DistributionActivity.queue.add("right");
                    DistributionActivity.pollOnce();
                }
                else
                {
                    DistributionActivity.queue.add("left");
                    DistributionActivity.pollOnce();
                }

                //判断用户点击的位置是否在大圆内
                if (Math.sqrt(Math.pow((bigCircleX+bigCircleR - (int) event.getX()), 2) + Math.pow((bigCircleY+bigCircleR - (int) event.getY()), 2)) <= bigCircleR) {
                    //让小圆跟随用户触点位置移动
                    smallCircleX = pointX-smallCircleR;
                    smallCircleY = pointY-smallCircleR;
                } else {
                    setSmallCircleXY(bigCircleX+bigCircleR, bigCircleY+bigCircleR, bigCircleR, getRad(bigCircleX+bigCircleR, bigCircleY+bigCircleR, pointX, pointY));
                }

            }

        }
        return true;
    }

    /**
     * 小圆针对于大圆做圆周运动时，设置小圆中心点的坐标位置
     * @param centerX
     *            围绕的圆形(大圆)中心点X坐标
     * @param centerY
     *            围绕的圆形(大圆)中心点Y坐标
     * @param R
     * 			     围绕的圆形(大圆)半径
     * @param rad
     *            旋转的弧度
     */
    public void setSmallCircleXY(float centerX, float centerY, float R, double rad) {
        //获取圆周运动的X坐标

        smallCircleX = (float) (R * Math.cos(rad)) + centerX-smallCircleR;
        //获取圆周运动的Y坐标
        smallCircleY = (float) (R * Math.sin(rad)) + centerY-smallCircleR;
    }

    /**
     * 按键事件监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 得到两点之间的弧度
     * @param px1    第一个点的X坐标
     * @param py1    第一个点的Y坐标
     * @param px2    第二个点的X坐标
     * @param py2    第二个点的Y坐标
     * @return
     */
    public double getRad(float px1, float py1, float px2, float py2) {
        //得到两点X的距离
        float x = px2 - px1;
        //得到两点Y的距离
        float y = py1 - py2;
        //算出斜边长
        float Hypotenuse = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        float cosAngle = x / Hypotenuse;
        //通过反余弦定理获取到其角度的弧度
        float rad = (float) Math.acos(cosAngle);
        //当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }

    /**
     * 游戏逻辑
     */
    private void logic() {
    }

    @Override
    public void run() {
        while (flag) {
            long start = System.currentTimeMillis();
            myDraw();
            logic();
            long end = System.currentTimeMillis();
            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * SurfaceView视图状态发生改变，响应此函数
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * SurfaceView视图消亡时，响应此函数
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }
}
