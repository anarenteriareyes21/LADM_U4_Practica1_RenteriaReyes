package mx.edu.ittepic.ladm_u4_practica1_renterireyes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_agregar_contacto.*

class AgregarContactoActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_contacto)

        btnGuardar.setOnClickListener {
            var tipoUsuario = ""
            if(radioDeseado.isChecked){
                tipoUsuario = "DESEADO"
            }
            if (radioIndeseado.isChecked){
                tipoUsuario = "INDESEADO"
            }
            var data = hashMapOf(
                "nombre" to txtNombre.text.toString(),
                "telefono" to txtNumero.text.toString(),
                "tipo" to tipoUsuario
            )

            baseRemota.collection("contactos")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "CONTACTO GUARDADO EXITOSAMENTE", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "ERROR AL GUARDAR EL CONTACTO", Toast.LENGTH_LONG).show()

                }
        }
    }


}
