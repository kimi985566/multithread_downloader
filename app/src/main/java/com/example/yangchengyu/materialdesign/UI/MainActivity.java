package com.example.yangchengyu.materialdesign.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yangchengyu.materialdesign.R;
import com.example.yangchengyu.materialdesign.UI.adapter.ListViewAdapter;
import com.example.yangchengyu.materialdesign.Utils.StatusBarActivity;
import com.example.yangchengyu.materialdesign.downloadHelper.DownloadManager;
import com.example.yangchengyu.materialdesign.downloadHelper.DownloadService;
import com.example.yangchengyu.materialdesign.entity.TaskInfo;

public class MainActivity extends StatusBarActivity {

    private DrawerLayout mDrawerLayout;
    private EditText mEditText_download;
    private ListView mListView;
    private DownloadManager mDownloadManager;
    private ListViewAdapter mListViewAdapter;
    private EditText mEditText_fileName;
    private int menuItem;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler.sendEmptyMessageDelayed(1, 50);
        initView();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);//找到最大的容器DrawerLayout

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                menuItem = item.getItemId();
                switch (menuItem) {
                    case R.id.nav_download:
                        View show_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_download, null);
                        mEditText_download = (EditText) show_view.findViewById(R.id.dialog_download_editText);
                        mEditText_fileName = (EditText) show_view.findViewById(R.id.dialog_download_fileName);
                        new AlertDialog.Builder(MainActivity.this).setView(show_view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if ("".equals(mEditText_download.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "请输入下载地址", Toast.LENGTH_SHORT).show();
                                } else {
                                    TaskInfo taskInfo = new TaskInfo();
                                    taskInfo.setFileName(mEditText_fileName.getText().toString());
                                    taskInfo.setTaskID(mEditText_fileName.getText().toString());
                                    taskInfo.setOnDownloading(true);

                                    mDownloadManager.addTask(mEditText_fileName.getText().toString(),
                                            mEditText_download.getText().toString(),
                                            mEditText_fileName.getText().toString());
                                    mListViewAdapter.addItem(taskInfo);
                                    Toast.makeText(MainActivity.this, "添加任务成果", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        break;

                    case R.id.nav_about:
                        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mListView = (ListView) findViewById(R.id.list_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                diaglog_download(v);
            }
        });
    }

    private void diaglog_download(final View v) {
        View show_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_download, null);
        mEditText_download = (EditText) show_view.findViewById(R.id.dialog_download_editText);
        mEditText_fileName = (EditText) show_view.findViewById(R.id.dialog_download_fileName);
        new AlertDialog.Builder(MainActivity.this).setView(show_view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("".equals(mEditText_download.getText().toString())) {
                    Snackbar.make(v, "请输入下载地址", Snackbar.LENGTH_LONG).show();
                } else {
                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setFileName(mEditText_fileName.getText().toString());
                    taskInfo.setTaskID(mEditText_fileName.getText().toString());
                    taskInfo.setOnDownloading(true);

                    mDownloadManager.addTask(mEditText_fileName.getText().toString(),
                            mEditText_download.getText().toString(),
                            mEditText_fileName.getText().toString());
                    mListViewAdapter.addItem(taskInfo);
                    Snackbar.make(v, "添加任务成功", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).setDuration(3000).show();
                }
            }
        }).setNegativeButton("取消", null).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        menuItem = item.getItemId();

        switch (menuItem) {
            case R.id.action_about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /*获取下载管理器*/
            mDownloadManager = DownloadService.getDownloadManager();
            /*断点续传需要服务器的支持，设置该项时要先确保服务器支持断点续传功能*/
            mDownloadManager.setSupportBreakpoint(true);
            mListViewAdapter = new ListViewAdapter(MainActivity.this, mDownloadManager);
            mListView.setAdapter(mListViewAdapter);
        }
    };
}
