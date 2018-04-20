package cl.cariola.tsummary

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
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import cl.cariola.tsummary.business.entities.SesionLocal
import cl.cariola.tsummary.provider.TSContract
import java.text.SimpleDateFormat
import java.util.*

enum class Acciones(val value: Int) {
    ELIMINAR_TODO(0),
    INITIAL(1);

    companion object {
        fun from(findValue: Int): Acciones = Acciones.values().first { it.value == findValue }
    }
}

class RegistrarCuentaActivity : AccountAuthenticatorActivity() {

    lateinit var editTxtLoginName: EditText
    lateinit var editTxtPassword: EditText
    lateinit var btnRegistrar: Button
    lateinit var btnResetData: Button
    lateinit var mContext: Context

    private val TAG = "AuthenticatorActivity"
    lateinit var mAccountManager: AccountManager

    val PARAM_USERNAME = "username"
    val PARAM_CONFIRM_CREDENTIALS = "confirmCredentials"

    private var mRequestNewAccount = false
    private var mConfirmCredentials = false

    var mUsername: String? = null
    var mPassword: String? = null
    var mIMEI: String? = null
    var task: RegistrarCuentaTask? = null
    lateinit var mProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cuenta)

        editTxtLoginName = findViewById(R.id.editTxtUserName)
        editTxtPassword = findViewById(R.id.editTxtPassword)
        editTxtLoginName.setText("Carlos_Tapia")
        editTxtPassword.setText("Car.2711")

        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnResetData = findViewById(R.id.btnResetData)

        this.mContext = this

        btnResetData.setOnClickListener {
            this.resetData()
        }

        btnRegistrar.setOnClickListener {
            this.sendData()
        }

        this.mAccountManager = AccountManager.get(this)

        mUsername = intent.getStringExtra(PARAM_USERNAME)
        mRequestNewAccount = (mUsername == null)
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false)
        this.mIMEI = "863166032574597"

        this.mProgressBar = findViewById(R.id.progressBar)
        this.mProgressBar.isIndeterminate = true
        this.mProgressBar.visibility = View.GONE

    }

    fun sendData() {
        mUsername = editTxtLoginName.text.toString()
        mPassword = editTxtPassword.text.toString()

        Constants.ACCOUNT_NAME = mUsername!!
        this.task = RegistrarCuentaTask()
        task?.messages = "Descargando datos..."
        task?.actions = Acciones.INITIAL
        task?.execute()


    }

    fun resetData() {
        this.task = RegistrarCuentaTask()
        this.task?.messages = "Eliminando datos..."
        this.task?.actions = Acciones.ELIMINAR_TODO
        this.task?.execute()
    }

    private fun finishLogin(authToken: String) {
        val account = Account(mUsername, Constants.ACCOUNT_TYPE)
        var accounts = mAccountManager.accounts.filter { it.type == Constants.ACCOUNT_TYPE }
        for (acc in accounts) {
            if (acc.name == mUsername) {
                mRequestNewAccount = false
                break
            }
        }

        if (mRequestNewAccount) {
            val data = Bundle()
            mAccountManager.addAccountExplicitly(account, mPassword, data)
            mAccountManager.setUserData(account, Constants.IMEI_SMARTPHONE, this.mIMEI)
            mAccountManager.setUserData(account, Constants.AUTH_TOKEN, authToken)
            mAccountManager.setPassword(account, mPassword)

            ContentResolver.setIsSyncable(account, TSContract.AUTHORITY, 1)
            ContentResolver.setSyncAutomatically(account, TSContract.AUTHORITY, true)
        } else {
            mAccountManager.setUserData(account, Constants.IMEI_SMARTPHONE, this.mIMEI)
            mAccountManager.setUserData(account, Constants.AUTH_TOKEN, authToken)
            mAccountManager.setPassword(account, mPassword)
            mAccountManager.setAuthToken(account, Constants.AUTH_TOKEN, authToken)
        }

        val intent = Intent(this.mContext, SchedulerActivity::class.java)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        intent.putExtra("fecha", dateFormat.format(Date()))
        val sesionLocal = SesionLocal(authToken, this.mIMEI!!)
        intent.putExtra("idAbogado", sesionLocal.getIdAbogado())
        startActivity(intent)
    }

    fun onAuthenticationResult(authToken: String?) {
        val success = authToken != null && authToken.length > 0
        Log.i(TAG, "onAuthenticationResult($success)")
        this.task = null
        if (success) {
            if (!mConfirmCredentials) {
                finishLogin(authToken!!)
            }
        }
    }

    inner class RegistrarCuentaTask() : AsyncTask<Void, Void, String>() {
        lateinit var actions: Acciones
        lateinit var messages: String

        override fun onPreExecute() {
            super.onPreExecute()
            mProgressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): String {
            if (NetWorkStatus.isNetworkAvailable(mContext)) {
                if (actions == Acciones.INITIAL) {
                    val autentificar = AutentificarController(mContext)
                    val sesionLocal = autentificar.register(mIMEI!!, mUsername!!, mPassword!!)
                    if (sesionLocal?.authToken != "") {
                        return sesionLocal?.authToken!!
                    } else {
                        return "FAIL"
                    }
                } else if (actions == Acciones.ELIMINAR_TODO) {
                    val controller = ProyectoController(mContext)
                    controller.resetData()
                    return "ERROR"
                }
                return ""
            } else {
                return "SIN INTERNET"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            mProgressBar.visibility = View.GONE
            onAuthenticationResult(result)
        }
    }

}