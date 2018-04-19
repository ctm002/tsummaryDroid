package cl.cariola.tsummary

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Bundle
import android.util.Log
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.provider.TSummaryContract
import java.text.SimpleDateFormat


class SyncAdapter: AbstractThreadedSyncAdapter
{


    private lateinit var mContentResolver: ContentResolver
    private lateinit var mAccountManager: AccountManager
    private val TAG = "SYNC_ADAPTER"
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    constructor(context: Context, autoInitialize: Boolean): super(context, autoInitialize)
    {
        this.mContentResolver = context.contentResolver
        this.mAccountManager = AccountManager.get(context);
    }

    constructor(context: Context, autoInitialize: Boolean, allowParallelSyncs: Boolean): super(context, autoInitialize, allowParallelSyncs)
    {
        this.mContentResolver = context.contentResolver
    }

    override fun onPerformSync(account: Account?, extras: Bundle?, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult?)
    {
        Log.w(TAG, "Starting synchronization...")
        try {
            val authToken= this.mAccountManager.getUserData(account, Constants.AUTH_TOKEN);
            if (authToken != null)
            {
                syncNewsRegistroHora(authToken)
                syncNewsProyectos(authToken)
            }
        }
        catch (exc: Exception)
        {
            Log.d(TAG, exc.toString())
        }
        Log.w(TAG, "Finished synchronization!");
    }

    private fun syncNewsRegistroHora(authToken: String)
    {
        Log.i(TAG, "Fetching local entries...")
        var batch = ArrayList<ContentProviderOperation>()
        val listRegistroHoras = ApiClient.getListHoursV2(20, "2018-04-01", "2018-05-01", authToken)
        if (listRegistroHoras != null) {
            var c = this.mContentResolver.query(TSummaryContract.RegistroHora.CONTENT_URI, TSummaryContract.RegistroHora.PROJECTION_REGISTRO_HORA, "1 = ?", arrayOf<String>("1"), "")
            assert(c != null)
            c.moveToFirst()
            while (!c.isAfterLast) {

                val correlativo = c.getString(c.getColumnIndex(TSummaryContract.RegistroHora.COL_TIM_CORREL))
                val dateUpdate = dateFormat.parse(c.getString(c.getColumnIndex(TSummaryContract.RegistroHora.COL_FECHA_ULT_MOD)))

                val found = listRegistroHoras.get(correlativo)
                if (found != null) {
                    listRegistroHoras.remove(correlativo)
                    if (dateUpdate < found.mFechaUpdate) {
                        batch.add(ContentProviderOperation.newUpdate(TSummaryContract.RegistroHora.CONTENT_URI)
                                .withSelection(TSummaryContract.RegistroHora.COL_TIM_CORREL + "=" + correlativo, null)
                                .withValue(TSummaryContract.RegistroHora.COL_PRO_ID, found.mProyectoId)
                                .withValue(TSummaryContract.RegistroHora.COL_TIM_ASUNTO, found.mAsunto)
                                .withValue(TSummaryContract.RegistroHora.COL_TIM_HORAS, found.mHoraTotal.horas)
                                .withValue(TSummaryContract.RegistroHora.COL_TIM_MINUTOS, found.mHoraTotal.minutos)
                                .withValue(TSummaryContract.RegistroHora.COL_FECHA_INSERT, dateFormat.format(found.mFechaInsert))
                                .withValue(TSummaryContract.RegistroHora.COL_FECHA_ING, dateFormat.format(found.mFechaIng))
                                .withValue(TSummaryContract.RegistroHora.COL_OFFLINE, found.mOffLine)
                                .withValue(TSummaryContract.RegistroHora.COL_ESTADO, found.mEstado.value)
                                .withValue(TSummaryContract.RegistroHora.COL_ABO_ID, found.mAbogadoId)
                                .build())
                    }
                }
                else
                {
                    batch.add(ContentProviderOperation.newDelete(TSummaryContract.RegistroHora.CONTENT_URI)
                            .withSelection(TSummaryContract.RegistroHora.COL_ID + "=" + correlativo, null)
                            .build())
                }
                c.moveToNext()
            }

            c.close()

            for (item in listRegistroHoras.values) {
                batch.add(ContentProviderOperation.newInsert(TSummaryContract.RegistroHora.CONTENT_URI)
                        .withValue(TSummaryContract.RegistroHora.COL_TIM_CORREL, item.mCorrelativo)
                        .withValue(TSummaryContract.RegistroHora.COL_PRO_ID, item.mProyectoId)
                        .withValue(TSummaryContract.RegistroHora.COL_TIM_ASUNTO, item.mAsunto)
                        .withValue(TSummaryContract.RegistroHora.COL_TIM_HORAS, item.mHoraTotal.horas)
                        .withValue(TSummaryContract.RegistroHora.COL_TIM_MINUTOS, item.mHoraTotal.minutos)
                        .withValue(TSummaryContract.RegistroHora.COL_FECHA_ING,  dateFormat.format(item.mFechaIng))
                        .withValue(TSummaryContract.RegistroHora.COL_FECHA_HORA_INICIO, "${item.mInicio.horas}:${item.mInicio.minutos}")
                        .withValue(TSummaryContract.RegistroHora.COL_FECHA_INSERT, dateFormat.format(item.mFechaInsert))
                        .withValue(TSummaryContract.RegistroHora.COL_FECHA_ULT_MOD, dateFormat.format(item.mFechaUpdate))
                        .withValue(TSummaryContract.RegistroHora.COL_OFFLINE, item.mOffLine)
                        .withValue(TSummaryContract.RegistroHora.COL_ESTADO, item.mEstado.value)
                        .withValue(TSummaryContract.RegistroHora.COL_ABO_ID, item.mAbogadoId)
                        .build()
                )
            }
            this.mContentResolver.applyBatch(TSummaryContract.AUTHORITY, batch)
            this.mContentResolver.notifyChange(TSummaryContract.RegistroHora.CONTENT_URI, null, false)
        }
    }

