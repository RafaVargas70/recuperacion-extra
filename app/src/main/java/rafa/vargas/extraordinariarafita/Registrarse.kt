package rafa.vargas.extraordinariarafita

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class Registrarse: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        val txtNombreRegistro = findViewById<EditText>(R.id.txtNombreRegistro)
        val txtApellidoRegistro = findViewById<EditText>(R.id.txtApellidoRegistro)
        val txtCorreoRegistro = findViewById<EditText>(R.id.txtCorreoRegistro)
        val txtPasswordRegistro = findViewById<EditText>(R.id.txtPasswordRegistro)
        val txtEdadRegistro = findViewById<EditText>(R.id.txtEdadRegistro)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)
        val btnRegresarLogin = findViewById<Button>(R.id.btnRegresarLogin)


        fun hashSHA256(contraencriptada: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contraencriptada.toByteArray())
            return bytes.joinToString ("") { "%02x".format(it) }
        }


        btnCrearCuenta.setOnClickListener {
            println("CLIC")

            val nombre = txtNombreRegistro.text.toString()
            val apellido = txtApellidoRegistro.text.toString()
            val correo = txtCorreoRegistro.text.toString()
            val contraencriptada = hashSHA256(txtPasswordRegistro.text.toString())
            val edad = txtEdadRegistro.text.toString()

            var hayErrores = false

            if (nombre.isEmpty()) {
                txtNombreRegistro.error = "El nombre es obligatorio"
                hayErrores = true
            } else {
                txtNombreRegistro.error = null
            }

            if (apellido.isEmpty()) {
                txtApellidoRegistro.error = "El apellido es obligatoria"
                hayErrores = true
            } else {
                txtApellidoRegistro.error = null
            }

            if (correo.isEmpty()) {
                txtCorreoRegistro.error = "El correo es obligatorio"
                hayErrores = true
            } else {
                txtCorreoRegistro.error = null
            }

            if (contraencriptada.isEmpty()) {
                txtPasswordRegistro.error = "La contraseña es obligatoria"
                hayErrores = true
            } else {
                txtPasswordRegistro.error = null
            }

            if (edad.isEmpty()) {
                txtEdadRegistro.error = "La edad es obligatoria"
                hayErrores = true
            } else {
                txtEdadRegistro.error = null
            }

            if (!edad.matches(Regex("[0-9]+"))) {
                txtEdadRegistro.error = "La edad no tiene un formato valido"
                hayErrores = true

            }

            if (!correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                txtCorreoRegistro.error = "El correo no tiene un formato valido"
                hayErrores = true
            } else {
                txtCorreoRegistro.error = null
            }

            if (contraencriptada.length <= 8) {
                txtPasswordRegistro.error = "La contraseña debe tener mas de 8 caracteres"
                hayErrores = true
            } else {
                txtPasswordRegistro.error = null
            }


if(hayErrores){
    println("hay errores")
}else{
    println("se dio clic antes de la corrutina")
    GlobalScope.launch(Dispatchers.IO) {
        //Creo un objeto de la clase conexion
        val objConexion = ClaseConexion().cadenaConexion()
        println("antes del insert")
        //Creo una variable que contenga un PrepareStatement
        val crearUsuario =
            objConexion?.prepareStatement("INSERT INTO REGYLOG (NOMBRE, APELLIDOS, CORREO_ELECTRONICO, CONTRASEÑA, EDAD) VALUES (?, ?, ?, ?, ?)")!!
        crearUsuario.setString(1, txtNombreRegistro.text.toString())
        crearUsuario.setString(2, txtApellidoRegistro.text.toString())
        crearUsuario.setString(3, txtCorreoRegistro.text.toString())
        crearUsuario.setString(4, contraencriptada)
        crearUsuario.setString(5, txtEdadRegistro.text.toString())
        crearUsuario.executeUpdate()
        withContext(Dispatchers.Main) {
            //Abro otra corrutina o "Hilo" para mostrar un mensaje y limpiar los campos
            //Lo hago en el Hilo Main por que el hilo IO no permite mostrar nada en pantalla
            Toast.makeText(this@Registrarse, "Usuario creado", Toast.LENGTH_SHORT)
                .show()
            txtNombreRegistro.setText("")
            txtApellidoRegistro.setText("")
            txtCorreoRegistro.setText("")
            txtPasswordRegistro.setText("")
            txtEdadRegistro.setText("")
        }
    }

    val pantallaLogin = Intent(this, Login::class.java)
    startActivity(pantallaLogin)

}



        }

        btnRegresarLogin.setOnClickListener {
            val pantallaLogin = Intent(this, Login::class.java)
            startActivity(pantallaLogin)
        }

    }
}