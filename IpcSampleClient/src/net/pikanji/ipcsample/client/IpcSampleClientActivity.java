package net.pikanji.ipcsample.client;

import net.pikanji.ipcsample.server.IIpcSampleServerService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.widget.Toast;

public class IpcSampleClientActivity extends Activity {

    private IIpcSampleServerService mSampleServiceIf;
    
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSampleServiceIf = IIpcSampleServerService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSampleServiceIf = null;
        }
        
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent intent = new Intent(IIpcSampleServerService.class.getName());
        this.bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        this.unbindService(mServiceConnection);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            try {
                Toast.makeText(this, "Service's PID: " + mSampleServiceIf.getPid(), Toast.LENGTH_LONG).show();
            } catch (RemoteException e) {
                Toast.makeText(this, "RemoteException: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        
        return true;
    }

}