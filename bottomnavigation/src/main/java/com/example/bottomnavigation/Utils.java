package com.example.bottomnavigation;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by wzxx on 16/8/9.
 */
public class Utils {

    private static final int[] APPCOMPAT_CHECK_ATTRS ={android.R.attr.colorPrimary};//这颜色是#3F51B5

    /**
     * 检查这个app兼容的主题
     * @param context
     */
    public static void checkAppCompatTheme(Context context){
        //返回一个由AttributeSet获得的一系列基本的属性值
        //程序在运行时维护乐一个TypedArray的池，程序调用时，会向该池中请求一个实例，用完之后再调用recycle()方法释放这个实例从而使其可被其他模块复用。
        TypedArray array=context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed=!array.hasValue(0);//如果有一个属性在索引0，则failed为false，否则为true
        if (array!=null){//假设array存在
            array.recycle();//回收array，用于后续调用时可复用之。调用此方法后，不可再操作该变量
        }

        if (failed){//如果不存在一个属性在索引0，就是没有值啦
            throw new IllegalArgumentException("你需要利用设计库使用一个Theme.AppCompat的主题（或其后代）");
        }
    }
}
