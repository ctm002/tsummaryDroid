package cl.cariola.tsummary

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Bundle
import android.util.Log
import cl.cariola.tsummary.data.ApiClient

class SyncAdapter: AbstractThreadedSyncAdapter
{

    private lateinit var mContentResolver: ContentResolver
    private lateinit var mAccountManager: AccountManager

    constructor(context: Context, autoInitialize: Boolean): super(context, autoInitialize) {
        this.mContentResolver = context.contentResolver
        this.mAccountManager = AccountManager.get(context);
    }

    constructor(context: Context, autoInitialize: Boolean, allowParallelSyncs: Boolean): super(context, autoInitialize, allowParallelSyncs){
        this.mContentResolver = context.contentResolver
    }

    override fun onPerformSync(account: Account?, extras: Bundle?, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult?) {
        val authToken= this.mAccountManager.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE, true);
        if (authToken != null)
        {
            val list = ApiClient.getListHours(20, "2018-04-01", "2018-05-01", authToken)
            Log.d("onPerformSync()", "sync")
        }
    }

}