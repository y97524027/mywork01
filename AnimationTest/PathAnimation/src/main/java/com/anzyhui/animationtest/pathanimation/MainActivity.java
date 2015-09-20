package com.anzyhui.animationtest.pathanimation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener, Animator.AnimatorListener {

    private ImageView imageCenter;
    private ImageView imageA;
    private ImageView imageB;
    private ImageView imageC;
    private ImageView imageD;
    private ImageView imageE;
    private ImageView imageF;
    private boolean isOpened;

    //定义一个集合，存储菜单项
    private List<ImageView> itemImages;
    private Animator animator;
    private Animator animatorClick;
    private ImageView itemImageClick;
    private Animator animatorBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //中心点设置点击事件，触发动画
        imageCenter = (ImageView)findViewById(R.id.image_center);
        imageA = (ImageView)findViewById(R.id.image_a);
        imageB = (ImageView)findViewById(R.id.image_b);
        imageC = (ImageView)findViewById(R.id.image_c);
        imageD = (ImageView)findViewById(R.id.image_d);
        imageE = (ImageView)findViewById(R.id.image_e);
        imageF = (ImageView)findViewById(R.id.image_f);

        itemImages = new LinkedList<ImageView>();

        itemImages.add(imageA);
        itemImages.add(imageB);
        itemImages.add(imageC);
        itemImages.add(imageD);
        itemImages.add(imageE);
        itemImages.add(imageF);


        //得到imagecenter所在的x轴和y轴坐标
        //!!!!!!!!  !!!!!!   !!!!!!!!!! !
        //在onCreate()方法中获取不到控件的宽高等基本属性，
        //float x = imageCenter.getX();  getX  Y 到底啥意思？？
        //float y = imageCenter.getY();


        imageCenter.setOnClickListener(this);
    }


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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        //////////////////////////////////
        //设置菜单项可见
        for (ImageView itemImage : itemImages) {
            itemImage.setVisibility(View.VISIBLE);
        }

        //为菜单项设置点击事件
        ItemClickListener itemClickListener = new ItemClickListener();

        for (ImageView itemImage : itemImages) {
            itemImage.setOnClickListener(itemClickListener);
        }


        //如果是要打开菜单项
        if (!isOpened) {
            //imageCenter.setImageResource(R.drawable.icon0);

            /////////////打开菜单项的  ----菜单的滚动动画
            //加载属性动画对象
            animator = AnimatorInflater.loadAnimator(this, R.animator.animator_center);

            //为iamge设置动画
            animator.setTarget(imageCenter);
            //imageCenter.setScaleX();
            //imageCenter.setPivotX();

            //开始动画
            animator.start();

            //菜单往各自的方向滚过去

            Animator animatorA = AnimatorInflater.loadAnimator(this, R.animator.animator_a);
            animatorA.setTarget(imageA);
            animatorA.start();

            Animator animatorB = AnimatorInflater.loadAnimator(this, R.animator.animator_b);
            animatorB.setTarget(imageB);
            animatorB.start();

            Animator animatorC = AnimatorInflater.loadAnimator(this, R.animator.animator_c);
            animatorC.setTarget(imageC);
            animatorC.start();

            Animator animatorD = AnimatorInflater.loadAnimator(this, R.animator.animator_d);
            animatorD.setTarget(imageD);
            animatorD.start();

            Animator animatorE = AnimatorInflater.loadAnimator(this, R.animator.animator_e);
            animatorE.setTarget(imageE);
            animatorE.start();

            Animator animatorF = AnimatorInflater.loadAnimator(this, R.animator.animator_f);
            animatorF.setTarget(imageF);
            animatorF.start();


        } else  //要是是要关闭菜单项
        {
            //设置关闭菜单项动画  ----菜单的动画
            animator = AnimatorInflater.loadAnimator(this,R.animator.animator_center_back);
            animator.setTarget(imageCenter);
            animator.start();
            //imageCenter.setImageResource(R.drawable.icon);

            //菜单各自滚回去
            Animator animatorA = AnimatorInflater.loadAnimator(this, R.animator.animator_a_back);
            animatorA.setTarget(imageA);
            animatorA.start();

            Animator animatorB = AnimatorInflater.loadAnimator(this, R.animator.animator_b_back);
            animatorB.setTarget(imageB);
            animatorB.start();

            Animator animatorC = AnimatorInflater.loadAnimator(this, R.animator.animator_c_back);
            animatorC.setTarget(imageC);
            animatorC.start();

            Animator animatorD = AnimatorInflater.loadAnimator(this, R.animator.animator_d_back);
            animatorD.setTarget(imageD);
            animatorD.start();

            Animator animatorE = AnimatorInflater.loadAnimator(this, R.animator.animator_e_back);
            animatorE.setTarget(imageE);
            animatorE.start();

            Animator animatorF = AnimatorInflater.loadAnimator(this, R.animator.animator_f_back);
            animatorF.setTarget(imageF);
            animatorF.start();
        }

        //设置菜单的监听
        animator.addListener(this);

        //设置打开状态
        isOpened = !isOpened;

    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    /**
     * 监听动画执行完
     * @param animation 结束的那个动画对象
     */
    @Override
    public void onAnimationEnd(Animator animation) {

        //如果是主菜单动画执行完了
        if (animation == this.animator){
            //如果已经打开菜单项，则设置打开的图片

            if (!isOpened) {
                //如果菜单项已经关闭
                //imageCenter.setImageResource(R.drawable.icon);
                //滚回来后设为不可见
                for (ImageView itemImage : itemImages) {
                    itemImage.setVisibility(View.INVISIBLE);
                }
            }
        }
        //如果是菜单项被执行了 且执行完了
        else if(animation==animatorClick){

            //大小返回原来的状态  、位置返回原来的位置，并设置为隐藏
            for (ImageView itemImage : itemImages) {

                itemImage.setImageAlpha(0xFF);
                itemImage.setScaleX(1);
                itemImage.setScaleY(1);
                itemImage.setX(588);
                itemImage.setY(986);
                itemImage.setVisibility(View.INVISIBLE);

                //itemImage.setVisibility(View.VISIBLE);

            }

        }
//        else if(animation == animatorBack){
//            //菜单关闭
//            //isOpened = !isOpened;
//            //imageCenter.setImageResource(R.drawable.icon);
//        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    //菜单项点击事件
    class ItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v != null) {
                if(v instanceof ImageView){

                    //设置被点击的对象的效果
                    itemImageClick = (ImageView)v;

                    animatorClick = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.animator_item_click);
                    animatorClick.setTarget(itemImageClick);
                    animatorClick.start();
                    //设置点击的菜单项的监听
                    animatorClick.addListener(MainActivity.this);

                    //其他没被点击的设置缩小效果
                    for (ImageView image : itemImages) {
                        if(image!= itemImageClick) {
                            //这样加载到的是同一个对象？
                            Animator animatorUnclick = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.animator_item_unclick);
                            animatorUnclick.setTarget(image);
                            animatorUnclick.start();
                        }
                    }
                    //菜单设置回滚 关闭特效
                    animatorBack = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.animator_center_back);
                    animatorBack.setTarget(imageCenter);
                    animatorBack.start();

                    //关闭了
                    isOpened = !isOpened;
                    //animatorBack.addListener(MainActivity.this);


                }
            }

        }
    }
}
