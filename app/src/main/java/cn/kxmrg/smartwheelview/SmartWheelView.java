package cn.kxmrg.smartwheelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LaMitad on 2016/11/11.
 * 滑轮选择器
 */

public class SmartWheelView extends ScrollView implements View.OnClickListener {

    public static final String TAG = SmartWheelView.class.getSimpleName();

    private Context context;
    private SmartWheelView smartWheelView;

    private LinearLayout rootLayout;
    //默认常量
    public static final int OFFSET_DEFAULT = 1;//默认的上下偏移量
    public static final int CHECK_TIME = 50;//检测时间
    public static final int DEFAULT_TEXT_SIZE = 17;//默认的字体大小
    public static final int DEFAULT_TEXT_PADDING = 15;//默认PADDING
    public static final int DEFAULT_TEXT_COLOR = Color.GRAY;
    public static final int SELECTED_TEXT_COLOR = Color.RED;
    public static final int LINE_COLOR = Color.RED;
    public static final int LINE_WIDTH = 1;

    //变量
    private int initialY;//原始的Y坐标
    private int itemHeight = 0;//每个Item的高度
    private int viewWidth = 0;//View的宽度
    private int selectedIndex = 1;//选中的Item
    private int offset = OFFSET_DEFAULT; // 偏移量（需要在最前面和最后面补全）
    private int displayItemCount;//显示Item的数量
    private boolean isBold = false;

    //样式
    private int text_szie;
    private int text_padding;
    private int line_width;
    private int text_color = DEFAULT_TEXT_COLOR;
    private int selected_text_color = SELECTED_TEXT_COLOR;
    private int line_color = LINE_COLOR;


    //初始化方法 代码创建View时调用
    public SmartWheelView(Context context) {
        super(context);
        init(context,null);
    }

