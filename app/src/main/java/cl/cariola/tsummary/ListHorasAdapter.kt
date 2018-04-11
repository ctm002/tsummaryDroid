package cl.cariola.tsummary
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cl.cariola.tsummary.business.entities.RegistroHora
import android.content.Context

class ListHorasAdapter(val items: List<RegistroHora>, context: Context) : RecyclerView.Adapter<ListHorasAdapter.ViewHolderRegistroHoras>()
{
    val mItems = items
    val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRegistroHoras {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_registro_horas, parent, false)
        return ViewHolderRegistroHoras(view).listen { pos, type ->
            val item = mItems.get(pos)
            Log.d("Selected items", "${item.mCorrelativo}")
            val intent = Intent(this.mContext, RegistrarHoraActivity:: class.java)
            this.mContext.startActivity(intent)
        }
    }

    override fun onBindViewHolder(holder: ViewHolderRegistroHoras, position: Int) {
        holder.setCliente(this.mItems.get(position).getNombreCliente()!!)
        holder.setProyecto(this.mItems.get(position).getNombreProyecto()!!)
        holder.setHora(this.mItems.get(position).getHoraTotal())
        holder.setAsunto(this.mItems.get(position).mAsunto)
    }

    override fun getItemCount(): Int {
        return this.mItems.size
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

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(getAdapterPosition(), getItemViewType())
        }
        return this
    }
}