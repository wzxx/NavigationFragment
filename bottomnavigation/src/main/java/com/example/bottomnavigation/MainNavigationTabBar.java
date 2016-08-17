package com.example.bottomnavigation;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzxx  on 16/8/9.
 */
public class MainNavigationTabBar extends LinearLayout implements View.OnClickListener{

    private static final String KEY_CURRENT_TAG="";

    private List<ViewHolder> mViewHolderList;//list动态数组
    private OnTabSelectedListener mTabSelectListener;
    private FragmentActivity mFragmentActivity;
    private String mCurrentTag;//当前标签
    private String mRestoreTag;//存储的标签


    private int mMainContentLayoutId;//主内容显示区域View的id
    private ColorStateList mSelectedTextColor;//选中的Tab文字颜色
    private ColorStateList mNormalTextColor;//正常的Tab文字颜色
    private float mTabTextSize;//Tab文字的大小
    private int mDefaultSelectedTab = 0;//默认选中的tab index
    private int mCurrentSelectedTab;//当前选中的tab

    /**
     * 构造函数
     */
    public MainNavigationTabBar(Context context){
        this(context,null);
    }
    public MainNavigationTabBar(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public MainNavigationTabBar(Context context, AttributeSet attrs, int defStyleAttr){
        //AttributeSet 是接收xml中定义的属性信息，包括自己定义的参数。存在
        //defStyleAttr是一个reference, 它指向当前Theme中的一个style, style其实就是各种属性的集合。存在
        super(context,attrs,defStyleAttr);

        //根据这个TypeArray来设置组件的属性
        TypedArray typedArray=context.getTheme().obtainStyledAttributes(attrs,R.styleable.MainNavigateTabBar,0,0);

        /**
         * android里实现“不同按钮状态采用不同颜色显示文字”的功能实现起来挺方便的,就是用ColorStateList
         *
         * 实现步骤：
         * 1.添加一个ColorStateList资源xml文件——attr.xml
         * 2.java代码实现
         */
        ColorStateList tabTextColor=typedArray.getColorStateList(R.styleable.MainNavigateTabBar_navigateTabTextColor);//默认是黑色：#ff333333
        ColorStateList selectedTabTextColor=typedArray.getColorStateList(R.styleable.MainNavigateTabBar_navigateTabSelectedTextColor);//默认还是黑色：#ff333333

        //获取指定资源id对应尺寸。返回像素数值，把结果转换为int，小树部分四舍五入.这里是初始化为0.
        mTabTextSize=typedArray.getDimensionPixelSize(R.styleable.MainNavigateTabBar_navigateTabTextSize,0);
        //如果没有获取到MainNavigateTabBar_containerId这个属性，则获取到的
        // 值是后面设置的0，否则这个0无意义
        mMainContentLayoutId=typedArray.getResourceId(R.styleable.MainNavigateTabBar_containerId,0);//获取到了main_container这个ID

        //如果导航栏文字颜色存在则设置为这个颜色，若不存在则设置为默认颜色＃212121。发现是有的，就是#ff333333。不过初始化的时候这两个颜色肉眼看上去其实没什么区别
        mNormalTextColor=(tabTextColor!=null?tabTextColor:context.getResources().getColorStateList(R.color.tab_text_normal));

        if (selectedTabTextColor!=null){//如果导航栏的被选中的tab的文字的颜色存在，直接设置为该颜色。其实就是#ff333333
            mSelectedTextColor=selectedTabTextColor;
        }else {//否则
            Utils.checkAppCompatTheme(context);
            TypedValue typedValue=new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimary,typedValue,true);
            mSelectedTextColor=context.getResources().getColorStateList(typedValue.resourceId);
        }

        mViewHolderList=new ArrayList<>();

    }


