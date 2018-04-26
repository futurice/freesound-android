package com.futurice.freesound.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.futurice.freesound.R
import com.futurice.freesound.core.BindingBaseFragment
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback

abstract class BindingBaseMapViewFragment<T> : BindingBaseFragment<T>() {

    private var map: MapView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_map, container, false)

        map = view?.findViewById(R.id.mapview)
        map?.onCreate(savedInstanceState)

        return view
    }

    fun getMapAsync(onMapReadyCallback: OnMapReadyCallback) = map?.getMapAsync(onMapReadyCallback)

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        map?.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        map?.onResume()
    }

    override fun onPause() {
        super.onPause()
        map?.onPause()
    }

    override fun onStart() {
        super.onStart()
        map?.onStart()
    }

    override fun onStop() {
        super.onStop()
        map?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        map?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map?.onLowMemory()
    }
}