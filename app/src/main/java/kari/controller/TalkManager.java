package kari.controller;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ws-kari on 15-4-20.
 */
public class TalkManager {
    final static String TAG = "TalkManager";

    private Handler mHandler;
    private ExecutorService mExecutorService;
    private Socket mSocket;
    private boolean mRunFlag = true;

    public TalkManager(Handler handler) {
        mHandler = handler;
        mExecutorService = Executors.newFixedThreadPool(2);
    }

    public void exit() {
        mRunFlag = false;
        if (null != mSocket) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mExecutorService.shutdown();
    }

    public void init(final String ip, final int port, final int token) {
        Log.d(TAG, "ip:" + ip + ",toke:" + token);

        mExecutorService.execute(new Runnable() {
            Message msg = null;

            @Override
            public void run() {
                try {
                    mSocket = new Socket(ip, port);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                int buffer[] = new int[4];
                buffer[0] = 0xa1;
                buffer[1] = 8;

                try {
                    mSocket.getOutputStream().write(int2byte(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = mHandler.obtainMessage();
                    msg.what = MainActivity.ERROR_CONNECT_SLAVE;
                    msg.obj = e.getMessage();
                    mHandler.sendMessage(msg);
                    return;
                }

                byte bytes[] = new byte[4 * 4];
                try {
                    mSocket.getInputStream().read(bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = mHandler.obtainMessage();
                    msg.what = MainActivity.ERROR_INIT_SIZE;
                    msg.obj = e.getMessage();
                    mHandler.sendMessage(msg);
                    return;
                }

                int[] readBuffer = byte2int(bytes);

                Log.d(TAG, "message type:" + readBuffer[0] + ", len:" + readBuffer[1]);
                Log.d(TAG, "width:" + readBuffer[2] + ",height:" + readBuffer[3]);
                msg = mHandler.obtainMessage();
                msg.what = MainActivity.MSG_INIT_SIZE;
                msg.arg1 = readBuffer[2];
                msg.arg2 = readBuffer[3];
                mHandler.sendMessage(msg);

                while (mRunFlag) {
                    try {
                        mSocket.getInputStream().read(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg = mHandler.obtainMessage();
                        msg.what = MainActivity.ERROR_REPORT_EVENT;
                        msg.obj = e.getMessage();
                        mHandler.sendMessage(msg);
                        return;
                    }

                    readBuffer = byte2int(bytes);
                    Log.e(TAG, "x=" + readBuffer[2] + ",y=" + readBuffer[3]);

                    msg = mHandler.obtainMessage();
                    msg.what = MainActivity.MSG_REPORT_EVENT;
                    msg.arg1 = readBuffer[2];
                    msg.arg2 = readBuffer[3];
                    mHandler.sendMessage(msg);
                }

            }
        });

    }

    public void send(final int x, final int y) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "send::run()");

                int buffer[] = new int[4];
                buffer[0] = 0xa2;
                buffer[1] = 16;
                buffer[2] = x;
                buffer[3] = y;
                try {
                    mSocket.getOutputStream().write(int2byte(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static byte[] int2byte(int res[]) {
        if (null == res) {
            return null;
        }
        byte[] targets = new byte[4 * res.length];
        for (int i = 0; i < res.length; ++i) {
            targets[3 + 4 * i] = (byte) (res[i] & 0xff);
            targets[2 + 4 * i] = (byte) ((res[i] >> 8) & 0xff);
            targets[1 + 4 * i] = (byte) ((res[i] >> 16) & 0xff);
            targets[0 + 4 * i] = (byte) (res[i] >>> 24);
        }
        return targets;
    }

    public static int[] byte2int(byte[] bytes) {
        if (null == bytes) {
            return null;
        }

        int ret[] = new int[bytes.length / 4];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = 0;
            ret[i] += (bytes[0 + 4 * i] & 0xff) << 24;
            ret[i] += (bytes[1 + 4 * i] & 0xff) << 16;
            ret[i] += (bytes[2 + 4 * i] & 0xff) << 8;
            ret[i] += bytes[3 + 4 * i] & 0xff;
        }

        return ret;
    }

}
