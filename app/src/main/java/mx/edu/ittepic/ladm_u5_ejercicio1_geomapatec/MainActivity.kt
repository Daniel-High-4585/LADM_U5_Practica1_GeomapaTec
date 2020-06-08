package mx.edu.ittepic.ladm_u5_ejercicio1_geomapatec

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()//obtener acceso a la base de datos
    var posicion = ArrayList<Data>()
    lateinit var locacion : LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        baseRemota.collection("tecnologico").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if(firebaseFirestoreException != null){
                textView.setText("error: "+firebaseFirestoreException!!.message)
                return@addSnapshotListener
            }

            var resultado = ""
            posicion.clear()
6
            for(document in querySnapshot!!){
                var data = Data()
                data.nombre = document.getString("nombre").toString()
                data.posicion1 = document.getGeoPoint("posicion1")!!
                data.posicion2 = document.getGeoPoint("posicion2")!!
                resultado += data.toString()+"\n\n"
                posicion.add(data)
            }

            textView.setText(resultado)
        }

        button.setOnClickListener {
            miUltimaUbicacion()
        }

        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente=Oyente(this)
        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,01f,oyente)
        //cuando el gps se de cuenta de que hay un cambio, mando a llamar el oyente
    }

    private fun miUltimaUbicacion() {
        //regresa la ultima localizacion conocida
        //tarea, bucar la formad e buscar la ubicacion gps
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
            var geoPosicion =GeoPoint(it.latitude,it.longitude)
            textView2.setText("${it.latitude},${it.longitude}")
            for(item in posicion){
                if(item.estoyEn(geoPosicion)){
                    AlertDialog.Builder(this).setMessage("Usted se encuentra en: "+item.nombre).setTitle("Atencion").setPositiveButton("ok"){p,q->}.show()
                    //textView3.setText("Usted se encuentra en: ${item.nombre}")
                }
            }
        }.addOnFailureListener {
            textView2.setText("Error al obtener ubicacion")
        }
    }
}

class Oyente(puntero:MainActivity): LocationListener {

    var p = puntero

    override fun onLocationChanged(location: Location?) {
        p.textView2.setText("${location!!.latitude},${location!!.longitude}")
        p.textView3.setText("Lugar no reconosido")
        var geoPosGPS = GeoPoint(location.latitude, location.longitude)

        for(item in p.posicion){
            if(item.estoyEn(geoPosGPS)){
                 p.textView3.setText("Estas en ${item.nombre}")
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

}
