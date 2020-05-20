package mx.edu.ittepic.ladm_u4_practica1_renterireyes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_configurar.*

class ConfigurarActivity : AppCompatActivity() {
    var listaMensajes = ArrayList<String>()
    var baseRemota = FirebaseFirestore.getInstance()
    var listaMensajesDeseados = ArrayList<String>()
    var listaMensajesIndeseados = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar)
       /*---------------- RECUPERAR LOS MENSAJES EXISTENTES--------------------*/
        recuperarMensajes()

        /*---------------- GUARDAR MENSAJES-------------------------*/
        btnSaveMnsDeseado.setOnClickListener {
            guardarMensaje(txtDeseados.text.toString(),1)
        }
        btnSaveMnsIndeseado.setOnClickListener {
            guardarMensaje(txtIndeseados.text.toString(),2)
        }
    }

    private fun guardarMensaje( texto : String, tipo : Int) {
        when(tipo){
            1->{
                /*------------------- MENSAJE DE TIPO CONTACTO DESEADO-----------------*/
               var data = hashMapOf(
                    "mensaje" to texto,
                    "tipo_contacto" to "DESEADO"
                )
                baseRemota.collection("mensajes")
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "MENSAJE GUARDADO EXITOSAMENTE PARA CONTACTOS DESEADOS", Toast.LENGTH_LONG).show()

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "ERROR AL GUARDAR EL MENSAJE PARA CONTACTOS DESEADOS", Toast.LENGTH_LONG).show()
                    }
            }
            2->{
                /*------------------- MENSAJE DE TIPO CONTACTO INDESEADO-----------------*/
                var data = hashMapOf(
                    "mensaje" to texto,
                    "tipo_contacto" to "INDESEADO"
                )
                baseRemota.collection("mensajes")
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "MENSAJE GUARDADO EXITOSAMENTE PARA CONTACTOS INDESEADOS", Toast.LENGTH_LONG).show()

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "ERROR AL GUARDAR EL MENSAJE PARA CONTACTOS INDESEADOS", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    fun recuperarMensajes(){
        /*-------------------------------------- RECUPERAR TODOS LOS MENSAJES---------------------------*/
            baseRemota.collection("mensajes")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    for (document in querySnapshot!!){
                        var cadena =  "ID:" +  document.id  + "Mensaje: " + document.getString( "mensaje") +"\n" + "TIPO MENSAJE: " + document.getString("tipo_contacto")
                        listaMensajes.add(cadena)
                    }
                }
        /*----------------------------- RECUPERAR DESEADOS---------------------*/
        baseRemota.collection("mensajes").whereEqualTo("tipo_contacto","DESEADO")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                listaMensajesDeseados.clear()
                for (document in querySnapshot!!){
                    var cadena = document.getString( "mensaje").toString()
                    listaMensajesDeseados.add(cadena)
                }
                var adaptadorD = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,listaMensajesDeseados)
                listViewDeseados.adapter = adaptadorD
            }
            listViewDeseados.setOnItemClickListener { parent, view, position, id ->
                txtDeseados.setText(listaMensajesDeseados[position])
            }
        /*-------------------------- RECUPERAR INDESEADOS-----------------*/
        baseRemota.collection("mensajes").whereEqualTo("tipo_contacto","INDESEADO")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                listaMensajesIndeseados.clear()
                for (document in querySnapshot!!){
                    var cadena = document.getString( "mensaje").toString()
                    listaMensajesIndeseados.add(cadena)
                }
                var adaptadorI = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,listaMensajesIndeseados)
                listViewIndeseados.adapter = adaptadorI
            }
        listViewIndeseados.setOnItemClickListener { parent, view, position, id ->
            txtIndeseados.setText(listaMensajesIndeseados[position])
        }

    }

}
