package cl.cariola.tsummary

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Bundle
import android.util.Log
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.provider.TSContract
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
            var c = this.mContentResolver.query(TSContract.RegistroHora.CONTENT_URI, TSContract.RegistroHora.PROJECTION_REGISTRO_HORA, null, null, "")
            assert(c != null)
            c.moveToFirst()
            while (!c.isAfterLast) {

                val correlativo = c.getString(c.getColumnIndex(TSContract.RegistroHora.COL_TIM_CORREL))
                val dateUpdate = dateFormat.parse(c.getString(c.getColumnIndex(TSContract.RegistroHora.COL_FECHA_ULT_MOD)))

                val found = listRegistroHoras.get(correlativo)
                if (found != null) {
                    listRegistroHoras.remove(correlativo)
                    if (dateUpdate < found.mFechaUpdate) {
                        batch.add(ContentProviderOperation.newUpdate(TSContract.RegistroHora.CONTENT_URI)
                                .withSelection(TSContract.RegistroHora.COL_TIM_CORREL + "=" + correlativo, null)
                                .withValue(TSContract.RegistroHora.COL_PRO_ID, found.mProyectoId)
                                .withValue(TSContract.RegistroHora.COL_TIM_ASUNTO, found.mAsunto)
                                .withValue(TSContract.RegistroHora.COL_TIM_HORAS, found.mHoraTotal.horas)
                                .withValue(TSContract.RegistroHora.COL_TIM_MINUTOS, found.mHoraTotal.minutos)
                                .withValue(TSContract.RegistroHora.COL_FECHA_INSERT, dateFormat.format(found.mFechaInsert))
                                .withValue(TSContract.RegistroHora.COL_FECHA_ING, dateFormat.format(found.mFechaIng))
                                .withValue(TSContract.RegistroHora.COL_OFFLINE, found.mOffLine)
                                .withValue(TSContract.RegistroHora.COL_ESTADO, found.mEstado.value)
                                .withValue(TSContract.RegistroHora.COL_ABO_ID, found.mAbogadoId)
                                .build())
                    }
                }
                else
                {
                    batch.add(ContentProviderOperation.newDelete(TSContract.RegistroHora.CONTENT_URI)
                            .withSelection(TSContract.RegistroHora.COL_ID + "=" + correlativo, null)
                            .build())
                }
                c.moveToNext()
            }

            c.close()

            for (item in listRegistroHoras.values) {
                batch.add(ContentProviderOperation.newInsert(TSContract.RegistroHora.CONTENT_URI)
                        .withValue(TSContract.RegistroHora.COL_TIM_CORREL, item.mCorrelativo)
                        .withValue(TSContract.RegistroHora.COL_PRO_ID, item.mProyectoId)
                        .withValue(TSContract.RegistroHora.COL_TIM_ASUNTO, item.mAsunto)
                        .withValue(TSContract.RegistroHora.COL_TIM_HORAS, item.mHoraTotal.horas)
                        .withValue(TSContract.RegistroHora.COL_TIM_MINUTOS, item.mHoraTotal.minutos)
                        .withValue(TSContract.RegistroHora.COL_FECHA_ING,  dateFormat.format(item.mFechaIng))
                        .withValue(TSContract.RegistroHora.COL_FECHA_HORA_INICIO, "${item.mInicio.horas}:${item.mInicio.minutos}")
                        .withValue(TSContract.RegistroHora.COL_FECHA_INSERT, dateFormat.format(item.mFechaInsert))
                        .withValue(TSContract.RegistroHora.COL_FECHA_ULT_MOD, dateFormat.format(item.mFechaUpdate))
                        .withValue(TSContract.RegistroHora.COL_OFFLINE, item.mOffLine)
                        .withValue(TSContract.RegistroHora.COL_ESTADO, item.mEstado.value)
                        .withValue(TSContract.RegistroHora.COL_ABO_ID, item.mAbogadoId)
                        .build()
                )
            }
            this.mContentResolver.applyBatch(TSContract.AUTHORITY, batch)
            this.mContentResolver.notifyChange(TSContract.RegistroHora.CONTENT_URI, null, false)
        }
    }

    private fun syncNewsProyectos(authToken: String)
    {

        Log.i(TAG, "Fetching local entries...")
        var batch = ArrayList<ContentProviderOperation>()
        val lstProyectos = ApiClient.getListProjectsV2(authToken)

        if (lstProyectos != null)
        {

            var c = this.mContentResolver.query(TSContract.Proyecto.CONTENT_URI, TSContract.Proyecto.PROJECTION_PROYECTO, null,  null, "")
            assert( c != null)
            c.moveToFirst()
            while(!c.isAfterLast){

                val id = c.getString(c.getColumnIndex(TSContract.Proyecto.COL_ID))
                val dateUpdate = dateFormat.parse(c.getString(c.getColumnIndex(TSContract.Proyecto.COL_FECHA_ULT_MOD)))

                val found = lstProyectos.get(id)
                if (found != null)
                {
                    lstProyectos.remove(id)
                    if (dateUpdate < found.fechaUpdate)
                    {
                        batch.add(ContentProviderOperation.newUpdate(TSContract.Proyecto.CONTENT_URI)
                                .withSelection( TSContract.Proyecto.COL_ID + "=" + id, null)
                                .withValue(TSContract.Proyecto.COL_NOMBRE, found.nombre)
                                .withValue(TSContract.Proyecto.COL_CLI_COD, found.cliente.codigo)
                                .withValue(TSContract.Proyecto.COL_CLI_NOM, found.cliente.nombre)
                                .withValue(TSContract.Proyecto.COL_ESTADO, found.estado)
                                .withValue(TSContract.Proyecto.COL_IDIOMA, found.cliente.idioma)
                                .withValue(TSContract.Proyecto.COL_FECHA_ULT_MOD, dateFormat.format(found.fechaUpdate))
                                .build())
                    }
                }
                else
                {
                    batch.add(ContentProviderOperation.newDelete(TSContract.Proyecto.CONTENT_URI)
                            .withSelection(TSContract.Proyecto.COL_ID + "=" + id, null )
                            .build())
                }

                c.moveToNext()
            }

            c.close()

            for (item in lstProyectos.values)
            {
                batch.add(ContentProviderOperation.newInsert(TSContract.Proyecto.CONTENT_URI)
                        .withValue(TSContract.Proyecto.COL_ID, item.id)
                        .withValue(TSContract.Proyecto.COL_NOMBRE, item.nombre)
                        .withValue(TSContract.Proyecto.COL_CLI_COD, item.cliente.codigo)
                        .withValue(TSContract.Proyecto.COL_CLI_NOM, item.cliente.nombre)
                        .withValue(TSContract.Proyecto.COL_ESTADO, item.estado)
                        .withValue(TSContract.Proyecto.COL_IDIOMA, item.cliente.idioma)
                        .withValue(TSContract.Proyecto.COL_FECHA_ULT_MOD,  dateFormat.format(item.fechaUpdate))
                        .build()
                )
            }

            this.mContentResolver.applyBatch(TSContract.AUTHORITY, batch)
            this.mContentResolver.notifyChange(TSContract.Proyecto.CONTENT_URI,null, false)
        }
    }

}