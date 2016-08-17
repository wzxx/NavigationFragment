package com.example.imagecut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by wzxx on 16/8/11.
 */
public class MyImageView extends ImageView{

    Matrix matrix=new Matrix();
    Matrix savedMatrix=new Matrix();

    private Bitmap bitmap=null;//位图对象
    private DisplayMetrics displayMetrics;//屏幕的分辨率

    float minScaleR=1.0f;//最小缩放比例
    static final float MAX_SCALE=15f;//最大缩放比例

    static final int NONE=0;//初始状态
    static final int DRAG=1;//拖动
    static final int ZOOM=2;//缩放

    int mode=NONE;//当前状态，初始为初始状态

    /**
     * 纪录并存储float类型的x、y值，就是按下的位置的坐标的x和y值
     */
    PointF prev=new PointF();
    PointF mid=new PointF();
    float dist=1f;

    /**
     * 构造函数
     */
    public MyImageView(Context context){
        super(context);
        setupView();
    }

    private void setupView(){
//        if(null==bitmap){
//
//        }else {
//            return;
//        }
        Context context=getContext();

        displayMetrics=context.getResources().getDisplayMetrics();//获取屏幕分辨率，需要根据分辨率来使用图片居中

        /**
         * 根据MyImageView来获取bitmap对象
         */
        BitmapDrawable bitmapDrawable=(BitmapDrawable)this.getDrawable();
        if (bitmapDrawable!=null){
            bitmap=bitmapDrawable.getBitmap();
        }

        this.setScaleType(ScaleType.MATRIX);//设置为矩阵形式。important!!!
        this.setImageBitmap(bitmap);

        if (bitmap!=null){//bitmap为空就不调用center函数
            center(true,true);
        }
        this.setImageMatrix(matrix);

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()&MotionEvent.ACTION_MASK){
                    //主点按下
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        prev.set(motionEvent.getX(),motionEvent.getY());//获取位置
                        mode=DRAG;
                        break;
                    //副点按下
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist=spacing(motionEvent);
                        if (spacing(motionEvent)>10f){// 如果连续两点距离大于10，判定为多点模式
                            savedMatrix.set(matrix);
                            midPoint(mid,motionEvent);//mid就是两点的中点
                            mode=ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:{
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP:
                        mode=NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode==DRAG){//假设是拖动的
                            matrix.set(savedMatrix);
                            matrix.postTranslate(motionEvent.getX()-prev.x,motionEvent.getY()-prev.y);
                        }else if (mode==ZOOM){//假设是缩放的
                            float newDist=spacing(motionEvent);
                            if (newDist>10f){
                                matrix.set(savedMatrix);
                                float tScale=newDist/dist;
                                matrix.postScale(tScale,tScale,mid.x,mid.y);
                            }
                        }
                        break;
                }
                MyImageView.this.setImageMatrix(matrix);
                CheckView();
                return true;
            }
        });
    }

    /**
     * 不管是横向还是纵向都居中呐
     * @param horizontal
     * @param vertical
     */
    protected void center(boolean horizontal,boolean vertical){
        Matrix m=new Matrix();
        m.set(matrix);
        RectF rectF=new RectF(0,0,bitmap.getWidth(),bitmap.getHeight());//新建一个矩形，宽度是图片的宽，高度是图片的高
        m.mapRect(rectF);

        float height=rectF.height();
        float width=rectF.width();

        float deltaX=0,deltaY=0;


        if (vertical){//如果纵向居中是true
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            int screenHeight=displayMetrics.heightPixels;
            if (height<screenHeight){//图片高度小于屏幕高度
                 deltaY=(screenHeight-height)/2-rectF.top;
            }else if (rectF.top>0){//大于0，改变的高度就是负值
                 deltaY=-rectF.top;
            }else if (rectF.bottom<screenHeight){
                deltaY=this.getHeight()-rectF.bottom;
            }
        }

        if (horizontal){//如果横向居中是true
            int screenWidth=displayMetrics.widthPixels;
            if (width<screenWidth){
                deltaX=(screenWidth-width)/2-rectF.left;
            }else if (rectF.left>0){
                deltaX=-rectF.left;
            }else if (rectF.right<screenWidth){
                deltaX=screenWidth-rectF.right;
            }
        }
        matrix.postTranslate(deltaX,deltaY);//在setScale方法后平移，两个参数是界面中心的坐标，在中心缩放呐
    }

    /**
     * 两点的距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event){
        float x=event.getX(0)-event.getX(1);
        float y=event.getY(0)-event.getY(1);
        return (float)Math.sqrt(x*x+y*y);
    }

    /**
     * 设置两点的中点
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point,MotionEvent event){
        float x=event.getX(0)+event.getX(1);
        float y=event.getY(0)+event.getY(1);
        point.set(x/2,y/2);
    }

    /**
     * 限制最大最小缩放比例，自动居中
     */
    private void CheckView(){

        float p[]=new float[9];
        matrix.getValues(p);
        if (mode==ZOOM){
            if (p[0]<minScaleR){
                Log.d(".MyImageView", "当前缩放级别:"+p[0]+",最小缩放级别:"+minScaleR);
                matrix.setScale(minScaleR,minScaleR);
            }
            if (p[0]>MAX_SCALE){
                Log.d(".MyImageView", "当前缩放级别:"+p[0]+",最大缩放级别:"+MAX_SCALE);
                matrix.set(savedMatrix);
            }
        }
        center(true,true);
    }

    /*******************************************重写***********************************************/
    @Override
    public void setImageBitmap(Bitmap drawable){
        super.setImageBitmap(drawable);
        setupView();
    }

    @Override
    public void setImageDrawable(Drawable drawable){
        super.setImageDrawable(drawable);
        setupView();
    }

    @Override
    public void setImageResource(int resId){
        setImageDrawable(getResources().getDrawable(resId));
    }
}
