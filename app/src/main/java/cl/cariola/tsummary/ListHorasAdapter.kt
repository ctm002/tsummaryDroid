package cl.cariola.tsummary

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.content.Context
import android.database.Cursor
import cl.cariola.tsummary.business.entities.RegistroHora
import cl.cariola.tsummary.provider.TSContract
import com.google.gson.Gson

class ListHorasAdapter(val cursor: Cursor, context: Context) : RecyclerView.Adapter<ListHorasAdapter.ViewHolderRegistroHoras>() {
    val mItems = cursor
    val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRegistroHoras {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_registro_horas, parent, false)

        return ViewHolderRegistroHoras(view).listen { pos, type ->

            this.mItems.moveToPosition(pos)
            var intent = Intent(this.mContext, RegistrarHoraActivity::class.java)
            val registro = RegistroHora(this.mItems)
            intent.putExtra("registro", Gson().toJson(registro))
            this.mContext.startActivity(intent)
        }
    }

    override fun onBindViewHolder(holder: ViewHolderRegistroHoras, position: Int) {
        this.mItems.moveToPosition(position)
        val registro = RegistroHora(this.mItems)
        holder.setCliente(registro.getNombreCliente()!!)
        holder.setProyecto(registro.getNombreProyecto()!!)
        holder.setAsunto(registro.mAsunto)
        holder.setCorrelativo(registro.mCorrelativo)
        holder.setHora(registro.getHoraTotal())
        holder.setHoraInicio(registro.getHoraInicio())
    }

    override fun getItemCount(): Int {
        return this.mItems.count
    }

    class ViewHolderRegistroHoras(v: View?) : RecyclerView.ViewHolder(v) {

        var mTxtBoxCliente: TextView
        var mTxtBoxProyecto: TextView
        var mTxtBoxHora: TextView
        var mTxtBoxAsunto: TextView
        var mTxtBoxHoraInicio : TextView
        var mCorrelativo: Int

        init {
            this.mTxtBoxCliente = this.itemView?.findViewById(R.id.tVCliente)!!
            this.mTxtBoxProyecto = this.itemView?.findViewById(R.id.tVProyecto)!!
            this.mTxtBoxHora = this.itemView?.findViewById(R.id.tVHora)!!
            this.mTxtBoxAsunto = this.itemView?.findViewById(R.id.tVAsunto)!!
            this.mCorrelativo = 0
            this.mTxtBoxHoraInicio = this.itemView?.findViewById(R.id.tVHoraInicio)!!
        }

        fun setCliente(_Cliente: String) {
            this.mTxtBoxCliente.text = _Cliente

        }

        fun setProyecto(_Proyecto: String) {
            this.mTxtBoxProyecto.text = _Proyecto

        }

        fun setHora(_Hora: String) {
            this.mTxtBoxHora.text = _Hora
        }

        fun setAsunto(_Asunto: String) {
            this.mTxtBoxAsunto.text = _Asunto
        }

        fun setCorrelativo(_Correlativo: Int) {
            this.mCorrelativo = _Correlativo
        }

        fun setHoraInicio(_Hora: String){
            this.mTxtBoxHoraInicio.setText(_Hora)
        }
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(getAdapterPosition(), getItemViewType())
        }
        return this
    }

}