    private fun syncNewsProyectos(authToken: String)
    {

        Log.i(TAG, "Fetching local entries...")
        var batch = ArrayList<ContentProviderOperation>()
        val lstProyectos = ApiClient.getListProjectsV2(authToken)

        if (lstProyectos != null)
        {

            var c = this.mContentResolver.query(TSummaryContract.Proyecto.CONTENT_URI, TSummaryContract.Proyecto.PROJECTION_PROYECTO, "1=?",  arrayOf<String>("1"), "")
            assert( c != null)
            c.moveToFirst()
            while(!c.isAfterLast){

                val id = c.getString(c.getColumnIndex(TSummaryContract.Proyecto.COL_ID))
                val dateUpdate = dateFormat.parse(c.getString(c.getColumnIndex(TSummaryContract.Proyecto.COL_FECHA_ULT_MOD)))

                val found = lstProyectos.get(id)
                if (found != null)
                {
                    lstProyectos.remove(id)
                    if (dateUpdate < found.fechaUpdate)
                    {
                        batch.add(ContentProviderOperation.newUpdate(TSummaryContract.Proyecto.CONTENT_URI)
                                .withSelection( TSummaryContract.Proyecto.COL_ID + "=" + id, null)
                                .withValue(TSummaryContract.Proyecto.COL_NOMBRE, found.nombre)
                                .withValue(TSummaryContract.Proyecto.COL_CLI_COD, found.cliente.codigo)
                                .withValue(TSummaryContract.Proyecto.COL_CLI_NOM, found.cliente.nombre)
                                .withValue(TSummaryContract.Proyecto.COL_ESTADO, found.estado)
                                .withValue(TSummaryContract.Proyecto.COL_IDIOMA, found.cliente.idioma)
                                .withValue(TSummaryContract.Proyecto.COL_FECHA_ULT_MOD, dateFormat.format(found.fechaUpdate))
                                .build())
                    }
                }
                else
                {
                    batch.add(ContentProviderOperation.newDelete(TSummaryContract.Proyecto.CONTENT_URI)
                            .withSelection(TSummaryContract.Proyecto.COL_ID + "=" + id, null )
                            .build())
                }

                c.moveToNext()
            }

            c.close()

            for (item in lstProyectos.values)
            {
                batch.add(ContentProviderOperation.newInsert(TSummaryContract.Proyecto.CONTENT_URI)
                        .withValue(TSummaryContract.Proyecto.COL_ID, item.id)
                        .withValue(TSummaryContract.Proyecto.COL_NOMBRE, item.nombre)
                        .withValue(TSummaryContract.Proyecto.COL_CLI_COD, item.cliente.codigo)
                        .withValue(TSummaryContract.Proyecto.COL_CLI_NOM, item.cliente.nombre)
                        .withValue(TSummaryContract.Proyecto.COL_ESTADO, item.estado)
                        .withValue(TSummaryContract.Proyecto.COL_IDIOMA, item.cliente.idioma)
                        .withValue(TSummaryContract.Proyecto.COL_FECHA_ULT_MOD,  dateFormat.format(item.fechaUpdate))
                        .build()
                )
            }

            this.mContentResolver.applyBatch(TSummaryContract.AUTHORITY, batch)
            this.mContentResolver.notifyChange(TSummaryContract.Proyecto.CONTENT_URI,null, false)
        }
    }

}