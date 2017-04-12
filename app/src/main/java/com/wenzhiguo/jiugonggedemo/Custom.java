package com.wenzhiguo.jiugonggedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by dell on 2017/4/7.
 */

public class Custom extends View {

    private boolean hasInit = false;
    boolean isSelected;
    boolean moveOnPoint = true;
    private int height;
    private int width;
    private int mOffsetY;
    private int mOffsetX;
    private Point[][] paints;
    private Bitmap mNormal;
    private Bitmap selected_error;
    private Bitmap mPressed;
    private float r;
    private float eventY;
    private float eventX;
    private boolean isFinsh;
    private Paint mPaint1 = new Paint();
    private ArrayList<Point> mPaint = new ArrayList<>();
    private Point mLastPoint;
    private Point mMiddlePoint;
    private CustomOnClickListen setOnCustomSuccess;

    public Custom(Context context) {
        super(context);
    }

    public Custom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Custom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //默认会滑动到九宫格的点上
        isFinsh = false;
        moveOnPoint = true;
        eventX = event.getX();
        eventY = event.getY();
        Point paint = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //重置
                reset();
                //当用户按下的时候 判断是否在九宫格的原点上
                paint = checkPaint(eventX, eventY, r);
                if (paint != null) {
                    //用户按在了点上
                    isSelected = true;
                    paint.setState(Point.STATE_PRESSED);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //当用户手指移动的时候，一开始必须是按在点上
                if (isSelected) {
                    paint = checkPaint(eventX, eventY, r);
                    if (paint != null) {
                        if (!checkIsRepeated(paint)){
                            //判断当前点和上一个点的中心位置是否坐落在九宫格上边
                            mMiddlePoint = checkPaint((mLastPoint.x + paint.x) / 2, (mLastPoint.y + paint.y) / 2, r);
                            if (mMiddlePoint!=null){
                                mMiddlePoint.setState(Point.STATE_PRESSED);
                            }
                        }
                        paint.setState(Point.STATE_PRESSED);
                        moveOnPoint = true;
                    } else {
                        moveOnPoint = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isFinsh = true;
                isSelected = false;
                break;
        }
        if (!isFinsh && isSelected && paint != null) {
            if (mMiddlePoint!=null&&!checkIsRepeated(mMiddlePoint)){
                mPaint.add(mMiddlePoint);
            }
            //是否是重复点
            boolean b = checkIsRepeated(paint);
            if (b) {
                //不是连接点
                moveOnPoint = false;
            } else {
                mLastPoint = paint;
                //添加到集合
                mPaint.add(paint);
            }
        } else if (isFinsh) {
            if (mPaint != null) {
                if (mPaint.size() == 1) {
                    //重置
                    reset();
                } else if (mPaint.size() < 5) {
                    errorPaint();
                    setOnCustomSuccess.setOnCustomError();
                } else if (mPaint.size() > 4) {
                    StringBuilder password = new StringBuilder();
                    for (int i = 0; i < mPaint.size(); i++) {
                        int index = mPaint.get(i).getIndex();
                        password.append(index+"");
                    }
                    setOnCustomSuccess.setOnCustomSuccess(password.toString());
                }
            }
        }
        //刷新onDraw
        postInvalidate();
        //消费此事件
        return true;
    }

    /**
     * 大于1小于5的时候执行错误图片
     */
    private void errorPaint() {
        for (int i = 0; i < mPaint.size(); i++) {
            mPaint.get(i).setState(Point.STATE_ERROR);
        }
    }

    /**
     * 判断是否是重复点
     */
    private boolean checkIsRepeated(Point paint) {
        boolean contains = mPaint.contains(paint);
        return contains;
    }

    /**
     * 选中的点添加到集合修改为正常图片
     */
    private void reset() {
        //将所有的点修改成正常的点
        for (int i = 0; i < mPaint.size(); i++) {
            mPaint.get(i).setState(Point.STATE_NORMAL);
        }
        //清空集合
        mPaint.clear();
        //将之前定好的中心点赋值为空
        mMiddlePoint = null;
    }

    /**
     * 计算九宫格的按下或移动的位置是否在九宫格上
     */
    private Point checkPaint(float eventX, float eventY, float r) {
        for (int i = 0; i < paints.length; i++) {
            for (int j = 0; j < paints[i].length; j++) {
                Point paint = paints[i][j];
                //判断一下
                double distance = getDistance(paint.getX(), paint.getY(), eventX, eventY);
                if (distance < r) {
                    return paint;
                }
            }
        }
        return null;
    }

    /**
     * 计算出斜边长度
     */
    private double getDistance(float x, float y, float eventX, float eventY) {
        float v = (x - eventX) * (x - eventX) + (y - eventY) * (y - eventY);
        return Math.sqrt(v);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!hasInit) {
            initPaints();
            //初始化画笔
            initPaint();
            hasInit = true;
        }
        //画图片
        initPicture(canvas);
        //画线
        initLines(canvas);
    }

