package cl.cariola.tsummary

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import cl.cariola.tsummary.business.controllers.AutentificarController
import cl.cariola.tsummary.business.controllers.ProyectoController
import android.accounts.AccountManager
import android.content.ContentResolver
import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.app.Activity
import android.util.Log
import cl.cariola.tsummary.provider.RegistroHoraContract


class RegistrarCuentaActivity: AccountAuthenticatorActivity()
{
    lateinit var editTxtLoginName : EditText
    lateinit var editTxtPassword : EditText
    lateinit var btnRegistrar : Button
    lateinit var btnResetData : Button
    lateinit var _context: Context

    /** The tag used to log to adb console.  */

    private val TAG = "AuthenticatorActivity"
    lateinit var mAccountManager: AccountManager

    val PARAM_USERNAME = "username"
    val PARAM_CONFIRM_CREDENTIALS = "confirmCredentials"

    private var mRequestNewAccount = false
    private var mConfirmCredentials = false

    var mUsername: String?  = null
    var mPassword: String? = null
    var mIMEI: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cuenta)

        editTxtLoginName = findViewById(R.id.editTxtUserName)
        editTxtPassword = findViewById(R.id.editTxtPassword)
        editTxtLoginName.setText("Carlos_Tapia")
        editTxtPassword.setText("Car.2711")

        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnResetData = findViewById(R.id.btnResetData)

        this._context = this

        btnResetData.setOnClickListener{
            this.resetData()
        }

        btnRegistrar.setOnClickListener {
            this.initial()
        }

        this.mAccountManager = AccountManager.get(this)

        mUsername = intent.getStringExtra(PARAM_USERNAME)
        mRequestNewAccount = (mUsername == null)
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false)
        mIMEI = "863166032574597"
    }

    fun initial()
    {
        mUsername = editTxtLoginName.text.toString()
        mPassword = editTxtPassword.text.toString()

        Constants.ACCOUNT_NAME = mUsername!!

        var task = RegistrarCuenta()
        task.messages ="Descargando datos..."
        task.actions = Acciones.INITIAL
        task.execute()
    }

    fun resetData()
    {
        var task = RegistrarCuenta()
        task.messages ="Eliminando datos..."
        task.actions = Acciones.ELIMINAR_TODO
        task.execute()
    }

    enum class Acciones(val value : Int)
    {
        ELIMINAR_TODO(0),
        INITIAL(1);

        companion object {
            fun from(findValue: Int): Acciones = Acciones.values().first { it.value == findValue }
        }
    }

    private fun finishLogin(authToken: String)
    {
        val account = Account(mUsername, Constants.ACCOUNT_TYPE)
        if (mRequestNewAccount)
        {

            val data = Bundle()
            data.putString("IMEI", this.mIMEI)
            //data.putString("AUTHTOKEN", authToken)
            mAccountManager.setAuthToken(account, Constants.AUTHTOKEN_TYPE, authToken)
            mAccountManager.addAccountExplicitly(account, mPassword, data)

            // Set contacts sync for this account.
            ContentResolver.setIsSyncable(account, RegistroHoraContract.AUTHORITY, 1)
            ContentResolver.setSyncAutomatically(account, RegistroHoraContract.AUTHORITY, true)
        }
        else
        {
            mAccountManager.setPassword(account, mPassword)
        }
        val intent = Intent()
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername)
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE)
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken)
        setAccountAuthenticatorResult(intent.extras)
        setResult(Activity.RESULT_OK, intent)
        //finish()
    }

    fun onAuthenticationResult(authToken: String?) {
        val success = authToken != null && authToken.length > 0
        Log.i(TAG, "onAuthenticationResult($success)")
        // Our task is complete, so clear it out
        //mAuthTask = null
        // Hide the progress dialog
        //hideProgress()
        if (success)
        {
            if (!mConfirmCredentials)
            {
                finishLogin(authToken!!)
            }
            else
            {
                //finishConfirmCredentials(success)
            }
        } else {

        }
    }

    internal inner class RegistrarCuenta(): AsyncTask<Void, Void, String>()
    {

        lateinit var actions : Acciones
        lateinit var messages : String
        lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(_context)
            progressDialog.setTitle(messages)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            if (NetWorkStatus.isNetworkAvailable(_context))
            {
                if (actions == Acciones.INITIAL)
                {
                    val autentificar = AutentificarController(_context)
                    val sesionLocal = autentificar.register(mIMEI!!, mUsername!!, mPassword!!)
                    onAuthenticationResult(sesionLocal?.authToken)
                    return "OK"
                }
                else if (actions == Acciones.ELIMINAR_TODO)
                {
                    val controller = ProyectoController(_context)
                    controller.resetData()
                    return "ERROR"
                }
                return ""
            }
            else
            {
                return "SIN INTERNET"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (result != null && result == "PASS")
            {
                val intent = Intent(_context, SchedulerActivity:: class.java)
                _context.startActivity(intent)
            }
        }
    }
}