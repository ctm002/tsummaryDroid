package cl.cariola.tsummary

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Bundle
import android.util.Log
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.provider.RegistroHoraContract
import java.text.SimpleDateFormat

class SyncAdapter: AbstractThreadedSyncAdapter
{

    private lateinit var mContentResolver: ContentResolver
    private lateinit var mAccountManager: AccountManager
    private val TAG = "SYNC_ADAPTER"

    private val PROJECTION_REGISTRO_HORA = arrayOf<String>(
            RegistroHoraContract.RegistroHora.COL_ID,
            RegistroHoraContract.RegistroHora.COL_TIM_ASUNTO ,
            RegistroHoraContract.RegistroHora.COL_TIM_CORREL,
            RegistroHoraContract.RegistroHora.COL_FECHA_ING,
            RegistroHoraContract.RegistroHora.COL_FECHA_INSERT,
            RegistroHoraContract.RegistroHora.COL_FECHA_ULT_MOD,
            RegistroHoraContract.RegistroHora.COL_TIM_HORAS,
            RegistroHoraContract.RegistroHora.COL_PRO_ID,
            RegistroHoraContract.RegistroHora.COL_TIM_MINUTOS,
            RegistroHoraContract.RegistroHora.COL_FECHA_HORA_INICIO,
            RegistroHoraContract.RegistroHora.COL_ABO_ID)


    private val PROJECTION_PROYECTO = arrayOf<String>(
            RegistroHoraContract.Proyecto.COL_ID,
            RegistroHoraContract.Proyecto.COL_NOMBRE,
            RegistroHoraContract.Proyecto.COL_CLI_COD,
            RegistroHoraContract.Proyecto.COL_CLI_NOM,
            RegistroHoraContract.Proyecto.COL_IDIOMA,
            RegistroHoraContract.Proyecto.COL_ESTADO)


    constructor(context: Context, autoInitialize: Boolean): super(context, autoInitialize) {
        this.mContentResolver = context.contentResolver
        this.mAccountManager = AccountManager.get(context);
    }

    constructor(context: Context, autoInitialize: Boolean, allowParallelSyncs: Boolean): super(context, autoInitialize, allowParallelSyncs){
        this.mContentResolver = context.contentResolver
    }

    override fun onPerformSync(account: Account?, extras: Bundle?, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult?)
    {
        Log.w(TAG, "Starting synchronization...")
        try {
            val authToken= this.mAccountManager.getUserData(account, Constants.AUTH_TOKEN);
            if (authToken != null)
            {
                //syncNewsRegistroHora(authToken)
                syncNewsProyectos(authToken)
            }
        }
        catch (exc: Exception)
        {
            Log.d(TAG, exc.toString())
        }
        Log.w(TAG, "Finished synchronization!");
    }

    private fun syncNewsRegistroHora(authToken: String) {
        Log.i(TAG, "Fetching local entries...")
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        var batch = ArrayList<ContentProviderOperation>()
        val listRegistroHoras = ApiClient.getListHoursV2(20, "2018-04-01", "2018-05-01", authToken)
        if (listRegistroHoras != null) {
            var c = this.mContentResolver.query(RegistroHoraContract.RegistroHora.CONTENT_URI, PROJECTION_REGISTRO_HORA, "1 = ?", arrayOf<String>("1"), "")
            assert(c != null)
            c.moveToFirst()
            while (!c.isAfterLast) {

                val correlativo = c.getString(c.getColumnIndex(RegistroHoraContract.RegistroHora.COL_TIM_CORREL))
                val dateUpdate = format.parse(c.getString(c.getColumnIndex(RegistroHoraContract.RegistroHora.COL_FECHA_ULT_MOD)))

                val found = listRegistroHoras.get(correlativo)
                if (found != null) {
                    listRegistroHoras.remove(correlativo)
                    if (dateUpdate < found.mFechaUpdate) {
                        batch.add(ContentProviderOperation.newUpdate(RegistroHoraContract.RegistroHora.CONTENT_URI)
                                .withSelection(RegistroHoraContract.RegistroHora.COL_TIM_CORREL + "=" + correlativo, null)
                                .withValue(RegistroHoraContract.RegistroHora.COL_PRO_ID, found.mProyectoId)
                                .withValue(RegistroHoraContract.RegistroHora.COL_TIM_ASUNTO, found.mAsunto)
                                .withValue(RegistroHoraContract.RegistroHora.COL_TIM_HORAS, found.mHoraTotal.horas)
                                .withValue(RegistroHoraContract.RegistroHora.COL_TIM_MINUTOS, found.mHoraTotal.minutos)
                                .withValue(RegistroHoraContract.RegistroHora.COL_FECHA_INSERT, format.format(found.mFechaInsert))
                                .withValue(RegistroHoraContract.RegistroHora.COL_FECHA_ING, format.format(found.mFechaIng))
                                .withValue(RegistroHoraContract.RegistroHora.COL_OFFLINE, found.mOffLine)
                                .withValue(RegistroHoraContract.RegistroHora.COL_ESTADO, found.mEstado.value)
                                .build())
                    }
                } else {
                    batch.add(ContentProviderOperation.newDelete(RegistroHoraContract.RegistroHora.CONTENT_URI)
                            .withSelection(RegistroHoraContract.RegistroHora.COL_ID + "=" + correlativo, null)
                            .build())
                }
                c.moveToNext()
            }

            c.close()

            for (item in listRegistroHoras.values) {
                batch.add(ContentProviderOperation.newInsert(RegistroHoraContract.RegistroHora.CONTENT_URI)
                        .withValue(RegistroHoraContract.RegistroHora.COL_TIM_CORREL, item.mCorrelativo)
                        .withValue(RegistroHoraContract.RegistroHora.COL_PRO_ID, item.mProyectoId)
                        .withValue(RegistroHoraContract.RegistroHora.COL_TIM_ASUNTO, item.mAsunto)
                        .withValue(RegistroHoraContract.RegistroHora.COL_TIM_HORAS, item.mHoraTotal.horas)
                        .withValue(RegistroHoraContract.RegistroHora.COL_TIM_MINUTOS, item.mHoraTotal.minutos)
                        .withValue(RegistroHoraContract.RegistroHora.COL_FECHA_INSERT, format.format(item.mFechaInsert))
                        .withValue(RegistroHoraContract.RegistroHora.COL_FECHA_ING, format.format(item.mFechaIng))
                        .withValue(RegistroHoraContract.RegistroHora.COL_OFFLINE, item.mOffLine)
                        .withValue(RegistroHoraContract.RegistroHora.COL_ESTADO, item.mEstado.value)
                        .build()
                )
            }
            this.mContentResolver.applyBatch(RegistroHoraContract.AUTHORITY, batch)
            this.mContentResolver.notifyChange(RegistroHoraContract.RegistroHora.CONTENT_URI, null, false)
        }
    }