    //初始化方法 XML创建且未指定Style时调用
    public SmartWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    //初始化方法 XML创建且指定Style时调用
    public SmartWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        this.smartWheelView = this;
        this.items = new ArrayList<>();
        //初始化样式
        if (attrs==null){
            text_szie = dp2px(DEFAULT_TEXT_SIZE);
            text_padding = dp2px(DEFAULT_TEXT_PADDING);
            line_width = dp2px(LINE_WIDTH);
        }else{
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartWheelView);
            isBold = typedArray.getBoolean(R.styleable.SmartWheelView_textBold, false);
            text_szie = typedArray.getDimensionPixelSize(R.styleable.SmartWheelView_textSize, dp2px(DEFAULT_TEXT_SIZE));
            text_padding = typedArray.getDimensionPixelSize(R.styleable.SmartWheelView_textPadding, dp2px(DEFAULT_TEXT_PADDING));
            line_width = typedArray.getDimensionPixelSize(R.styleable.SmartWheelView_lineWidth, dp2px(LINE_WIDTH));
            text_color = typedArray.getColor(R.styleable.SmartWheelView_textColor, DEFAULT_TEXT_COLOR);
            selected_text_color = typedArray.getColor(R.styleable.SmartWheelView_selectedTextColor, SELECTED_TEXT_COLOR);
            line_color = typedArray.getColor(R.styleable.SmartWheelView_lineColor, LINE_COLOR);
            offset = typedArray.getInteger(R.styleable.SmartWheelView_offset, OFFSET_DEFAULT);
            typedArray.recycle();
        }
        //隐藏滚动条
        this.setVerticalScrollBarEnabled(false);
        //去掉阴影
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        //添加线性布局
        rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        this.addView(rootLayout);
    }

    //用于在滑动后根据位置回弹
    private Runnable scrollerTask = new Runnable() {
        public void run() {
            //获取新的Y坐标
            int newY = getScrollY();
            if (initialY - newY == 0) { // 滑动停止了
                final int remainder = initialY % itemHeight;
                final int divided = initialY / itemHeight;
                Log.d(TAG, "initialY: " + initialY);
                Log.d(TAG, "remainder: " + remainder + ", divided: " + divided);
                if (remainder == 0) {
                    selectedIndex = divided + offset;
                    onSeletedCallBack();
                } else {
                    if (remainder > itemHeight / 2) {
                        smartWheelView.post(new Runnable() {
                            @Override
                            public void run() {
                                smartWheelView.smoothScrollTo(0, initialY - remainder + itemHeight);
                                selectedIndex = divided + offset + 1;
                                onSeletedCallBack();
                            }
                        });
                    } else {
                        smartWheelView.post(new Runnable() {
                            @Override
                            public void run() {
                                smartWheelView.smoothScrollTo(0, initialY - remainder);
                                selectedIndex = divided + offset;
                                onSeletedCallBack();
                            }
                        });
                    }
                }
            } else {//正在滑动
                initialY = getScrollY();
                smartWheelView.postDelayed(scrollerTask, CHECK_TIME);
            }
        }
    };

    public void startScrollerTask() {
        initialY = getScrollY();
        this.postDelayed(scrollerTask, CHECK_TIME);
    }

    //初始化数据部分
    private List<String> items;

    /**
     * 设置数据源
     *
     * @param list
     */
    public void setDatas(List<String> list) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.clear();
        items.addAll(list);
        // 根据前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        initData();
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;
        for (int i = 0; i < items.size(); i++) {
            rootLayout.addView(createView(i, items.get(i)));
        }
        refreshItemView(0);
    }

    //创建Item的方法
    private TextView createView(int position, String item) {
        TextView tv = new TextView(context);
        tv.setTag(position);
        tv.setOnClickListener(this);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setTextSize(px2sp(context,text_szie));
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(text_padding, text_padding, text_padding, text_padding);
        if (isBold) {
            Paint paint = tv.getPaint();
            paint.setFakeBoldText(true);
        }
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv);
            Log.d(TAG, "itemHeight: " + itemHeight);
            rootLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
        }
        return tv;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = viewWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = itemHeight * displayItemCount;
        }
        Log.d(TAG, "wh:" + width + "," + height);
        setMeasuredDimension(width, height);
    }

    //刷新ItemView状态
    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;
        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }
        int childSize = rootLayout.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) rootLayout.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                itemView.setTextColor(selected_text_color);
            } else {
                itemView.setTextColor(text_color);
            }
        }
    }

    //监听滑动更新Item状态
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
    }

    //获取选中区域的边界
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }

    private Paint paint;

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (viewWidth == 0) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            viewWidth = dm.widthPixels;
            Log.d(TAG, "viewWidth: " + viewWidth);
        }
        if (null == paint) {
            paint = new Paint();
            paint.setColor(line_color);
            paint.setStrokeWidth(line_width);
        }
        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[0], viewWidth * 5 / 6, obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[1], viewWidth * 5 / 6, obtainSelectedAreaBorder()[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }


        };
        super.setBackgroundDrawable(background);
    }

    //修改View宽度
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    //滑动松开后判断回弹位置
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 设置上下偏移量
     *
     * @param offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * 设置选中的item
     *
     * @param position
     */
    public void setSeletion(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(new Runnable() {
            @Override
            public void run() {
                SmartWheelView.this.smoothScrollTo(0, p * itemHeight);
            }
        });
    }

    /**
     * 返回选中的item内容
     *
     * @return
     */
    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    /**
     * 返回选中的item
     *
     * @return
     */
    public int getSeletedIndex() {
        return selectedIndex - offset;
    }

    public void setBold(boolean bold) {
        this.isBold = bold;
    }

    @Override
    public void onClick(View v) {
        setSeletion(((int) v.getTag() - offset));
        onSeletedCallBack();
    }

    //回调接口部分
    public interface OnWheelSelectedListener {
        void onSelected(int selectedIndex, String item);
    }

    private OnWheelSelectedListener onWheelSelectedListener;

    //设置回调监听
    public void setOnWheelSelectedListener(OnWheelSelectedListener onWheelSelectedListener) {
        this.onWheelSelectedListener = onWheelSelectedListener;
    }

    private void onSeletedCallBack() {
        if (null != onWheelSelectedListener) {
            onWheelSelectedListener.onSelected(selectedIndex, items.get(selectedIndex));
        }
    }

    //DP2PX
    private int dp2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //PX2SP
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    //用于测量View高度
    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    //修改样式:


    public void setLineWidth(int line_width) {
        this.line_width = line_width;
    }

    public void setLineColor(int line_color) {
        this.line_color = line_color;
    }

    public void setSelectedTextColor(int selected_text_color) {
        this.selected_text_color = selected_text_color;
    }

    public void setTextColor(int text_color) {
        this.text_color = text_color;
    }

    public void setTextPadding(int text_padding) {
        this.text_padding = text_padding;
    }

    public void setTextSzie(int text_szie) {
        this.text_szie = text_szie;
    }
}
