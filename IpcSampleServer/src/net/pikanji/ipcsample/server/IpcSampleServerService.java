package net.pikanji.ipcsample.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;

public class IpcSampleServerService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IIpcSampleServerService.Stub mBinder = new IIpcSampleServerService.Stub() {

        @Override
        public void setData(int data) throws RemoteException {
            return;
        }

        @Override
        public int getData() throws RemoteException {
            return 123;
        }

        @Override
        public int getPid() throws RemoteException {
            return Process.myPid();
        }
    };
}
