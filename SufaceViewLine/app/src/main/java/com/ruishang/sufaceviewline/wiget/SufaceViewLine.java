package com.ruishang.sufaceviewline.wiget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.ruishang.sufaceviewline.R;

/**
 * 动态线条
 * Created by Jone on 2016/2/16.
 */
public class SufaceViewLine extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int UP = 0; //初始位置在上方
    private static final int DONW = 1; //初始位置在下方
    private static final int ACT = 0; //循环
    private static final int ONCE = 1; //一次
    private static final String TAG = SufaceViewLine.class.getSimpleName();

    private Context mContext;//上下文

    private int width; //屏幕宽度
    private int height; //屏幕高度
    private int l; //分段线长度
    private int p = 35;//Y轴起伏高度
    private int speed = 3;//步进速度
    private int state = ACT; //线条是否动态
    private int direction = UP; //线条初始方向
    private boolean stop = false;//停止线程标志
    private int defaultColor = Color.GREEN;


    public SufaceViewLine(Context c) {
        super(c);
        init(c);
    }

    public void init(Context c) {
        this.mContext = c;
        this.surfaceHolder = this.getHolder();
        this.surfaceHolder.addCallback(this);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.obj = new GameObject();
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        this.width = wm.getDefaultDisplay().getWidth();
        this.height = wm.getDefaultDisplay().getHeight();
        l=(width - 2 * p) / 3;
    }

    public SufaceViewLine(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public SufaceViewLine(Context c, AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);
        TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.SufaceViewLine);
        this.state = ta.getInt(R.styleable.SufaceViewLine_lineType, ACT);
        this.defaultColor = ta.getColor(R.styleable.SufaceViewLine_lineColor, Color.WHITE);
        ta.recycle();
        init(c);
    }

    /*
    * 这个类用来当测试的物件，会沿着方形路线持续移动
    */
    class GameObject {
        private float x;
        private float y;
        private float y2;
        private Paint paint;
        private boolean redraw = false;
        private float pts[] = {
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
        };

        public GameObject() {
            this.x = 0;
            this.y = 5;
            this.y2 = p;
            if (direction == DONW) {
                y = p;
            }
            this.paint = new Paint();
            this.paint.setColor(defaultColor);
            this.paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }

        // 在SurfaceView加锁同步后传给自己的Canvas上绘制自己
        public void drawSelf(Canvas canvas) {
            //canvas.drawLine(x, y, x2, y2,paint);
            canvas.drawLines(pts, paint);
        }

        /**
         * 获取物件下一次要绘制的位置
         */
        public void getNextPosUP() {
            if (y == 5 && x < l) {
                x += speed;
                if (pts[1] == 0) {
                    pts[0] = x - speed;
                    pts[1] = y;
                    pts[20] = x - speed;
                    pts[21] = y2;
                }
                pts[2] = x;
                pts[3] = y;
                pts[22] = x;
                pts[23] = y2;

            } else if (y < p && x < l + p-5) {
                x += speed;
                y += speed;
                y2 -= speed;
                if (pts[4] == 0) {
                    pts[4] = x - speed;
                    pts[5] = y - speed;
                    pts[24] = x - speed;
                    pts[25] = y2 + speed;
                }
                pts[6] = x;
                pts[7] = y;
                pts[26] = x;
                pts[27] = y2;
            } else if (y == p && x < 2 * l + p-5) {
                x += speed;
                if (pts[8] == 0) {
                    pts[8] = x - speed;
                    pts[9] = y;
                    pts[28] = x - speed;
                    pts[29] = y2;
                }
                pts[10] = x;
                pts[11] = y;
                pts[30] = x;
                pts[31] = y2;
            } else if (y > 5 && x < 2 * l + 2 * p-10) {
                x += speed;
                y -= speed;
                y2 += speed;
                if (pts[12] == 0) {
                    pts[12] = x - speed;
                    pts[13] = y + speed;
                    pts[32] = x - speed;
                    pts[33] = y2 - speed;
                }
                pts[14] = x;
                pts[15] = y;
                pts[34] = x;
                pts[35] = y2;
            } else if (y == 5 && x < width) {
                x += speed;
                if (pts[18] == 0) {
                    pts[17] = y;
                    pts[16] = x - speed;
                    pts[37] = y2;
                    pts[36] = x - speed;
                }
                pts[18] = x;
                pts[19] = y;
                pts[38] = x;
                pts[39] = y2;
            } else if (x >= width) {
                if (state == ONCE) {
                    stop = true;
                }
                x = 0;
                y = 5;
                y2 = p;
                pts = null;
                pts = new float[]{
                        0, y, 0, y,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, p, 0, p,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                };
                i++;
                if (i >= colors.length) {
                    i = 0;
                }
                // this.paint.setColor(colors[i]);
            }

        }

        public float getX() {
            return x;
        }
    }

    int i = 0;
    private int colors[] = {Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED};
    private Thread thread; // SurfaceView通常需要自己单独的线程来播放动画
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private GameObject obj;


    @Override
    public void run() {
        while (true) {
            obj.getNextPosUP();
            if (stop) {
                return;
            }
            canvas = this.surfaceHolder.lockCanvas(); // 通过lockCanvas加锁并得到該SurfaceView的画布
            if (state == ACT)
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            obj.drawSelf(canvas); // 把SurfaceView的画布传给物件，物件会用这个画布将自己绘制到上面的某个位置
            this.surfaceHolder.unlockCanvasAndPost(canvas); // 释放锁并提交画布进行重绘
            if (obj.getX() == width) {
                //return;
            }
            try {
                Thread.sleep(8); // 这个就相当于帧频了，数值越小画面就越流畅
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // Toast.makeText(mContext, "SurfaceView已经销毁", Toast.LENGTH_LONG).show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // Toast.makeText(mContext, "SurfaceView已经创建n:"+n+"L:"+l, Toast.LENGTH_LONG).show();
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // 这里是SurfaceView发生变化的时候触发的部分
    }

}
