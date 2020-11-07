package com.gemini.cloud.app.myprogressbar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

public class MainActivity2 extends Activity implements View.OnClickListener {

    private CustomProgressText mCustomProgressBar;
    private float mProgress;
    private int mStateType;
    private DownloadHandler mDownloadHandler;


    private static class DownloadHandler extends Handler {

        private WeakReference<Context> reference;

        DownloadHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity2 downloadActivity = (MainActivity2) reference.get();
            if (downloadActivity != null) {
                switch (msg.what) {
                    case 0:
                        if (downloadActivity.mProgress < 100) {
                            downloadActivity.mProgress += 2.0;
                            downloadActivity.mCustomProgressBar.setProgress(downloadActivity.mProgress);
                            downloadActivity.mDownloadHandler.sendEmptyMessageDelayed(0, 300);
                        } else {
                            downloadActivity.mStateType = CustomProgressText.STATE_DOWNLOAD_FINISH;
                            downloadActivity.mCustomProgressBar.setState(downloadActivity.mStateType);
                        }
                        break;

                    default:
                        break;
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadHandler = new DownloadHandler(this);
        mCustomProgressBar =  findViewById(R.id.cpt);
        mCustomProgressBar.setOnClickListener(this);
        mStateType = CustomProgressText.STATE_DEFAULT;
        mCustomProgressBar.setState(mStateType);
        mCustomProgressBar.setProgress(0);
    }

    @Override
    public void onClick(View v) {
        switch (mStateType) {
            case CustomProgressText.STATE_DEFAULT:
                mProgress = 0;
                mCustomProgressBar.setProgress(mProgress);
                mCustomProgressBar.setState(CustomProgressText.STATE_DOWNLOADING);
                mDownloadHandler.sendEmptyMessageDelayed(0, 500);
                break;

            case CustomProgressText.STATE_DOWNLOADING:
                mCustomProgressBar.setState(CustomProgressText.STATE_PAUSE);
                mDownloadHandler.removeMessages(0);
                break;

            case CustomProgressText.STATE_PAUSE:
                mCustomProgressBar.setState(CustomProgressText.STATE_DOWNLOADING);
                mDownloadHandler.sendEmptyMessageDelayed(0, 500);
                break;

            case CustomProgressText.STATE_DOWNLOAD_FINISH:
                mCustomProgressBar.setState(CustomProgressText.STATE_DEFAULT);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDownloadHandler.removeMessages(0);
    }

}
