package com.example.gps_compas

import android.view.View
import android.widget.ImageView
import kotlin.math.cos
import kotlin.math.sin
import android.widget.FrameLayout
import android.widget.TextView
import com.example.gpscompass.MainActivity
import com.example.gpscompass.MainActivity.Companion.angleToWind

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

    fun showWind(
        windDirection: Float,
        azimuth: Float,
        pointerContainer: FrameLayout,
        windText: TextView,
        compassView: ImageView
    ) {

        // If no wind direction, reset pointer to center
        if (windDirection < 0) {
            compassView.post {
                val centerX = compassView.x + compassView.width / 2
                val centerY = compassView.y + compassView.height / 2

                ShowWind.placeWindIcon(
                    pointerContainer,
                    centerX,
                    centerY,
                    0f,
                    0f
                )
            }
            windText.text = ""
            return
        }

        val direction = (windDirection - azimuth + 360) % 360
        angleToWind = if (direction > 180) 360 - direction else direction
        windText.text = "${angleToWind.toInt()}°"

        compassView.post {
            val centerX = compassView.x + compassView.width / 2
            val centerY = compassView.y + compassView.height / 2
            val radius = compassView.width / 2f - 100f

            ShowWind.placeWindIcon(
                pointerContainer,
                centerX,
                centerY,
                radius,
                direction
            )
        }

        if (MainActivity.windState == MainActivity.WindState.BEEP) {
            if (angleToWind > 170f || angleToWind < 10f) {
                BeepManager.beepSeries()
            }
        }
    }

}