    private fun syncNewsProyectos(authToken: String) {

        Log.i(TAG, "Fetching local entries...")
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        var batch = ArrayList<ContentProviderOperation>()
        val lstProyectos = ApiClient.getListProjectsV2(authToken)
        if (lstProyectos != null) {
            var c = this.mContentResolver.query(RegistroHoraContract.Proyecto.CONTENT_URI, PROJECTION_PROYECTO, "1=?",  arrayOf<String>("1"), "")
            assert( c != null)
            c.moveToFirst()
            while(!c.isAfterLast){

                val id = c.getString(c.getColumnIndex(RegistroHoraContract.Proyecto.COL_ID))
                val dateUpdate = format.parse(c.getString(c.getColumnIndex(RegistroHoraContract.Proyecto.COL_FECHA_ULT_MOD)))

                val found = lstProyectos.get(id)
                if (found != null)
                {
                    lstProyectos.remove(id)
                    if (dateUpdate < found.mFechaUpdate)
                    {
                        batch.add(ContentProviderOperation.newUpdate(RegistroHoraContract.Proyecto.CONTENT_URI)
                                .withSelection( RegistroHoraContract.Proyecto.COL_ID + "=" + id, null)
                                .withValue(RegistroHoraContract.Proyecto.COL_NOMBRE, found.nombre)
                                .withValue(RegistroHoraContract.Proyecto.COL_CLI_COD, found.cliente.codigo)
                                .withValue(RegistroHoraContract.Proyecto.COL_CLI_NOM, found.cliente.nombre)
                                .withValue(RegistroHoraContract.Proyecto.COL_ESTADO, found.estado)
                                .withValue(RegistroHoraContract.Proyecto.COL_IDIOMA, found.cliente.idioma)
                                .build())
                    }
                }
                else
                {
                    batch.add(ContentProviderOperation.newDelete(RegistroHoraContract.Proyecto.CONTENT_URI)
                            .withSelection(RegistroHoraContract.Proyecto.COL_ID + "=" + id, null )
                            .build())
                }
                c.moveToNext()
            }

            c.close()

            for (item in lstProyectos.values)
            {
                batch.add(ContentProviderOperation.newInsert(RegistroHoraContract.Proyecto.CONTENT_URI)
                        .withValue(RegistroHoraContract.Proyecto.COL_ID, item.id)
                        .withValue(RegistroHoraContract.Proyecto.COL_NOMBRE, item.nombre)
                        .withValue(RegistroHoraContract.Proyecto.COL_CLI_COD, item.cliente.codigo)
                        .withValue(RegistroHoraContract.Proyecto.COL_CLI_NOM, item.cliente.nombre)
                        .withValue(RegistroHoraContract.Proyecto.COL_ESTADO, item.estado)
                        .withValue(RegistroHoraContract.Proyecto.COL_IDIOMA, item.cliente.idioma)
                        //.withValue(RegistroHoraContract.Proyecto.COL_IDIOMA, item.cliente.idioma)
                        .build()
                )
            }

            this.mContentResolver.applyBatch(RegistroHoraContract.AUTHORITY, batch)
            this.mContentResolver.notifyChange(RegistroHoraContract.Proyecto.CONTENT_URI,null, false)
        }
    }

}