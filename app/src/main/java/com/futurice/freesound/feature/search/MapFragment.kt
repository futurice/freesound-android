package com.futurice.freesound.feature.search

import android.os.Bundle
import com.futurice.freesound.inject.fragment.BaseFragmentModule
import com.futurice.freesound.map.BindingBaseMapViewFragment
import com.futurice.freesound.viewmodel.DataBinder
import com.futurice.freesound.viewmodel.SimpleDataBinder
import com.futurice.freesound.viewmodel.ViewModel
import javax.inject.Inject

class MapFragment : BindingBaseMapViewFragment<MapFragmentComponent>() {

    @Inject
    internal lateinit var simpleMapViewViewModel: MapViewModel

    private val dataBinder = SimpleDataBinder()

    override fun inject() {
        component().inject(this)
    }

    override fun createComponent(): MapFragmentComponent =
            (activity as SearchActivity).component()
                    .plusMapFragmentComponent(BaseFragmentModule(this))

    override fun viewModel(): ViewModel = simpleMapViewViewModel

    override fun dataBinder(): DataBinder = dataBinder

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.getMapAsync(simpleMapViewViewModel)
    }

    companion object {
        internal fun create(): MapFragment = MapFragment()
    }
}
