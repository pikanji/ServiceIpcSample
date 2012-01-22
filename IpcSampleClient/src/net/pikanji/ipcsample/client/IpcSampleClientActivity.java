package net.pikanji.ipcsample.client;

import net.pikanji.ipcsample.server.IIpcSampleServerService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class IpcSampleClientActivity extends Activity {

    private IIpcSampleServerService mSampleServiceIf;
    private final Object mMutex = new Object();
    
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TEST", "onServiceConnected");
            mSampleServiceIf = IIpcSampleServerService.Stub.asInterface(service);
            if (null != mMutex) {
                synchronized (mMutex) {
                    mMutex.notifyAll();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("TEST", "onServiceDisconnected");
            mSampleServiceIf = null;
        }
        
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unbindService(mServiceConnection);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            new RemoteTask().execute();
        }
        
        return true;
    }
    
    private class RemoteTask extends AsyncTask<Void, Void, Boolean> {
        private static final long WAIT_TIMEOUT = 7000; // 5sec
        private final Context mContext = IpcSampleClientActivity.this;
        
        @Override
        protected void onPreExecute() {
            if (null == mSampleServiceIf) {
                Intent intent = new Intent(IIpcSampleServerService.class.getName());
                mContext.bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
            }
        }

        /**
         * Wait until the Service is bound. Returns false on timeout.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (null == mSampleServiceIf) {
                    Thread.sleep(WAIT_TIMEOUT);
                } else {
                    return true;
                }
            } catch (InterruptedException e) {
                Log.e("TEST", "notified");
                return true; // Expected to be interrupted by notify()
            }
            Log.e("TEST", "TIMEOUT");
            return false; // Timeout
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                try {
                    Toast.makeText(mContext, "Service's PID: " + mSampleServiceIf.getPid(), Toast.LENGTH_LONG).show();
                } catch (RemoteException e) {
                    Toast.makeText(mContext, "RemoteException: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            //mContext.unbindService(mServiceConnection);
        }
        
    }

}