package com.example.gps_compas

import android.widget.ImageView
import kotlin.math.cos
import kotlin.math.sin

object ShowWind {

    /**
     * Places the wind icon around the compass circumference.
     *
     * @param windIcon The ImageView containing the SVG from drawable (wind.xml)
     * @param compassCenterX X coordinate of compass center
     * @param compassCenterY Y coordinate of compass center
     * @param radius Distance from center where icon should be placed
     * @param directionDeg Wind direction in degrees (0 = North, 90 = East, etc.)
     */
    fun placeWindIcon(
        windIcon: ImageView,
        compassCenterX: Float,
        compassCenterY: Float,
        radius: Float,
        directionDeg: Float
    ) {
        val angleRad = Math.toRadians(directionDeg.toDouble())

        val x = (compassCenterX + radius * sin(angleRad)).toFloat()
        val y = (compassCenterY - radius * cos(angleRad)).toFloat()

        windIcon.x = x - windIcon.width / 2
        windIcon.y = y - windIcon.height / 2
    }
}
