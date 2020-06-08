package mx.edu.ittepic.ladm_u5_ejercicio1_geomapatec

import com.google.firebase.firestore.GeoPoint

class Data {

    var nombre : String = ""
    var posicion1: GeoPoint = GeoPoint(0.0,0.0)
    var posicion2: GeoPoint = GeoPoint(0.0,0.0)

    override fun toString():String{
        return nombre + "\n"+posicion1.latitude+", "+posicion1.longitude+"\n"+posicion2.latitude+", "+posicion2.longitude
    }

    fun estoyEn(posActual:GeoPoint):Boolean{
        //logica similar a la clase figura geometrica de canvas
        //similar al metodo estaEnArea
        if(posActual.latitude>=posicion1.latitude && posActual.latitude<=posicion2.latitude){
            if(invertir(posActual.longitude)>=invertir(posicion1.longitude) && invertir(posActual.longitude)<=invertir(posicion2.longitude)){
                return true
            }
        }


        return false
    }

    private fun invertir(valor:Double):Double{
        return valor*-1
    }
}