    /**
     * 画笔
     */
    private void initPaint() {
        mPaint1.setColor(Color.BLACK);
        mPaint1.setStrokeWidth(10);
    }

    /**
     * 画线
     */
    private void initLines(Canvas canvas) {
        if (mPaint.size() > 0) {
            Point a = mPaint.get(0);
            for (int i = 1; i < mPaint.size(); i++) {
                //获取第二个点
                Point b = mPaint.get(i);
                //画线
                canvas.drawLine(a.x, a.y, b.x, b.y, mPaint1);
                //必须赋值给a点,不重新复制连接第三点的时候还是重第一点开始的
                a = b;
            }
            if (!moveOnPoint) {
                //单纯画线
                canvas.drawLine(a.x, a.y, eventX, eventY, mPaint1);
            }
        }
    }

    /**
     * 画初始图片
     */
    private void initPicture(Canvas canvas) {
        for (int i = 0; i < paints.length; i++) {
            for (int j = 0; j < paints[i].length; j++) {
                Point paint = paints[i][j];
                //默认图片
                if (paint.getState() == paint.STATE_NORMAL) {
                    //获取当前左标点，减去图片的半径  得到left跟right值
                    canvas.drawBitmap(mNormal, paints[i][j].x - r, paints[i][j].y - r, null);
                } else if (paint.getState() == paint.STATE_PRESSED) {
                    //按下图片
                    canvas.drawBitmap(mPressed, paints[i][j].x - r, paints[i][j].y - r, null);
                } else if (paint.getState() == paint.STATE_ERROR) {
                    //错误图片
                    canvas.drawBitmap(selected_error, paints[i][j].x - r, paints[i][j].y - r, null);
                }
            }
        }
    }

    /**
     * 定义九个点的位置,计算出测量大小
     */
    private void initPaints() {
        //得到屏幕的宽和高
        height = getHeight();
        width = getWidth();
        mOffsetY = 0;
        mOffsetX = 0;
        if (height > width) {
            //竖屏
            mOffsetY = (height - width) / 2;
            height = width;
        } else if (width > height) {
            //横屏
            mOffsetX = (width - height) / 2;
              width=height;
        }
        //定义9个点的坐标
        int index = 1;
        paints = new Point[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                paints[i][j] = new Point(mOffsetX + width / 4 * (i + 1), mOffsetY + height / 4 * (j + 1));
                paints[i][j].setIndex(index);
            }
        }
        //实例化默认图片
        mNormal = BitmapFactory.decodeResource(getResources(), R.drawable.selected);
        //按下图片
        mPressed = BitmapFactory.decodeResource(getResources(), R.drawable.s);
        //错误图片
        selected_error = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        //图片的半径
        r = mNormal.getWidth() / 2;
    }

    /**
     * 创建接口
     */
    public interface CustomOnClickListen{
        public void setOnCustomSuccess(String password);
        public void setOnCustomError();
    }

    public void setOnClickCustomSuccess(CustomOnClickListen setOnCustomSuccess){
        this.setOnCustomSuccess=setOnCustomSuccess;
    }

}
