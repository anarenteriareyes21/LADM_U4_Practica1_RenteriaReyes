package mx.edu.ittepic.ladm_u4_practica1_renterireyes

import android.content.Context
import android.widget.Toast


class Hilo(p: MainActivity) : Thread() {
    var punteromain = p
    var tamActual = 0
    var tamTemp = 0
    var contador = 0

    override fun run() {
        super.run()

        while (true){
            sleep(2000)
                punteromain.runOnUiThread {
                    tamTemp = punteromain.tamañoLista
                    punteromain.registroLlamadas()
                  tamActual = punteromain.tamañoLista
                    //Toast.makeText(punteromain,"${tamTemp}     ${tamActual} ",Toast.LENGTH_LONG).show()
                        if (tamActual > tamTemp) {
                            if (contador == 0){
                               // Toast.makeText(punteromain, "PRIMERA VEZ", Toast.LENGTH_SHORT).show()
                                ++contador
                            }else {
                                //MANDAR LLAMAR EL DE COMPARAR NUMEROS
                                Toast.makeText(punteromain, "${tamTemp}     ${tamActual} ", Toast.LENGTH_SHORT).show()
                                Toast.makeText(punteromain, "HAY UNA NUEVA LLAMADA", Toast.LENGTH_SHORT).show()
                                punteromain.verificarLista()
                            }

                        } else {
                            //Toast.makeText(punteromain, "TODAVIA ESTAN LAS MISMAS LLAMADAS", Toast.LENGTH_SHORT).show()
                        }

                }
        }
    }

}// class

