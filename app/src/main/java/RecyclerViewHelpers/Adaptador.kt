package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.LIBRERIA
import rafa.vargas.extraordinariarafita.R
import java.util.UUID

class Adaptador(var Datos:List<LIBRERIA>): RecyclerView.Adapter<ViewHolder>() {

    fun actualizarPantalla(uuid:String, nuevoNombre: String, nuevoAutor: String, nuevoPublicacion: String, nuevoEstado: String, nuevoISBM: Int, nuevoPaginas: Int, nuevoGenero:String, nuevoEditorial: String) {
        val index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[index].nombre = nuevoNombre
        Datos[index].autor = nuevoAutor
        Datos[index].publicacion = nuevoPublicacion
        Datos[index].estado = nuevoEstado
        Datos[index].ISBM = nuevoISBM
        Datos[index].paginas = nuevoPaginas
        Datos[index].genero = nuevoGenero
        Datos[index].editorial = nuevoEditorial


        notifyDataSetChanged()
    }

    fun eliminarLibro(uuidLibro:String, posicion:Int) {

        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val deleteUsuario = objConexion?.prepareStatement("delete LIBRERIA where NOMBRE_LIBRO = ?")!!
            deleteUsuario.setString(1, uuidLibro)
            deleteUsuario.executeUpdate()

            val commit = objConexion.prepareStatement("Commit")
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()

        notifyItemRemoved(posicion)

        notifyDataSetChanged()
    }

    fun actualizarLibro(uuidLibro:String, nombreLibro: String, autorLibro: String, publicacionLibro: String, estadoLibro: String, ISBMLibro: Int, paginasLibro: Int, generoLibro: String, editorialLibro: String,) {

        GlobalScope.launch(Dispatchers.IO){

            val objConexion = ClaseConexion().cadenaConexion()

            val updateLibro = objConexion?.prepareStatement("update LIBRERIA set AUTOR_LIBRO = ? where UUID_LIBRO = ?")!!

            updateLibro.setString(1, uuidLibro)
            updateLibro.setString(2, autorLibro)

            updateLibro.executeUpdate()

            val commit = objConexion.prepareStatement("Commit")
            commit.executeUpdate()


            withContext(Dispatchers.Main){
                actualizarPantalla(uuidLibro, nombreLibro, autorLibro, publicacionLibro, estadoLibro, ISBMLibro, paginasLibro, generoLibro, editorialLibro)
            }

        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = Datos[position]

        holder.txtNombre.text = item.autor

        holder.imgEliminar.setOnClickListener {
            val contexto = holder.itemView.context

            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar")
            builder.setMessage("Estas seguro que quieres eliminar, tene en cuenta que la vas a regar loco")


            builder.setPositiveButton("Si") {
                    dialog, wich ->
                eliminarLibro(item.nombre, position)
            }

            builder.setNegativeButton("No") {
                    dialog, wich ->

                dialog.dismiss()
            }

            builder.show()
        }

        holder.imgActualizar.setOnClickListener {
            val contexto = holder.itemView.context

            val builder = AlertDialog.Builder(contexto)

            builder.setTitle("Actualizar")
            builder.setMessage("¿Desea actualizar el Autor?")

            val cuadroTexto = EditText(contexto)


            cuadroTexto.setHint(item.autor)

            builder.setView(cuadroTexto)

            builder.setPositiveButton("Actualizar") {
                    dialog, wich ->

                actualizarLibro(item.uuid, item.nombre, cuadroTexto.text.toString(), item.publicacion, item.estado, item.ISBM, item.paginas, item.genero, item.editorial)
            }


            builder.setNegativeButton("Cancelar") {
                    dialog, wich ->

                dialog.dismiss()
            }

            builder.show()
        }




    }
}