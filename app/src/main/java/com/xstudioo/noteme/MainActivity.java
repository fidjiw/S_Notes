package com.xstudioo.noteme;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    Adapter adapter;


    TextView noItemText;
    SimpleDatabase simpleDatabase;
    private FloatingActionButton mFAB;
    private FloatingActionButton mFAB2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtils.setTransparent(this);
        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorTheme));

        mFAB = (FloatingActionButton) findViewById(R.id.menu_item);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNote.class);
                startActivity(intent);
            }
        });

        mFAB2 = (FloatingActionButton) findViewById(R.id.menu_item2);
        mFAB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, firebase_main.class);
                startActivity(intent);
            }
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        noItemText = findViewById(R.id.noItemText);
        simpleDatabase = new SimpleDatabase(this);
        List<Note> allNotes = simpleDatabase.getAllNotes();
        recyclerView = findViewById(R.id.allNotesList);

        if (allNotes.isEmpty()) {
            noItemText.setVisibility(View.VISIBLE);
        } else {
            noItemText.setVisibility(View.GONE);
            displayList(allNotes);
        }

    }

    private void displayList(List<Note> allNotes) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, allNotes);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.licenses) {
            Toast.makeText(this, "V1.0", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, licenses.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Note> getAllNotes = simpleDatabase.getAllNotes();
        if (getAllNotes.isEmpty()) {
            noItemText.setVisibility(View.VISIBLE);
        } else {
            noItemText.setVisibility(View.GONE);
            displayList(getAllNotes);
        }
    }


    public static class StatusBarUtils {
        /*获取状态栏高度 */
        public static int getHeight(Context context) {
            int statusBarHeight = 0;
            try {
                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                        "android");
                if (resourceId > 0) {
                    statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return statusBarHeight;
        }

        /*实现设置状态栏颜色的功能：*/
        public static void setColor(@NonNull Window window, @ColorInt int color) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.setStatusBarColor(color);
                //设置状态栏字体颜色
                setTextDark(window, !isDarkColor(color));
            }

        }

        /*修改状态栏颜色的功能其实就是对Window进行操作，而该Window可以是Activity或Dialog等持有的Window，所以我们就封装了一个传递Window的方法。*/

        public static void setColor(Context context, @ColorInt int color) {
            if (context instanceof Activity) {
                setColor(((Activity) context).getWindow(), color);
            }

        }
        /*我们是可以将状态栏文字的颜色改成深色的，官方也仅支持设置状态栏文字和图标的深色模式和浅色模式，但是官方仅在Android 6.0以上版本提供支持
        */
        private static void setTextDark(Window window, boolean isDark) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                View decorView = window.getDecorView();
                int systemUiVisibility = decorView.getSystemUiVisibility();
                if (isDark) {
                    decorView.setSystemUiVisibility(systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    decorView.setSystemUiVisibility(systemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        }

        /*
         * 同样再增加一个对Activity的支持
         * */
        public static void setTextDark(Context context, boolean isDark) {
            if (context instanceof Activity) {
                setTextDark(((Activity) context).getWindow(), isDark);
            }
        }
        /* 为了能够根据状态栏背景颜色的深浅而自动设置文字的颜色，我们再新增一个判断颜色深浅的方法
         * */
        public static boolean isDarkColor(@ColorInt int color) {
            return ColorUtils.calculateLuminance(color) < 0.5;
        }
        public static void setTransparent(@NonNull Window window,Activity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
            //添加一个与状态栏高度一致的View
            setRootView(activity);
        }

        /**
         * 设置根布局参数
         */
        private static void setRootView(Activity activity) {
            ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
            for (int i = 0, count = parent.getChildCount(); i < count; i++) {
                View childView = parent.getChildAt(i);
                if (childView instanceof ViewGroup) {
                    childView.setFitsSystemWindows(true);
                    ((ViewGroup) childView).setClipToPadding(true);
                }
            }
        }

        public static void setTransparent(Context context) {
            if (context instanceof Activity) {
                setTransparent(((Activity) context).getWindow(), (Activity) context);
            }
        }
    }
}



