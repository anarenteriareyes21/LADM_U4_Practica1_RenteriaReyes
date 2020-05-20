package mx.edu.ittepic.ladm_u4_practica1_renterireyes

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_configurar.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    /*------------------------- DECLARACION DE VARIABLES-----------------------------*/
    var listaMensajesDeseados = ArrayList<String>()
    var listaMensajesIndeseados = ArrayList<String>()
    var hilo : Hilo ?= null
    var baseRemota = FirebaseFirestore.getInstance()
    var numero = ""
    var listaNumeroEncontrado = ArrayList<String>()
    var tamañoLista = 0
    var listaTemp = ArrayList<String>()
    var llamadas =
        listOf<String>(CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.TYPE).toTypedArray()

    /*------------- REQUEST CODES----------*/
    var siPermisoLlamadas = 101
    val siPermisoEnviar = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*--------------- VERIFICAR PERMISOS-----------------------------------------------*/
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                Array(1) { android.Manifest.permission.READ_CALL_LOG },
                siPermisoLlamadas
            )
        }
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            //se otorga la variable siPermiso si el permiso si se otorga
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS),siPermisoEnviar)
        }
        /*---------------------------------------------------------------------------------*/

        /*--------------------- INICIAR HILO PARA VERIFICAR LLAMADAS PERDIDAS-------------*/
        hilo = Hilo(this)
        hilo!!.start()
        /*---------------------------------------------------------------------------------*/

        btnAgregarContacto.setOnClickListener {
            startActivity(Intent(this,AgregarContactoActivity:: class.java))
        }

        btnConfigurar.setOnClickListener {
            startActivity(Intent(this,ConfigurarActivity:: class.java))

        }
        btnVerContactos.setOnClickListener {
            startActivity(Intent(this,VerContactos:: class.java))
        }
        btnLlamadasPerdidas.setOnClickListener {
            startActivity(Intent(this,PerdidasActivity:: class.java))
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == siPermisoLlamadas && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //registroLlamadas()
        }

    }


    /*------------------------ RECUPERAR REGISTRO DE LLAMADAS---------------------------*/
    @SuppressLint("MissingPermission")
    fun registroLlamadas() {
        listaTemp.clear()
        var aux = ""
        var listaLlamadasPerdidas = ArrayList<String>()
        var tipo = "3"

        var datos = listOf<String>(CallLog.Calls.NUMBER, CallLog.Calls.TYPE).toTypedArray()
        //  var datosEnPantalla = intArrayOf(R.id.textView9, R.id.textView11)
        /*------------------ HACER CONSULTAS--------------------------*/
        var cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            llamadas,
            CallLog.Calls.TYPE + " = ?",
            arrayOf<String>(tipo.toString()),
            "${CallLog.Calls.LAST_MODIFIED}"
        )

        /*---------------------- MOSTRAR LLAMADAS PERDIDAS-----------------------------*/
        /*var adapter = SimpleCursorAdapter(
            applicationContext,
            R.layout.llamadas_registro,
            cursor,
            datos,
            datosEnPantalla,
            0
        )
        lista.adapter = adapter*/
        /*-------------------- OBTENER NUMEROS---------------*/
        if (cursor!!.moveToFirst()) {
            var posTelephone = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            do {
                val telephone = cursor.getString(posTelephone)
                numero = "" +  telephone
                aux += telephone
                listaLlamadasPerdidas.add(numero)
                listaTemp.add(aux)
            } while (cursor.moveToNext())
        }
        tamañoLista = listaTemp.size
        //Toast.makeText(this, numero, Toast.LENGTH_LONG).show()
    }

    /*---------------------------- COMPARAR NUMEROS------------------------------*/
    fun verificarLista(){
        baseRemota.collection("contactos").whereEqualTo("telefono",numero)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                for (document in querySnapshot!!){
                    listaNumeroEncontrado.add(document.getString("tipo")!!)
                }
                if(listaNumeroEncontrado.size > 0){
                    //Toast.makeText(this, "EL USUARIO HA SIDO ENCONTRADO", Toast.LENGTH_SHORT).show()
                    (0..listaNumeroEncontrado.size-1).forEach {
                        if(listaNumeroEncontrado[it] == "DESEADO"){
                            Toast.makeText(this, "EL USUARIO ES DESEADO", Toast.LENGTH_SHORT).show()
                            mandarMensaje(1, numero)
                        }else{
                            Toast.makeText(this, "EL USUARIO ES INDESEADO", Toast.LENGTH_SHORT).show()
                            mandarMensaje(2, numero)
                        }
                    }
                }else{
                    //Toast.makeText(this, "EL USUARIO NO HA SIDO ENCONTRADO", Toast.LENGTH_LONG).show()
                }
            }
    }

    /*-------------------------------------------------- MANDAR MENSAJES-------------------------------------------------------------*/
    fun mandarMensaje( tipo : Int, numtel : String){
        var mensaje = ""
        var tamaño = 0
        when(tipo) {
            1 -> {
                /*-----------........................ MENSAJE DESEADO.................................-------------------------------*/
                /*----------------------------- RECUPERAR DESEADOS---------------------*/
                baseRemota.collection("mensajes").whereEqualTo("tipo_contacto", "DESEADO")
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        listaMensajesDeseados.clear()
                        for (document in querySnapshot!!) {
                            var cadena = document.getString("mensaje").toString()
                            listaMensajesDeseados.add(cadena)

                        }
                        tamaño = listaMensajesDeseados.size-1
                        //Toast.makeText(this,"${tamaño}", Toast.LENGTH_SHORT).show()
                       mensaje = listaMensajesDeseados[tamaño]
                        //Toast.makeText(this,mensaje, Toast.LENGTH_SHORT).show()
                        /*------------------------------ ENVIAR MENSAJE-------------------------*/
                        SmsManager.getDefault()
                            .sendTextMessage(numtel, null, mensaje, null, null)
                        Toast.makeText(this, "Se envio sms", Toast.LENGTH_SHORT).show()
                    }
                //Toast.makeText(this,numtel, Toast.LENGTH_SHORT).show()
            }
            2 -> {
                /*-----------........................ MENSAJE INDESEADO.................................-------------------------------*/
                /*--------------- MENSAJE INDESEADO---------------------*/
                //recuperar ultimo mensaje
                baseRemota.collection("mensajes").whereEqualTo("tipo_contacto", "INDESEADO")
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        listaMensajesIndeseados.clear()
                        for (document in querySnapshot!!) {
                            var cadena = document.getString("mensaje").toString()
                            listaMensajesIndeseados.add(cadena)
                        }
                        mensaje = listaMensajesIndeseados[listaMensajesIndeseados.size-1]
                        /*------------------------------ ENVIAR MENSAJE-------------------------*/
                        SmsManager.getDefault()
                            .sendTextMessage(numero, null, mensaje, null, null)
                        Toast.makeText(this, "Se envio sms", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

}//class
