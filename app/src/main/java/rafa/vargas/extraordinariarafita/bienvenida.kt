package rafa.vargas.extraordinariarafita

import RecyclerViewHelpers.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.LIBRERIA
import java.util.UUID
import java.util.regex.Pattern

class bienvenida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenida)

        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtAutor = findViewById<EditText>(R.id.txtAutor)
        val txtPublicacion = findViewById<EditText>(R.id.txtPublicacion)
        val txtEstado = findViewById<EditText>(R.id.txtEstado)
        val txtISBM = findViewById<EditText>(R.id.txtISBM)
        val txtPaginas = findViewById<EditText>(R.id.txtPaginas)
        val txtGenero = findViewById<EditText>(R.id.txtGenero)
        val txtEditorial = findViewById<EditText>(R.id.txtEditorial)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val rcvLibros = findViewById<RecyclerView>(R.id.rcvLibros)

        rcvLibros.layoutManager = LinearLayoutManager(this)

        fun obtenerLibros(): List<LIBRERIA> {

            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM LIBRERIA")!!

            val listaLibros = mutableListOf<LIBRERIA>()

            while (resultSet.next()) {
                val uuid = resultSet.getString("UUID_LIBRO")
                val nombre = resultSet.getString("NOMBRE_LIBRO")
                val autor = resultSet.getString("AUTOR_LIBRO")
                val publicacion = resultSet.getString("ANIO_PUBLICACION")
                val estado = resultSet.getString("ESTADO_LIBRO")
                val ISBM = resultSet.getInt("ISBM")
                val paginas = resultSet.getInt("PAGINAS_LIBRO")
                val genero = resultSet.getString("GENERO_LIT")
                val editorial = resultSet.getString("EDITORIAL")

                val valoresJuntos =
                    LIBRERIA(uuid, nombre, autor, publicacion, estado, ISBM, paginas, genero, editorial)

                listaLibros.add(valoresJuntos)


            }

            return listaLibros

        }

            CoroutineScope(Dispatchers.IO).launch {
                val libroDB = obtenerLibros()
                withContext(Dispatchers.Main) {
                    val adapter = Adaptador(libroDB)
                    rcvLibros.adapter = adapter
                }
            }

            btnGuardar.setOnClickListener {
                val nombre = txtNombre.text.toString().trim()
                val autor = txtAutor.text.toString().trim()
                val publicacion = txtPublicacion.text.toString().trim()
                val estado = txtEstado.text.toString().trim()
                val ISBM = txtISBM.text.toString().trim()
                val paginas = txtPaginas.text.toString().trim()
                val genero = txtGenero.text.toString().trim()
                val editorial = txtEditorial.text.toString().trim()

                // Validaciones de los campos
                if (nombre.isEmpty()) {
                    Toast.makeText(this@bienvenida, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (autor.isEmpty()) {
                    Toast.makeText(this@bienvenida, "El autor no puede estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (publicacion.isEmpty()) {
                    Toast.makeText(this@bienvenida, "La publicacion no puede estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (estado.isEmpty()) {
                    Toast.makeText(this@bienvenida, "El estado no puede estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (ISBM.isEmpty() || !isValidInt(ISBM)) {
                    Toast.makeText(this@bienvenida, "ISBM no puede estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (paginas.isEmpty() || !isValidInt(ISBM)) {
                    Toast.makeText(this@bienvenida, "Las paginas no puede estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (genero.isEmpty()) {
                    Toast.makeText(this@bienvenida, "El genero no puede estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (editorial.isEmpty()) {
                    Toast.makeText(this@bienvenida, "la editorial no puede estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()

                        val addLibro = objConexion?.prepareStatement(
                            "INSERT INTO LIBRERIA (UUID_LIBRO, NOMBRE_LIBRO, AUTOR_LIBRO, ANIO_PUBLICACION, ESTADO_LIBRO, ISBM, PAGINAS_LIBRO, GENERO_LIT, EDITORIAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        )!!

                        addLibro.setString(1, UUID.randomUUID().toString())
                        addLibro.setString(2, nombre)
                        addLibro.setString(3, autor)
                        addLibro.setString(4, publicacion)
                        addLibro.setString(5, estado)
                        addLibro.setInt(6, ISBM.toInt())
                        addLibro.setInt(7, paginas.toInt())
                        addLibro.setString(8, genero)
                        addLibro.setString(9, editorial)

                        addLibro.executeUpdate()

                        runOnUiThread {
                            Toast.makeText(this@bienvenida, "exito", Toast.LENGTH_SHORT).show()

                            CoroutineScope(Dispatchers.IO).launch {
                                val libroDB = obtenerLibros()
                                withContext(Dispatchers.Main) {
                                    val adapter = Adaptador(libroDB)
                                    rcvLibros.adapter = adapter
                                }
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@bienvenida, "error", Toast.LENGTH_SHORT).show()
                            println("este es el error $e")
                        }
                    }
                }

            }
        }


    }



    private fun isValidInt(value: String): Boolean {
        return try {
            value.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isValidFloat(value: String): Boolean {
        return try {
            value.toFloat()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