    public void addTab(Class frameLayoutClass,TabParam tabParam){
        int defaultLayout=R.layout.comui_tab_view;//默认的这个item的布局,默认字体颜色是黑色

        /**
         * 判断用户是否输入这个title。
         * 虽然说equals方法也可以用来判断输入的是否为空，不过这二者是有区别的。
         *
         * 区别：
         * 1.返回的变量本身是null值的话，用它的equals方法是要报错的；
         * 2.如果调用TextUtils.isEmpty方法把这个变量作为参数传进去，只要这个参数为空活着为""，都会返回真。
         */
        if (TextUtils.isEmpty(tabParam.title)){
            //如果不存在title值，就是没有用户没有输入这个title呐，而是用了另外一个方法（第三个参数用的标题字符串的资源
            tabParam.title=getContext().getString(tabParam.titleStringRes);
        }

        //从context中获得一个布局填充器，然后利用这个填充器把xml布局文件转换为view对象
        View view= LayoutInflater.from(getContext()).inflate(defaultLayout,null);
        view.setFocusable(true);//使能获得焦点

        ViewHolder holder=new ViewHolder();//为了性能优化呗

        holder.tabIndex=mViewHolderList.size();//确定包含的viweholder数量
        holder.fragmentClass=frameLayoutClass;//设置为调用者输入的fragment
        holder.tag=tabParam.title;//tag即输入的title，比如"Recents"，"Nearby"那些
        holder.pageParam=tabParam;//pageParam则设为调用着输入的参数
        holder.tabIcon=(ImageView)view.findViewById(R.id.tab_icon);
        holder.tabTitle=(TextView)view.findViewById(R.id.tab_title);

        if (TextUtils.isEmpty(tabParam.title)){//如果不存在title，就设置这个tab文字不可见
            holder.tabTitle.setVisibility(View.INVISIBLE);
        }else {//否则就设置它的显示的text为title
            holder.tabTitle.setText(tabParam.title);
        }

        if (mTabTextSize!=0){//如果存在tab文字大小
            holder.tabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTabTextSize);//Pixels
        }
        if (mNormalTextColor!=null){//如果存在文字颜色
            holder.tabTitle.setTextColor(mNormalTextColor);
        }

        if (tabParam.backgroundColor>0){//默认是白色的呀
            view.setBackgroundResource(tabParam.backgroundColor);//直接设置背景颜色
        }

        if(tabParam.iconResId>0){//如果存在这个id就直接设置
            holder.tabIcon.setImageResource(tabParam.iconResId);
        }else {//如果不存在就设置图标为不可见
            holder.tabIcon.setVisibility(View.INVISIBLE);
        }

        if (tabParam.iconResId>0&&tabParam.iconSelectedResId>0){//如果不仅普通icon存在id值并且选中的icon也存在资源id值
            view.setTag(holder);//给view添加一个额外的holder，以后可以用gettag提取出来，性能优化。
            view.setOnClickListener(this);
            mViewHolderList.add(holder);
        }

