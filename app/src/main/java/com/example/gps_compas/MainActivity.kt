package com.example.gpscompass

import CompassManager
import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.gps_compas.ReferencePoint
import com.example.gps_compas.askUserName
import com.example.gps_compas.showCompasArrow
import com.example.gps_compas.showPointsOnCompas
import com.example.gps_compas.showPointsOnList
import com.example.gps_compas.ShowWind
import com.example.gps_compas.updateVisibleLines
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    companion object {
        val noName = "No_Name"
        var userName: String = noName
        var windDirection = -1f
        var angleToWind = -1f

        var windNoneActive = true  // global or class-level variable
    }

    private lateinit var compassManager: CompassManager

    public var fullLocationsList: List<NavigationResult> = emptyList()

    private lateinit var tvSpeed: TextView
    public lateinit var tvDirection: TextView
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private val uiUpdateHandler = Handler(Looper.getMainLooper())



    private val uiUpdateRunnable = object : Runnable {
        override fun run() {
            // Read latest location from service
            val location = LocationService.latestLocation
            location?.let {
                updateUI(location)
            }

            // Schedule next update in 2 seconds
            uiUpdateHandler.postDelayed(this, 5000)
        }
    }

    data class NavigationResult(
        var point: ReferencePoint,
        var distance: Float,
        var bearing: Float,
        var atPoint: Boolean = false,
        var index: Int = 0,
    )

    private var referencePoints: MutableList<ReferencePoint> = mutableListOf(
//        ReferencePoint("Jerusalem", 31.7795, 35.2339),
//        ReferencePoint("Home", 32.17062, 34.83878),

        ReferencePoint("Marina_Nahariya", 33.012278, 35.089028),
        ReferencePoint("Akko_Marina", 32.919944, 35.067778),
        ReferencePoint("Haifa_Marina", 32.815639, 35.028361),
        ReferencePoint("Marina_Herzliya", 32.165806, 34.792583),
        ReferencePoint("Marina_Tel Aviv", 32.087778, 34.766667),
        ReferencePoint("Jaffa_Port", 32.053083, 34.750222),
        ReferencePoint("Marina_Ashdod", 31.826250, 34.637528),
        ReferencePoint("Marina_Ashkelon", 31.682250, 34.555833),

        ReferencePoint("Eilat_Marina", 29.545167, 34.960019),

                // CYPRUS – Green entrance lights
        ReferencePoint("Limassol_Port", 34.6716, 33.0419),
        ReferencePoint("Larnaca_Port", 34.9116, 33.6473),
        ReferencePoint("Paphos_Port", 34.7579, 32.4088),
        ReferencePoint("Ayia_Napa_Marina", 34.9824, 34.0019),
        ReferencePoint("Zygi_Marina", 34.7337, 33.3331),

        // GREECE – Mainland
        ReferencePoint("Piraeus_Port", 37.9356, 23.6284),
        ReferencePoint("Rafina_Port", 38.0219, 24.0067),
        ReferencePoint("Lavrio_Port", 37.7144, 24.0564),
        ReferencePoint("Thessaloniki_Port", 40.6276, 22.9335),
        ReferencePoint("Volos_Port", 39.3610, 22.9436),

        // CRETE
        ReferencePoint("Heraklion_Port", 35.3443, 25.1360),
        ReferencePoint("Chania_Port", 35.5197, 24.0148),
        ReferencePoint("Rethymno_Port", 35.3729, 24.4730),
        ReferencePoint("Agios_Nikolaos_Marina", 35.1909, 25.7152),

        // IONIAN SEA
        ReferencePoint("Corfu_Port", 39.6200, 19.9190),
        ReferencePoint("Gouvia_Marina", 39.6524, 19.8486),
        ReferencePoint("Lefkada_Marina", 38.8304, 20.7096),
        ReferencePoint("Preveza_Marina", 38.9594, 20.7522),
        ReferencePoint("Zakynthos_Port", 37.7809, 20.8957),


// AEGEAN SEA
        ReferencePoint("Mykonos_Port", 37.4466, 25.3289),
        ReferencePoint("Paros_Parikia_Port", 37.0856, 25.1475),
        ReferencePoint("Naxos_Port", 37.1064, 25.3726),
        ReferencePoint("Rhodes_Mandrakia", 36.4515, 28.2276),
        ReferencePoint("Kos_Marina", 36.8897, 27.3036)


    )

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                // ✅ Permission granted → start the location service
                startLocationService()
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_LONG).show()
            }
        }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        startService(serviceIntent)
    }

    private fun stopLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)
    }

    var smoothedAzimuth = 0f
    val smoothingFactor = 0.1f  // smaller = smoother


    fun smoothAzimuth(oldAzimuth: Float, newAzimuth: Float): Float {
        var delta = newAzimuth - oldAzimuth

        // Handle wrap-around: keep delta between -180° and +180°
        if (delta > 180) delta -= 360
        if (delta < -180) delta += 360

        // Apply low-pass filter
        val result = (oldAzimuth + smoothingFactor * delta + 360) % 360
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // must match activity_main.xml


        val compassCircle = findViewById<ImageView>(R.id.compassCircle)
        compassCircle.setImageResource(R.drawable.circle)

        getWindPress(compassCircle)

        compassManager = CompassManager(this) { azimuth ->
            smoothedAzimuth = smoothAzimuth(smoothedAzimuth, azimuth)

            showCompasArrow(this, fullLocationsList, smoothedAzimuth, tvDirection)
            showPointsOnCompas(this, fullLocationsList, smoothedAzimuth)

            ShowWind.showWind(
                windDirection,
                azimuth,
                findViewById(R.id.windPointerContainer),
                findViewById(R.id.windPointerText),
                findViewById(R.id.compassCircle)
            )

        }


        tvSpeed = findViewById(R.id.tvSpeed)
        tvDirection = findViewById(R.id.tvDirection)
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)

        if (false)
        {
// Save name
            val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            prefs.edit().putString("userName", userName).apply()

// Load name in onCreate
            val savedName = prefs.getString("userName", null)
            if (savedName == null || savedName == noName) {
                askUserName(this) { name ->
                    userName = name
                    prefs.edit().putString("userName", name).apply()
                }
            } else {
                userName = savedName
            }
        }

        locationPermissionRequest.launch(locationPermissions)

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val pointsContainer = findViewById<LinearLayout>(R.id.pointsContainer)

