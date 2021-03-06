package com.zys.pilu.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zys.pilu.R;
import com.zys.pilu.utils.LyricUtil;

import java.util.List;

/**
 * Created by zys on 2016/7/12.
 */
public class Lyric extends TextView {
    private final String TAG = "Lyric";
    private Paint paint;
    private LyricUtil lyricUtil = new LyricUtil();
    private String filePath = "";
    private int currentTime = 0;
    private List<String> lyricList;
    private List<Integer> timeList;
    int screenH;
    float density;
    private int dy = 100;
    private int textSize = 30;
    private Context context;
    private int id = -1;



    public Lyric(Context context) {
        this(context, null);
    }

    public Lyric(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Lyric(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void inti(String url, final String name, final String artist) {
        filePath = url.replace(".mp3", ".lrc");
        screenH = context.getResources().getDisplayMetrics().widthPixels;
        density = context.getResources().getDisplayMetrics().density;

        /*
         * When there is no lyric and download from the net
         * We need to reset the height of Lyric View.
         */
        lyricUtil.callback = new LyricUtil.OnDownLoadLyric() {
            @Override
            public void onDownLoadLyric() {
                lyricUtil.init(filePath, name, artist);
                lyricList = lyricUtil.getLyricList();
                Lyric.this.setHeight(screenH + lyricList.size() * dy);
            }
        };
        lyricUtil.init(filePath, name, artist);
        lyricList = lyricUtil.getLyricList();
        Lyric.this.setHeight(screenH + lyricList.size() * dy);
        Lyric.this.setHeight(screenH + lyricList.size() * dy);
        timeList = lyricUtil.getTimeList();

        id = -1;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.qianhui));
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);  //消除锯齿
        paint.setTextAlign(Paint.Align.CENTER);
        int y = 0;
        /*
         * If there is no lyric
         */
        if (lyricList.size() == 1 && timeList.size() == 1 && timeList.get(0) == 0) {
            paint.setColor(Color.GRAY);
            canvas.drawText(lyricList.get(0), getWidth()/2, y + screenH/2, paint);
        } else {
            for (int i = 0; i < lyricList.size(); i++) {
                if (id == i) {
                    paint.setColor(Color.WHITE);
                } else {
                    paint.setColor(Color.GRAY);
                }
                canvas.drawText(lyricList.get(i), getWidth()/2, y + screenH/2, paint);
                y += dy;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (lyricList != null)
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), screenH + lyricList.size() * dy);
        else
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), screenH);
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
        /*
         * Must looping to check with currentTime which lyric id is
         * Because user may drap the bar forward or aferward!
         */
        for (int i = 0; i < lyricList.size(); i++) {
            if (i == lyricList.size() - 1) {
                if (id != i &&  currentTime >= timeList.get(i)) {
                    id = i;
                    invalidate();
                }
            } else if (currentTime < timeList.get(i+1) && currentTime >= timeList.get(i) && id != i) {
                id = i;
                invalidate();
                break;
            }
        }
    }

    public int getId() {
        return id;
    }
    public int getDy() {
        return dy;
    }

}