        //动态给这个布局添加view
        addView(view,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1.0F));
    }


    /**
     * 类ViewHolder。性能优化
     * 存储数据
     */
    private static class ViewHolder{
        public String tag;//标签
        public TabParam pageParam;//tab的一些属性参数
        public ImageView tabIcon;//tab的图标
        public TextView tabTitle;//tab的标题
        public Class fragmentClass;
        public int tabIndex;//tab的索引
    }

    /**
     * 类TabParam
     */
    public static class TabParam{
        public int backgroundColor=android.R.color.white;//背景颜色默认是白色
        public int iconResId;//图标资源的ID
        public int iconSelectedResId;//被选择的图标的资源的id
        public int titleStringRes;//标题的资源
        public String title;//标题

        /**
         * 构造函数
         * @param iconResId 图标资源id
         * @param iconSelectedResId 被选中的图标资源id
         * @param title 标题
         */
        public TabParam(int iconResId,int iconSelectedResId,String title){
            this.iconResId=iconResId;
            this.iconSelectedResId=iconSelectedResId;
            this.title=title;
        }

        //第三个参数更改
        public TabParam(int iconResId, int iconSelectedResId, int titleStringRes) {
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.titleStringRes = titleStringRes;
        }

        //增加一个参数
        public TabParam(int backgroundColor, int iconResId, int iconSelectedResId, int titleStringRes) {
            this.backgroundColor = backgroundColor;
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.titleStringRes = titleStringRes;
        }

        public TabParam(int backgroundColor, int iconResId, int iconSelectedResId, String title) {
            this.backgroundColor = backgroundColor;
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.title = title;
        }
    }

    /**
     * 接口
     */
    public  interface OnTabSelectedListener{
        void onTabSelected(ViewHolder holder);
    }

    /*******************************************重写override********************************************/

    /**
     * 当View附加到窗体时，也就是View和Window绑定时就会调用这个函数，此时将会有一个Surface进行绘图之类的逻辑
     *
     * 生命周期为onCreate->onStart->onResume->onAttachedToWindow
     */
    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();

        if (mMainContentLayoutId==0){//就是那个main_container
            throw new RuntimeException("mFrameLayoutId不可以为0呀。");
        }
        if (mViewHolderList.size() == 0) {
            throw new RuntimeException("mViewHolderList的大小不可以为0呀，请调用addTab。");
        }
        Context context;
        if (!((context=getContext()) instanceof FragmentActivity)){//appcompatActivity也可以哇
//            Log.e("错误",context.toString());
            throw new RuntimeException("父活动必须是FragmentActivity的一个实例。"+context.toString());
        }

        mFragmentActivity=(FragmentActivity)getContext();

        ViewHolder defaultHolder=null;//默认的viewholder

        hideAllFragment();

        if (!TextUtils.isEmpty(mRestoreTag)){//假如存在
            for (ViewHolder holder:mViewHolderList){//对于每个viewholder
                if(TextUtils.equals(mRestoreTag,holder.tag)){//检查下是否相等
                    defaultHolder=holder;
                    mRestoreTag=null;
                    break;
                }
            }
        }else {//假如不存在这个mRestoreTag
            defaultHolder=mViewHolderList.get(mDefaultSelectedTab);//默认的tag就更改为默认选中的item的tag。初始为0.
        }

        showFragment(defaultHolder);
    }

    /**
     * 每次点中一个item
     * @param v
     */
    @Override
    public void onClick(View v){

        Object object=v.getTag();//拿出viewholder
        if (object!=null&&object instanceof ViewHolder){//object当然是属于viewholder的
            ViewHolder holder=(ViewHolder)v.getTag();
            showFragment(holder);//然后就要显示fragment呐
            if (mTabSelectListener!=null){
                mTabSelectListener.onTabSelected(holder);
            }
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRestoreTag = savedInstanceState.getString(KEY_CURRENT_TAG);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_CURRENT_TAG, mCurrentTag);
    }

    /*****************************************内部调用方法**************************************************/

    /**
     * 隐藏所有的fragment
     */
    private void hideAllFragment(){
        if (mViewHolderList==null||mViewHolderList.size()==0){//当然如果没有holder就直接return啦
            return;
        }
        FragmentTransaction transaction=mFragmentActivity.getFragmentManager().beginTransaction();//要对fragment进行操作了

        for (ViewHolder holder:mViewHolderList){//对于每个viewholder
            Fragment fragment=mFragmentActivity.getFragmentManager().findFragmentByTag(holder.tag);//根据tag（即title）找到这个fragment
            if (fragment!=null&&!fragment.isHidden()){
                transaction.hide(fragment);//如果不存在fragment或者它藏起来了就hide呗
            }
        }
        transaction.commit();//提交更改
    }

    /**
     * 显示 holder 对应的 fragment
     *
     * @param holder
     */
    private void showFragment(ViewHolder holder){

        //getFragmentManager()调用获得fragment的实例。而FragmentTransaction对fragment进行添加、移除、替换以及执行其他动作
        FragmentTransaction transaction=mFragmentActivity.getFragmentManager().beginTransaction();
        if (isFragmentShown(transaction,holder.tag)){
            return;
        }
        setCurrSelectedTabByTag(holder.tag);

        Fragment fragment=mFragmentActivity.getFragmentManager().findFragmentByTag(holder.tag);
        if (fragment==null){
            fragment=getFragmentInstance(holder.tag);
            transaction.add(mMainContentLayoutId,fragment,holder.tag);
        }else {
            transaction.show(fragment);
        }
        transaction.commit();//提交fragment修改
        mCurrentSelectedTab=holder.tabIndex;
    }

    /**
     * 确定fragment是否显示
     *
     * @param transaction
     * @param newTag
     * @return
     */
    private boolean isFragmentShown(FragmentTransaction transaction,String newTag){

        if (TextUtils.equals(newTag,mCurrentTag)){//假如这个title就是当前的title
            return true;
        }

        if (TextUtils.isEmpty(mCurrentTag)){
            return false;
        }

        Fragment fragment=mFragmentActivity.getFragmentManager().findFragmentByTag(mCurrentTag);
        if (fragment!=null&&!fragment.isHidden()){
            transaction.hide(fragment);
        }
        return false;
    }

    /**
     * 设置当前选中tab的图片和文字颜色
     *
     * @param tag
     */
    private void setCurrSelectedTabByTag(String tag){

        if (TextUtils.equals(mCurrentTag,tag)){
            return;//如果选中的仍然是原来选中的就直接return
        }

        for (ViewHolder holder:mViewHolderList){//遍历所有viewholder
            //解释？？？？？？？？？？
            if (TextUtils.equals(mCurrentTag,holder.tag)){
                holder.tabIcon.setImageResource(holder.pageParam.iconResId);
                holder.tabTitle.setTextColor(mNormalTextColor);
            }else if (TextUtils.equals(tag,holder.tag)){
                holder.tabIcon.setImageResource(holder.pageParam.iconSelectedResId);
                holder.tabTitle.setTextColor(mSelectedTextColor);
            }
        }
        mCurrentTag=tag;
    }

    /**
     * 获取fragment例子
     * @param tag
     * @return
     */
    private Fragment getFragmentInstance(String tag){
        Fragment fragment=null;
        for (ViewHolder holder:mViewHolderList){
            if (TextUtils.equals(tag,holder.tag)){
                try {
                    fragment=(Fragment)Class.forName(holder.fragmentClass.getName()).newInstance();
                }catch (InstantiationException e){
                    e.printStackTrace();
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
                break;
            }
        }
        return fragment;
    }
    /******************************************************************************************************/

    /*************************************************外部可调用接口****************************************/

    public void setSelectedTextColor(ColorStateList selectedTextColor){
        mSelectedTextColor=selectedTextColor;
    }
    public void setSelectedTextColor(int color){
        mSelectedTextColor=ColorStateList.valueOf(color);
    }

    public void setTabTextColor(ColorStateList color) {
        mNormalTextColor = color;
    }

    public void setTabTextColor(int color) {
        mNormalTextColor = ColorStateList.valueOf(color);
    }

    public void setFrameLayoutId(int frameLayoutId) {
        mMainContentLayoutId = frameLayoutId;
    }

    public void setTabSelectListener(OnTabSelectedListener tabSelectListener) {
        mTabSelectListener = tabSelectListener;
    }

    public void setDefaultSelectedTab(int index) {
        if (index >= 0 && index < mViewHolderList.size()) {
            mDefaultSelectedTab = index;
        }
    }



    public void setCurrentSelectedTab(int index) {
        if (index >= 0 && index < mViewHolderList.size()) {
            ViewHolder holder = mViewHolderList.get(index);
            showFragment(holder);
        }
    }

    public int getCurrentSelectedTab(){
        return mCurrentSelectedTab;
    }

    public void setBackgroundColor(int backgroundColor,TabParam tabParam){
        tabParam.backgroundColor=backgroundColor;
    }
}
