package cl.cariola.tsummary
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cl.cariola.tsummary.business.entities.RegistroHora

class ListHorasAdapter(val list: List<RegistroHora>) : RecyclerView.Adapter<ListHorasAdapter.ViewHolderRegistroHoras>()
{
    val mList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRegistroHoras {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_registro_horas, parent, false)
        return ViewHolderRegistroHoras(view)
    }

    override fun onBindViewHolder(holder: ViewHolderRegistroHoras, position: Int) {
        holder.setCliente(this.mList.get(position).getNombreCliente()!!)
        holder.setProyecto(this.mList.get(position).getNombreProyecto()!!)
        holder.setHora(this.mList.get(position).getHoraTotal())
        holder.setAsunto(this.mList.get(position).mAsunto)
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

    class ViewHolderRegistroHoras(v: View?) : RecyclerView.ViewHolder(v) {

        var mTxtBoxCliente: TextView
        var mTxtBoxProyecto: TextView
        var mTxtBoxHora: TextView
        var mTxtBoxAsunto: TextView

        init
        {
            this.mTxtBoxCliente = this.itemView?.findViewById(R.id.tVCliente)!!
            this.mTxtBoxProyecto = this.itemView?.findViewById(R.id.tVProyecto)!!
            this.mTxtBoxHora = this.itemView?.findViewById(R.id.tVHora)!!
            this.mTxtBoxAsunto = this.itemView?.findViewById(R.id.tVAsunto)!!
        }

        fun setCliente(cliente: String){
            this.mTxtBoxCliente.text = cliente

        }

        fun setProyecto(proyecto: String){
            this.mTxtBoxProyecto.text = proyecto

        }

        fun setHora(hora: String)
        {
            this.mTxtBoxHora.text = hora
        }

        fun setAsunto(asunto: String)
        {
            this.mTxtBoxAsunto.text = asunto
        }
    }
}