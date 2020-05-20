package mx.edu.ittepic.ladm_u4_practica1_renterireyes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_configurar.*
import kotlinx.android.synthetic.main.activity_ver_contactos.*

class VerContactos : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var listaContactos = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_contactos)
        /*-------------------------------------- RECUPERAR TODOS LOS CONTACTOS---------------------------*/
        baseRemota.collection("contactos")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                for (document in querySnapshot!!){
                    var cadena = "Nombre: " + document.getString( "nombre") +"\n" + "Telefono: " + document.getString("telefono") + "\n" + "Tipo: " + document.getString("tipo")
                    listaContactos.add(cadena)
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,listaContactos)
                lista.adapter = adaptador
            }
    }
}
