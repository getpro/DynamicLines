package com.ruishang.sufaceviewline.wiget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by Jone on 2016/2/16.
 */
public class SufaceViewLine extends SurfaceView implements SurfaceHolder.Callback, Runnable{
    private Context mContext;
    private int width;
    private int height;
    private int l;
    private int n;
    private int p=60;
    private int o=5;

    public SufaceViewLine(Context c) {
        super(c);
        init(c);
    }

    public void init(Context c){
        this.mContext=c;
        this.surfaceHolder = this.getHolder();
        this.surfaceHolder.addCallback(this);
        this.obj = new GameObject();
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        this.width = wm.getDefaultDisplay().getWidth();
        this.height = wm.getDefaultDisplay().getHeight();
        jisuan(100);
        this.l = (this.width-2*n)/3;
    }

    public SufaceViewLine(Context c, AttributeSet attrs) {
        super(c, attrs);
        init(c);
    }

    public SufaceViewLine(Context c, AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);
        init(c);
    }

    /*
    * 这个类用来当测试的物件，会沿着方形路线持续移动
    */
    class GameObject {
        private float x;
        private float y;
        private Paint paint;
        private boolean redraw=false;
        private float pts[]={
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0
        };

        public GameObject() {
            this.x = 0;
            this.y = 0;


            this.paint = new Paint();
            this.paint.setColor(colors[0]);
            this.paint.setStrokeWidth(10);
            paint.setAntiAlias(true);
        }

        // 在SurfaceView加锁同步后传给自己的Canvas上绘制自己
        public void drawSelf(Canvas canvas) {
            //canvas.drawLine(x, y, x2, y2,paint);
            canvas.drawLines(pts,paint);
        }

        // 获取物件下一次要绘制的位置(这里是沿着一个边长为400的正方形不断运动的)
        public void getNextPos() {
            if(y==0&&x<l){
                x+=o;
                pts[2]=x;
            }else if(y<p&&x<l+n){
                x+=o;
                y+=o;
                if(pts[4]==0){
                    pts[4]=x-o;
                    pts[5]=y-o;
                }
                pts[6]=x;
                pts[7]=y;
            }else if(y==p&&x<2*l+n){
                x+=o;
                if(pts[8]==0){
                    pts[8]=x-o;
                    pts[9]=y;
                }
                pts[10]=x;
                pts[11]=y;
            }else if(y>0&&x<2*l+2*n){
                x+=o;
                y-=o;
                if(pts[12]==0){
                    pts[12]=x-o;
                    pts[13]=y+o;
                }
                pts[14]=x;
                pts[15]=y;
            }else if(y==0&&x<width){
                x+=o;
                if(pts[18]==0){
                    pts[17]=y;
                    pts[16]=x-o;
                }
                pts[18]=x;
                pts[19]=y;
            }else if(x>width){
                x=0;
                pts=new float[]{
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0
                };
                i++;
                if(i>=colors.length){
                    i=0;
                }
                this.paint.setColor(colors[i]);
            }

        }

        public boolean getRedraw(){
            return redraw;
        }
    }

    int i=0;
    private int colors[] = {Color.WHITE,Color.BLUE,Color.YELLOW,Color.RED};
    private Thread thread; // SurfaceView通常需要自己单独的线程来播放动画
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private GameObject obj;



    @Override
    public void run() {
        while (true) {
            obj.getNextPos();
            canvas = this.surfaceHolder.lockCanvas(); // 通过lockCanvas加锁并得到該SurfaceView的画布
            if(obj.getRedraw()){
                //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            canvas.drawColor(Color.BLACK);
            obj.drawSelf(canvas); // 把SurfaceView的画布传给物件，物件会用这个画布将自己绘制到上面的某个位置
            this.surfaceHolder.unlockCanvasAndPost(canvas); // 释放锁并提交画布进行重绘
            try {
                Thread.sleep(1); // 这个就相当于帧频了，数值越小画面就越流畅
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Toast.makeText(mContext, "SurfaceView已经销毁", Toast.LENGTH_LONG).show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Toast.makeText(mContext, "SurfaceView已经创建n:"+n+"L:"+l, Toast.LENGTH_LONG).show();
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // 这里是SurfaceView发生变化的时候触发的部分
    }


    //计算间距
    public void jisuan(int i){
        if(((width-2*i)/3+i)%5==0){
            n=i;
            o=5;
        }else{
            if(i>=30)
                jisuan(i-1);
        }
    }

}
