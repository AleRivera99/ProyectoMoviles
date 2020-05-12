package com.example.proyectoterminado

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    private lateinit var mMap: GoogleMap

    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION
    private val CODIGO_SOLICITUD_PERMISO = 100
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var callback: LocationCallback? = null
    private var listaMarcadores: ArrayList<Marker>? = null

    //Marcadpres de mapa
    private var marcadorGolden: Marker? = null
    private var marcadorPiramides: Marker? = null
    private var marcadorTorre: Marker? = null
    private var miPosicion: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = FusedLocationProviderClient(this)
        inicializarLocationRequest()
        callback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (mMap != null) {
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true
                    for (ubicacion in locationResult?.locations!!) {
                        Toast.makeText(
                            applicationContext,
                            ubicacion.latitude.toString() + "," + ubicacion.longitude.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        miPosicion = LatLng(ubicacion.latitude, ubicacion.longitude)
                        mMap.addMarker(MarkerOptions().position(miPosicion!!).title("Su ubicacion"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(miPosicion))
                    }
                }
            }
        }
    }

    private fun inicializarLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest?.interval = 999999999999999999
        locationRequest?.fastestInterval = 999999999999999999
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        cambiarEstiloMapa()
        MarcadoresEstaticos()
        crearListenres()
        prepararMarcadores()

        dibujarLineas()
    }

    private fun dibujarLineas() {
        val coordenadasLineas = PolylineOptions()
            .add(LatLng(13.6819319, -89.2651937))
            .add(LatLng(19.444104913340259, -99.14651446044444))
            .add(LatLng(19.44404092953131, -99.14057102054359))
            .add(LatLng(19.437794547975827, -99.13751095533371))
            .pattern(arrayListOf<PatternItem>(Dash(10f), Gap(20f)))
            .color(Color.CYAN)
            .width(30f)
        val coordenadasPoligonos = PolygonOptions()
            .add(LatLng(19.433383649089755, -99.1424274445801))
            .add(LatLng(19.43134426617855, -99.13905724883081))
            .add(LatLng(19.42880157493221, -99.138451404486956))
            .strokePattern(arrayListOf<PatternItem>(Dash(10f), Gap(20f)))
            .strokeColor(Color.BLUE)
            .fillColor(Color.GREEN)
            .strokeWidth(10f)

        val coordernadasCirculo = CircleOptions()
            .center(LatLng(19.434200011141158, -99.1477056965232))
            .radius(120.0)
            .strokePattern(arrayListOf<PatternItem>(Dash(10f), Gap(10f)))
            .strokeWidth(15f)
            .strokeColor(Color.WHITE)
            .fillColor(Color.YELLOW)

        mMap.addPolyline(coordenadasLineas)
        mMap.addPolygon(coordenadasPoligonos)
        mMap.addCircle(coordernadasCirculo)


    }

    private fun cambiarEstiloMapa() {
        // mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        val exitoCambioMapa =
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.estilo_mapa))


        if (!exitoCambioMapa) {
            //Mencionar que hubo un problema al cambiar el tipo de mapa
        }

    }

    private fun crearListenres() {
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMarkerDragListener(this)
    }

    private fun MarcadoresEstaticos() {
        val GOLDEN_GATE = LatLng(37.8199286, -122.4782551)
        val PIRAMIDES = LatLng(29.9772962, 31.1324855)
        val TORRE_PISA = LatLng(43.722952, 10.396597)

        marcadorGolden = mMap.addMarker(
            MarkerOptions()
                .position(GOLDEN_GATE)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Golden Gate")
        )
        marcadorGolden?.tag = 0
        marcadorPiramides = mMap.addMarker(
            MarkerOptions()
                .position(PIRAMIDES)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Piramides")
        )
        marcadorPiramides?.tag = 0
        marcadorTorre = mMap.addMarker(
            MarkerOptions()
                .position(TORRE_PISA)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                .title("Torre pisa")
        )
        marcadorTorre?.tag = 0
    }

    private fun prepararMarcadores() {
        listaMarcadores = ArrayList()
        mMap.setOnMapClickListener { location: LatLng? ->
            listaMarcadores?.add(
                mMap.addMarker(
                    MarkerOptions()
                        .position(location!!)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                        .title("Torre pisa")
                )
            )
            listaMarcadores?.last()!!.isDraggable = true

            val coordenadas = LatLng(
                listaMarcadores?.last()!!.position.latitude,
                listaMarcadores?.last()!!.position.longitude
            )

            val origen = "origin=" + miPosicion?.latitude + ", " + miPosicion?.longitude + "&"

            var destino = "destination=" + coordenadas.latitude + "," + coordenadas.longitude + "&"

            val key = "&key=" + getString(R.string.google_maps_key)

            val parametros = origen + destino + key
            Log.d("URL", "https://maps.googleapis.com/maps/api/directions/json?" + parametros);
            cargarURL("https://maps.googleapis.com/maps/api/directions/json?" + parametros)
        }
    }

    override fun onMarkerDragEnd(marcador: Marker?) {
        Toast.makeText(this, "Acabo de mover el marcador", Toast.LENGTH_LONG).show()

        Log.d("Marcador Final", marcador?.position?.latitude.toString())
        val index = listaMarcadores?.indexOf(marcador!!)
        Log.d("Marcador Final", listaMarcadores?.get(index!!)!!.position?.latitude.toString())

    }

    override fun onMarkerDragStart(marcador: Marker?) {
        Toast.makeText(this, "Empezando a mover el marcador", Toast.LENGTH_LONG).show()

        Log.d("Marcador Inicial", marcador?.position?.latitude.toString())
        val index = listaMarcadores?.indexOf(marcador!!)
        Log.d("Marcador Inicial", listaMarcadores?.get(index!!)!!.position?.latitude.toString())
    }

    override fun onMarkerDrag(marcador: Marker?) {
        title =
            marcador?.position?.latitude.toString() + " - " + marcador?.position?.longitude.toString()
    }

    override fun onMarkerClick(marcador: Marker?): Boolean {
        var numeroClicks = marcador?.tag as? Int
        if (numeroClicks != null) {
            numeroClicks++
            marcador?.tag = numeroClicks
            Toast.makeText(
                this,
                "Se han dado " + numeroClicks.toString() + "  clicks",
                Toast.LENGTH_LONG
            ).show()
        }
        return true
    }

    private fun validarPermisosUbicacion(): Boolean {
        val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(
            this,
            permisoFineLocation
        ) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(
            this,
            permisoFineLocation
        ) == PackageManager.PERMISSION_GRANTED
        return hayUbicacionPrecisa && hayUbicacionOrdinaria
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        /*   fusedLocationClient?.lastLocation?.addOnSuccessListener (this, object: OnSuccessListener<Location> {
               override fun onSuccess(location:Location?) {
                   if (location !=null) {
                       Toast.makeText(applicationContext,location?.latitude.toString()+ "-"+location?.longitude.toString(),Toast.LENGTH_LONG).show()
                   }

               }
           })*/
        fusedLocationClient?.requestLocationUpdates(locationRequest, callback, null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pedirPermiso() {
        val deboProveerContexto =
            ActivityCompat.shouldShowRequestPermissionRationale(this, permisoFineLocation)

        if (deboProveerContexto) {
            Toast.makeText(this, "Holi", Toast.LENGTH_LONG).show()
            solicitudPermiso()
        } else {
            solicitudPermiso()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun solicitudPermiso() {
        requestPermissions(
            arrayOf(permisoFineLocation, permisoCoarseLocation),
            CODIGO_SOLICITUD_PERMISO
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CODIGO_SOLICITUD_PERMISO -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    obtenerUbicacion()
                } else {
                    Toast.makeText(
                        this,
                        "No diste permiso para acceder a la ubicacion",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun detenerActualizacionUbicacion() {
        fusedLocationClient?.removeLocationUpdates(callback)
    }

    private fun cargarURL(url: String) {
        val queue = Volley.newRequestQueue(this)

        val solicitud =
            StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                Log.d("HTTP", response)

                val coordenadas = obtenerCoordenadas(response)

                mMap.addPolyline(coordenadas)


            }, Response.ErrorListener { })

        queue.add(solicitud)
    }


    private fun obtenerCoordenadas(json: String): PolylineOptions {
        val gson = Gson()
        val objeto = gson.fromJson(json, com.example.proyectoterminado.Response::class.java)
        val puntos = objeto.routes?.get(0)!!.legs?.get(0)!!.steps!!

        var coordenadas = PolylineOptions()
        for (punto in puntos) {
            coordenadas.add(punto.start_location?.toLatLng())
            coordenadas.add(punto.end_location?.toLatLng())
        }

        coordenadas.color(Color.CYAN)
            .width(15f)
        return coordenadas
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        if (validarPermisosUbicacion()) {
            obtenerUbicacion()
        } else {
            pedirPermiso()
        }
    }

    override fun onPause() {
        super.onPause()
        detenerActualizacionUbicacion()
    }
}