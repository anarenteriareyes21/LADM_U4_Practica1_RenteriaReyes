package mx.edu.ittepic.ladm_u4_practica1_renterireyes

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.telecom.Call
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_perdidas.*
import java.util.jar.Manifest

class PerdidasActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var numero = ""
    var listaNumeroEncontrado = ArrayList<String>()
    var tamañoLista = 0
    var listaTemp = ArrayList<String>()


    var llamadas =
        listOf<String>(CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.TYPE).toTypedArray()

    /*------------- REQUEST CODES----------*/
    var siPermisoLlamadas = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perdidas)
        /*--------------- VERIFICAR PERMISOS------------------------------*/
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
        } else {
            registroLlamadas()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == siPermisoLlamadas && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            registroLlamadas()
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
       var datosEnPantalla = intArrayOf(R.id.textView9, R.id.textView11)
        /*------------------ HACER CONSULTAS--------------------------*/
        var cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            llamadas,
            CallLog.Calls.TYPE + " = ?",
            arrayOf<String>(tipo.toString()),
            "${CallLog.Calls.LAST_MODIFIED}"
        )

        /*---------------------- MOSTRAR LLAMADAS PERDIDAS-----------------------------*/
        var adapter = SimpleCursorAdapter(
            applicationContext,
            R.layout.llamadas_registro,
            cursor,
            datos,
            datosEnPantalla,
            0
        )
        lista.adapter = adapter
        /*-------------------- OBTENEER NUMEROS---------------*/
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




}
