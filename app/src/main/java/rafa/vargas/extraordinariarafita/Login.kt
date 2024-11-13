package rafa.vargas.extraordinariarafita

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val txtCorreoLogin = findViewById<EditText>(R.id.txtCorreoLogin)
        val txtPasswordLogin = findViewById<EditText>(R.id.txtPasswordLogin)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrarme = findViewById<Button>(R.id.btnRegistrarme)

        fun hashSHA256(contraencriptada: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contraencriptada.toByteArray())
            return bytes.joinToString ("") { "%02x".format(it) }
        }

        btnIngresar.setOnClickListener {

                val correo = txtCorreoLogin.text.toString()
                val contraencriptada = hashSHA256(txtPasswordLogin.text.toString())

                var hayErrores = false

                if (correo.isEmpty()) {
                    txtCorreoLogin.error = "El correo es obligatorio"
                    hayErrores = true
                } else {
                    txtCorreoLogin.error = null
                }

                if (contraencriptada.isEmpty()) {
                    txtPasswordLogin.error = "La contraseña es obligatoria"
                    hayErrores = true
                } else {
                    txtPasswordLogin.error = null
                }

                if (!correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                    txtCorreoLogin.error = "El correo no tiene un formato valido"
                    hayErrores = true
            } else {
                txtCorreoLogin.error = null
                }

                if (contraencriptada.length <=8) {
                    txtPasswordLogin.error = "La contraseña debe tener mas de 8 caracteres"
                    hayErrores = true
                } else {
                    txtPasswordLogin.error = null
                }


                val pantallaPrincipal = Intent(this, bienvenida::class.java)

                GlobalScope.launch(Dispatchers.IO) {

                    val objConexion = ClaseConexion().cadenaConexion()

                    val comprobarUsuario =
                        objConexion?.prepareStatement("SELECT * FROM REGYLOG WHERE CORREO_ELECTRONICO = ? AND CONTRASEÑA = ?")!!
                    comprobarUsuario.setString(1, txtCorreoLogin.text.toString())
                    comprobarUsuario.setString(2, contraencriptada)
                    val resultado = comprobarUsuario.executeQuery()
                    if (resultado.next()) {
                        startActivity(pantallaPrincipal)

                        withContext(Dispatchers.Main) {
                            //Abro otra corrutina o "Hilo" para mostrar un mensaje y limpiar los campos
                            //Lo hago en el Hilo Main por que el hilo IO no permite mostrar nada en pantalla
                            Toast.makeText(this@Login, "Credenciales correctas", Toast.LENGTH_SHORT)
                                .show()
                            txtCorreoLogin.setText("")
                            txtPasswordLogin.setText("")
                        }

                    } else {
                        println("Usuario no encontrado, verifique las credenciales")
                        withContext(Dispatchers.Main) {
                            //Abro otra corrutina o "Hilo" para mostrar un mensaje y limpiar los campos
                            //Lo hago en el Hilo Main por que el hilo IO no permite mostrar nada en pantalla
                            Toast.makeText(this@Login, "Usuario no encontrado, verifique las credenciales", Toast.LENGTH_SHORT)
                                .show()
                            txtCorreoLogin.setText("")
                            txtPasswordLogin.setText("")
                        }
                    }
                }
            }

            btnRegistrarme.setOnClickListener {
                val pantallaRegistrarme = Intent(this, Registrarse::class.java)
                startActivity(pantallaRegistrarme)
            }
    }
}
