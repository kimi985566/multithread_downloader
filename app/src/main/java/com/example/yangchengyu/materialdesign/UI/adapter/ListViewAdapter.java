package com.example.yangchengyu.materialdesign.UI.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangchengyu.materialdesign.R;
import com.example.yangchengyu.materialdesign.UI.MainActivity;
import com.example.yangchengyu.materialdesign.downloadHelper.DownloadListener;
import com.example.yangchengyu.materialdesign.downloadHelper.DownloadManager;
import com.example.yangchengyu.materialdesign.entity.DBDownloadInfo;
import com.example.yangchengyu.materialdesign.entity.TaskInfo;

import java.util.ArrayList;

/**
 * Created by YangChengyu on 2017/4/8.
 */

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<TaskInfo> mInfoArrayList = new ArrayList<TaskInfo>();
    private Context mContext;
    private static final String TAG = ListViewAdapter.class.getSimpleName();
    private DownloadManager mDownloadManager;

    public ListViewAdapter(Context context, DownloadManager downloadManager) {
        this.mContext = context;
        this.mDownloadManager = downloadManager;
        this.mInfoArrayList = downloadManager.getAllTask();
        downloadManager.setAllTaskListener(new DownloadManagerListener());
    }

    @Override
    public int getCount() {
        return mInfoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mInfoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final TaskInfo info = mInfoArrayList.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, null);
            holder.mTextView = (TextView) convertView.findViewById(R.id.item_list_title);
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.item_list_progress_bar);
            holder.mBtn_download = (Button) convertView.findViewById(R.id.item_list_btn_download);
            holder.mBtn_pause = (Button) convertView.findViewById(R.id.item_list_btn_pause);
            holder.mTextView_progress = (TextView) convertView.findViewById(R.id.item_list_text_progress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTextView.setText(mInfoArrayList.get(position).getFileName());
        holder.mProgressBar.setMax(100);
        holder.mProgressBar.setProgress(mInfoArrayList.get(position).getProgress());
        holder.mTextView_progress.setText(mInfoArrayList.get(position).getProgress() + "%");

        View.OnClickListener startlistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfoArrayList.get(position).setOnDownloading(true);
                mDownloadManager.startTask(mInfoArrayList.get(position).getTaskID());
                Toast.makeText(mContext, "任务：" + mInfoArrayList.get(position).getTaskID().toString() + "：下载中",
                        Toast.LENGTH_SHORT).show();
                ListViewAdapter.this.notifyDataSetChanged();
            }
        };

        View.OnClickListener stoplistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfoArrayList.get(position).setOnDownloading(false);
                mDownloadManager.stopTask(mInfoArrayList.get(position).getTaskID());
                Toast.makeText(mContext, "任务：" + mInfoArrayList.get(position).getTaskID().toString() + "：下载暂停",
                        Toast.LENGTH_SHORT).show();
                ListViewAdapter.this.notifyDataSetChanged();
            }
        };
        holder.mBtn_download.setOnClickListener(startlistener);
        holder.mBtn_pause.setOnClickListener(stoplistener);

        return convertView;
    }

    public void addItem(TaskInfo taskinfo) {
        this.mInfoArrayList.add(taskinfo);
        this.notifyDataSetInvalidated();
    }

    public void setListdata(ArrayList<TaskInfo> listdata) {
        this.mInfoArrayList = listdata;
        this.notifyDataSetInvalidated();
    }

    static class ViewHolder {
        TextView mTextView;
        TextView mTextView_progress;
        ProgressBar mProgressBar;
        Button mBtn_download;
        Button mBtn_pause;
    }

    private class DownloadManagerListener implements DownloadListener {
        @Override
        public void onStart(DBDownloadInfo dbDownloadInfo) {

        }

        @Override
        public void onProgress(DBDownloadInfo dbDownloadInfo, boolean isSupportBreakpoint) {
            //根据监听到的信息查找列表相对应的任务，更新相应任务的进度
            for (TaskInfo taskInfo : mInfoArrayList) {
                if (taskInfo.getTaskID().equals(dbDownloadInfo.getTaskId())) {
                    taskInfo.setFinished(dbDownloadInfo.getFinished());
                    taskInfo.setFileLength(dbDownloadInfo.getLength());
                    ListViewAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onStop(DBDownloadInfo dbDownloadInfo, boolean isSupportBreakpoint) {

        }

        @Override
        public void onError(DBDownloadInfo dbDownloadInfo) {
            for (TaskInfo taskInfo : mInfoArrayList) {
                if (taskInfo.getTaskID().equals(dbDownloadInfo.getTaskId())) {
                    taskInfo.setOnDownloading(false);
                    Toast.makeText(mContext, "Download Failed", Toast.LENGTH_LONG).show();
                    ListViewAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onSuccess(DBDownloadInfo dbDownloadInfo) {
            //根据监听到的信息查找列表相对应的任务，删除对应的任务
            for (TaskInfo taskInfo : mInfoArrayList) {
                if (taskInfo.getTaskID().equals(dbDownloadInfo.getTaskId())) {
                    mInfoArrayList.remove(taskInfo);
                    Toast.makeText(mContext, "Download Success", Toast.LENGTH_LONG).show();
                    ListViewAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}
