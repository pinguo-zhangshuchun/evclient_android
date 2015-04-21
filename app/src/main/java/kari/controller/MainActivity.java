package kari.controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.SoftReference;

public class MainActivity extends Activity {
    final static String TAG = "MainActivity";

    public final static int ERROR_CONNECT_SLAVE = 0x1001;
    public final static int ERROR_INIT_SIZE = 0x1002;
    public final static int ERROR_REPORT_EVENT = 0x1003;
    public final static int MSG_INIT_SIZE = 0x1004;
    public final static int MSG_REPORT_EVENT = 0x1005;

    private MyHandler mHandler;
    private AspectRatioLayout mLayout;
    private ScreenView mView;
    TalkManager mTalkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        setListener();
        mHandler = new MyHandler(this);
        mTalkManager = new TalkManager(mHandler);

        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        Log.d(TAG, "width:" + width + ",height:" + height);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTalkManager.exit();
    }

    private void findView() {
        mLayout = (AspectRatioLayout) findViewById(R.id.main_layout);
        mView = (ScreenView) findViewById(R.id.main_view);
    }

    public void setListener() {
        mLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    int x = (int) event.getRawX();
                    int y = (int) event.getRawY();
                    Log.d(TAG, "onTouch x=" + x + ",y=" + y);
                    mTalkManager.send(x, y);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    class MyHandler extends Handler {
        final static String TAG = "MyHandler";

        SoftReference<MainActivity> mRef;

        public MyHandler(MainActivity activity) {
            super();
            mRef = new SoftReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mRef.get();
            if (null == activity) {
                Log.d(TAG, "activity is null in softReference");
                return;
            }

            switch (msg.what) {
                case ERROR_INIT_SIZE:
                    Toast.makeText(activity, "Failed init size", Toast.LENGTH_LONG).show();
                    break;

                case ERROR_REPORT_EVENT:
                    Toast.makeText(activity, "Failed recv event report ", Toast.LENGTH_LONG).show();
                    break;

                case ERROR_CONNECT_SLAVE:
                    Toast.makeText(activity, "Failed connect to server", Toast.LENGTH_LONG).show();
                    break;

                case MSG_INIT_SIZE:
                    mLayout.setSize(msg.arg1, msg.arg2);
                    break;

                case MSG_REPORT_EVENT:
                    mView.notifyEvent(msg.arg1, msg.arg2);
                    break;
            }
        }
    }
}
