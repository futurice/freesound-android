/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.map

import com.futurice.freesound.network.api.model.GeoLocation
import com.futurice.freesound.viewmodel.BaseViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

internal abstract class SimpleMapViewViewModel : BaseViewModel(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val mapReady
        get() = checkMapReady()

    private fun checkMapReady() = ::googleMap.isInitialized

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    protected fun zoomToMarker(geoLocation: GeoLocation) {
        if (mapReady) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(geoLocation.latitude, geoLocation.longitude), 15.0f))
        }
    }

    protected fun wipeMarkers() {
        if (mapReady)
            googleMap.clear()
    }

    protected fun addMarker(geoLocation: GeoLocation, title: String) {
        if (mapReady)
            googleMap.addMarker(MarkerOptions()
                    .position(LatLng(geoLocation.latitude, geoLocation.longitude))
                    .title(title))
    }
}
