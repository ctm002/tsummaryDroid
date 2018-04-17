package cl.cariola.tsummary

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class SyncService: Service() {

    private val TAG = "SyncService"
    private val sSyncAdapterLock = Any()
    private var sSyncAdapter: SyncAdapter? = null

    override fun onBind(intent: Intent?): IBinder {
        return sSyncAdapter!!.syncAdapterBinder;
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service created")
        synchronized(sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = SyncAdapter(applicationContext, true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Service destroyed")
    }

}