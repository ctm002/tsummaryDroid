package cl.cariola.tsummary.accounts

import android.accounts.*
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.SyncStateContract
import android.util.Log;
import android.accounts.AccountManager
import android.text.TextUtils
import cl.cariola.tsummary.Constants
import cl.cariola.tsummary.RegistrarCuentaActivity
import cl.cariola.tsummary.business.entities.SesionLocal
import cl.cariola.tsummary.data.ApiClient


class GenericAccountService: Service() {

    private lateinit var mAuthenticator : Authenticator

    companion object {
        val TAG = "GenericAccountService"

        fun getAccount() : Account
        {
            val accountName = Constants.ACCOUNT_NAME
            return Account(accountName, Constants.ACCOUNT_TYPE)

        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return this.mAuthenticator.iBinder
    }

    override fun onCreate() {
        Log.i(TAG, "Service created")
        this.mAuthenticator = Authenticator(this)
    }

    override fun onDestroy() {
        Log.i(TAG, "Service destroyed")
    }

    class  Authenticator: AbstractAccountAuthenticator
    {

        var mContext : Context?

        constructor(context: Context?): super(context)
        {
            this.mContext = context
        }

        override fun getAuthTokenLabel(authTokenType: String?): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun confirmCredentials(response: AccountAuthenticatorResponse?, account: Account?, options: Bundle?): Bundle {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun updateCredentials(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getAuthToken(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle {

            Log.v(TAG, "getAuthToken()")
            if (!authTokenType.equals(Constants.AUTHTOKEN_TYPE)) {
                val result = Bundle()
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType")
                return result
            }

            val am = AccountManager.get(this.mContext)
            val imei =  am.getUserData(account, "IMEI")
            var authToken =  am.peekAuthToken(account, authTokenType)  // am.getUserData(account, "AUTHTOKEN")
            val sesionLocal = SesionLocal(authToken, imei)

            val password = am.getPassword(account)
            if (password != null && imei != null && (sesionLocal == null || sesionLocal.isExpired())) {
                authToken = ApiClient.register(imei ,account?.name!!, password)?.authToken
                if (!TextUtils.isEmpty(authToken)) {
                    am.setAuthToken(account, authTokenType, authToken)
                    val result = Bundle()
                    result.putString("IMEI", imei)
                    result.putString(AccountManager.KEY_ACCOUNT_NAME, account?.name)
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE, SyncStateContract.Constants.ACCOUNT_TYPE)
                    result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
                    return result
                }
            }
            am.setAuthToken(account, authTokenType, authToken)
            val bundle = Bundle()
            bundle.putString("IMEI", imei)
            return bundle
        }

        override fun hasFeatures(response: AccountAuthenticatorResponse?, account: Account?, features: Array<out String>?): Bundle {
            // This call is used to query whether the Authenticator supports
            // specific features. We don't expect to get called, so we always
            // return false (no) for any queries.
            Log.v(TAG, "hasFeatures()")
            val result = Bundle()
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
            return result
        }

        override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addAccount(response: AccountAuthenticatorResponse?, accountType: String?, authTokenType: String?, requiredFeatures: Array<out String>?, options: Bundle?): Bundle {
            Log.v(TAG, "addAccount()")
            val intent = Intent(mContext, RegistrarCuentaActivity::class.java)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            val bundle = Bundle()
            bundle.putParcelable(AccountManager.KEY_INTENT, intent)
            return bundle
        }

    }
}