// Add a scroll listener
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            updateVisibleLines(this, scrollView, pointsContainer, fullLocationsList)
        }
    }


    private fun updateUI(location: Location) {
        val speedMps = location.speed
        val speedKmh = speedMps * 3.6
        val speedKnots = speedMps * 1.94384

        tvSpeed.text = "Speed: %.1f knots".format(speedKnots)
//        tvDirection.text = "Direction: %.0f°".format(location.bearing)
//        tvCoords.text = "Lat: %.5f, Lng: %.5f".format(location.latitude, location.longitude)

        tvLatitude.text = "Lat: %.5f".format(location.latitude)
        tvLongitude.text = "Lng: %.5f".format(location.longitude)

        fullLocationsList = referencePoints.map { point ->
            val (distance, bearing) = CalculateDistance.calculateDistanceAndBearing(
                location.latitude,
                location.longitude,
                point.lat,
                point.lon
            )
            NavigationResult(point, distance, bearing, distance < 10F)
        }.sortedBy { it.distance }
            .onEachIndexed { index, result ->
                result.index = index
            }

//        showCompasArrow(this, fullLocationsList, location)
//        showPointsOnCompas(this,fullLocationsList, location)
        showPointsOnList(this, fullLocationsList)
    }

    override fun onStart() {
        super.onStart()
        uiUpdateHandler.post(uiUpdateRunnable) // start periodic updates
    }

    override fun onStop() {
        super.onStop()
        uiUpdateHandler.removeCallbacks(uiUpdateRunnable) // stop updates when activity stops
    }
//    private var updateJob: Job? = null

    override fun onResume() {
        super.onResume()
        compassManager.start()
        startLocationService()

//        updateJob = lifecycleScope.launch {
//            while (isActive) {
//                showPointsOnCompas(this@MainActivity, fullLocationsList)
//                delay(500)
//            }
//        }
    }

    override fun onPause() {
        super.onPause()
        compassManager.stop()
        stopLocationService()
//        updateJob?.cancel()

    }


    private fun getWindPress(compassCircle: ImageView) {

        compassCircle.setOnTouchListener { v, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {

                if (!windNoneActive) {
                    windDirection = -1f
                    // Skip this press
                    windNoneActive = true  // flip for next press
                    return@setOnTouchListener true
                }

                // Calculate tap distance from center
                val cx = v.width / 2f
                val cy = v.height / 2f
                val dx = event.x - cx
                val dy = event.y - cy
                val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                if (distance < 80f) {
                    // SAVE THE CURRENT COMPASS DIRECTION
                    windDirection = smoothedAzimuth

                    Toast.makeText(this, "Direction saved: $smoothedAzimuth°", Toast.LENGTH_SHORT).show()

                    windNoneActive = false  // next press will skip
                    return@setOnTouchListener true
                }
            }

            false
        }
    }

}

