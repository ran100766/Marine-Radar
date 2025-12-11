package com.example.gps_compas

import android.view.View
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
    fun placeWindIcon(view: View, centerX: Float, centerY: Float, radius: Float, direction: Float) {
        val angleRad = Math.toRadians(direction - 90.0)
        val x = centerX + radius * cos(angleRad)
        val y = centerY + radius * sin(angleRad)

        view.x = x.toFloat() - view.width / 2
        view.y = y.toFloat() - view.height / 2
    }

}
