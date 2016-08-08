package com.xifly.myandroidstudiodemo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SimpleHttpServer shs;

    //当前移动动画是否正在执行
    private boolean isAnimRun = false;
    //游戏是否开始
    private boolean isGameStart = false;
    //游戏主界面
    private GridLayout gl_main_game;
    //利用二维数组创建拼图块
    private ImageView[][] iv_game_arr = new ImageView[3][5];
    /**
     * 当前空空方块的实例的保存
     */
    private ImageView iv_null_ImageView;
    /**
     * 当前手势
     */
    private GestureDetector mDetector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                int type = getDirByGes(motionEvent.getX(), motionEvent.getY(), motionEvent1.getX(), motionEvent1.getY());
//                Toast.makeText(MainActivity.this,""+type,Toast.LENGTH_SHORT).show();
                changeByDir(type);
                return false;
            }
        });
        setContentView(R.layout.activity_main);
        /****************************************/
        MyAsyncTask task = new MyAsyncTask();
        task.execute();





        /****************************************/

        /**初始化游戏的若干个小方块*/
        //获取大图
        Bitmap bigBm = ((BitmapDrawable) getResources().getDrawable(R.mipmap.image)).getBitmap();
        int tuWAndH = bigBm.getWidth() / 5;
        //小方块的宽高为整个屏幕的宽/5
        int ivWAndH = getWindowManager().getDefaultDisplay().getWidth() / 5;
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                //根据行列切成若干个小块
                Bitmap bm = Bitmap.createBitmap(bigBm, j * tuWAndH, i * tuWAndH, tuWAndH, tuWAndH);
                iv_game_arr[i][j] = new ImageView(this);
                iv_game_arr[i][j].setImageBitmap(bm);//每个方块的图案
                iv_game_arr[i][j].setPadding(2, 2, 2, 2);
                iv_game_arr[i][j].setLayoutParams(new RelativeLayout.LayoutParams(ivWAndH, ivWAndH));
                iv_game_arr[i][j].setTag(new GameData(i, j, bm));//绑定自定义的数据
                iv_game_arr[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean flag = isHasByNullImageView((ImageView) view);
//                        Toast.makeText(MainActivity.this,"位置关系是否存在："+flag,Toast.LENGTH_SHORT).show();
                        if (flag) {
                            changeDataByImageView((ImageView) view);
                        }
                    }
                });

            }
        }

        /**初始哈游戏主界面*/
        gl_main_game = (GridLayout) findViewById(R.id.gl_main_game);
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                gl_main_game.addView(iv_game_arr[i][j]);
            }
        }


        WebConfiguration wc = new WebConfiguration();
        wc.setPort(8088);
        wc.setMaxParallels(50);
        shs = new SimpleHttpServer(wc);
        shs.startAsync();

        setNullImage(iv_game_arr[2][4]);
        /**初始化随机打乱顺序方法*/
        randomMove();
        isGameStart = true;//开始游戏
    }


    @Override
    protected void onDestroy() {
        shs.stopAsync();
        super.onDestroy();
    }

    /**
     * 根据手势方向，获取空方块相应相邻的位置，如果存在方块，那么进行方块数据交换
     *
     * @param type 1:上，2:下，3:左，4:右
     */
    public void changeByDir(int type) {
        changeByDir(type, true);
    }


    /**
     * 根据手势方向，获取空方块相应相邻的位置，如果存在方块，那么进行方块数据交换
     *
     * @param type        1:上，2:下，3:左，4:右
     * @param isAnimation true：有动画 false：没有动画
     */
    public void changeByDir(int type, boolean isAnimation) {

        //获取当前空方块的位置
        GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
        //根据方向，设置相应的相邻位置的坐标
        int new_x = mNullGameData.x;
        int new_y = mNullGameData.y;
        if (type == 1) {//要移动的空方块在当前空方块的下边
            new_x++;
        } else if (type == 2) {
            new_x--;
        } else if (type == 3) {
            new_y++;
        } else if (type == 4) {
            new_y--;
        }
        //判断这个新的坐标是否存在
        if (new_x >= 0 && new_x < iv_game_arr.length && new_y >= 0 && new_y < iv_game_arr[0].length) {
            //存在的话开始移动
            if (isAnimation) {
                changeDataByImageView(iv_game_arr[new_x][new_y]);
            } else {
                changeDataByImageView(iv_game_arr[new_x][new_y], isAnimation);
            }

        } else {
            //什么也不做
        }


    }


    /**
     * 判断游戏结束的方法
     */
    public void isGameOver() {
        boolean isGameOver = true;
        //要遍历每个游戏小方块
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                //为空的方块数据不判断跳过
                if (iv_game_arr[i][j] == iv_null_ImageView) {
                    continue;
                }
                GameData mGameData = (GameData) iv_game_arr[i][j].getTag();
                if (!mGameData.isTrue()) {
                    isGameOver = false;
                    break;
                }
            }
        }

        //根据一个开关变量决定游戏是否结束，结束时给出游戏结束的提示
        if (isGameOver) {
            Toast.makeText(MainActivity.this, "游戏结束", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 手势判断，是向右滑还是向左滑
     *
     * @param start_x 手势的起始点x
     * @param start_y 手势的起始点y
     * @param end_x   手势的终止点x
     * @param end_y   手势的终止点y
     * @return 1:上，2:下，3:左，4:右
     */
    public int getDirByGes(float start_x, float start_y, float end_x, float end_y) {

        boolean isLeftOrRight = (Math.abs(start_x - end_x) > Math.abs(start_y - end_y)) ? true : false;//是否是左右
        if (isLeftOrRight) {//左右
            boolean isLeft = start_x - end_x > 0 ? true : false;
            if (isLeft) {
                return 3;
            } else {
                return 4;
            }
        } else {//上下

            boolean isUp = start_y - end_y > 0 ? true : false;
            if (isUp) {
                return 1;
            } else {
                return 2;
            }
        }
    }


    //随机打乱拼图的顺序
    public void randomMove() {
        //打乱的次数
        for (int i = 0; i < 10; i++) {
            //根据手势开始交换，没有交换的动画效果
            int type = (int) (Math.random() * 4) + 1;
            changeByDir(type, false);
        }

    }


    /**
     * 利用动画结束之后，交换两个数据
     *
     * @param imageView 点击的方块
     */
    public void changeDataByImageView(final ImageView imageView) {
        changeDataByImageView(imageView, true);
    }


    /**
     * 利用动画结束之后，交换两个数据
     *
     * @param imageView   点击的方块
     * @param isAnimation true:有动画 false：没有动画
     */
    public void changeDataByImageView(final ImageView imageView, boolean isAnimation) {
        if (isAnimRun) {
            return;
        }
        if (!isAnimation) {
            GameData gameData = (GameData) imageView.getTag();
            iv_null_ImageView.setImageBitmap(gameData.bm);
            GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
            mNullGameData.bm = gameData.bm;
            mNullGameData.p_x = gameData.p_x;
            mNullGameData.p_y = gameData.p_y;
            setNullImage(imageView);//设置当前点击的方块为空方块
            if (isGameStart) {
                isGameOver();//成功时，会弹出一个Toast
            }
            return;
        }
        //创建一个动画，设置好方向，移动距离
        TranslateAnimation translateAnimation = null;
        if (imageView.getX() > iv_null_ImageView.getX()) {//当前点击的方块在空方块的下边
            //往上移动
            translateAnimation = new TranslateAnimation(0.1f, -imageView.getWidth(), 0.1f, 0.1f);
        } else if (imageView.getX() < iv_null_ImageView.getX()) {
            //往下移动
            translateAnimation = new TranslateAnimation(0.1f, imageView.getWidth(), 0.1f, 0.1f);
        } else if (imageView.getY() > iv_null_ImageView.getY()) {
            //往左移动
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, -imageView.getWidth());
        } else if (imageView.getY() < iv_null_ImageView.getY()) {
            //往右移动
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, imageView.getWidth());
        }
        //设置动的时长
        translateAnimation.setDuration(70);
        //设置动画结束之后是否停留
        translateAnimation.setFillAfter(true);
        //设置动画结束之后要真的交换数据
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimRun = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimRun = false;
                imageView.clearAnimation();
                GameData gameData = (GameData) imageView.getTag();
                iv_null_ImageView.setImageBitmap(gameData.bm);
                GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
                mNullGameData.bm = gameData.bm;
                mNullGameData.p_x = gameData.p_x;
                mNullGameData.p_y = gameData.p_y;
                setNullImage(imageView);//设置当前点击的方块为空方块
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //执行动画
        imageView.startAnimation(translateAnimation);

    }

    /**
     * 设置某个方块位空
     *
     * @param imageView 设置当前的图案为空
     */
    public void setNullImage(ImageView imageView) {
        imageView.setImageBitmap(null);
        iv_null_ImageView = imageView;
    }


    /**
     * 判断当前点击的方块，是否与空方块是相邻的关系
     *
     * @param imageView 所点击的方块
     * @return tru:相邻 false：不相邻
     */
    public boolean isHasByNullImageView(ImageView imageView) {
        //分别获取当前空方块的位置与点击方块的位置，通过 x y两边都差1的方式判断

        GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
        GameData mGameData = (GameData) imageView.getTag();

        if (mNullGameData.y == mGameData.y && mGameData.x + 1 == mNullGameData.x) {//当前点击的方块在空方块的上边
            return true;
        } else if (mNullGameData.y == mGameData.y && mGameData.x - 1 == mNullGameData.x) {//当前点击的方块在空方块的下边
            return true;
        } else if (mNullGameData.y == mGameData.y + 1 && mGameData.x == mNullGameData.x) {//当前点击的方块在空方块的左边
            return true;
        } else if (mNullGameData.y == mGameData.y - 1 && mGameData.x == mNullGameData.x) {//当前点击的方块在空方块的右边
            return true;
        }
        return false;
    }


    /**
     * 每个小方块上要绑定的护具
     */
    class GameData {
        /**
         * 每个小方块的实际位置x
         */
        public int x = 0;
        /**
         * 每个小方块的实际位置y
         */
        public int y = 0;
        /**
         * 每个小方块的图片
         */
        public Bitmap bm;
        /**
         * 每个小方块的图片位置
         */
        public int p_x = 0;
        /**
         * 每个小方块的图片位置
         */
        public int p_y = 0;

        public GameData(int x, int y, Bitmap bm) {
            this.x = x;
            this.y = y;
            this.bm = bm;
            this.p_x = x;
            this.p_y = y;
        }

        /**
         * 每个小方块得位置，是否正确
         *
         * @return true正确  false不正确
         */
        public boolean isTrue() {
            if (x == p_x && y == p_y) {
                return true;
            }
            return false;
        }
    }


}
