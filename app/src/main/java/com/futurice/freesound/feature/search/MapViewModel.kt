package com.futurice.freesound.feature.search

import com.futurice.freesound.common.rx.plusAssign
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.map.SimpleMapViewViewModel
import com.futurice.freesound.network.api.model.Sound
import io.reactivex.disposables.CompositeDisposable
import polanski.option.Option
import timber.log.Timber

internal class MapViewModel(private val tabController: TabController,
                            private val searchDataModel: SearchDataModel,
                            private val schedulerProvider: SchedulerProvider) : SimpleMapViewViewModel() {

    override fun bind(d: CompositeDisposable) {
        d += tabController.tabRequestStream
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        { soundItem ->
                            soundItem.sound.ifSome { sound ->
                                sound.geotag?.let { zoomToMarker(it) }
                            }
                        },
                        Timber::e)

        d += searchDataModel.searchStateOnceAndStream
                .map(SearchState::results)
                .observeOn(schedulerProvider.ui())
                .subscribe(::displayMarkers,
                        Timber::e)
    }

    override fun unbind() {
        //nothing to do
    }

    private fun displayMarkers(optionalList: Option<List<Sound>>) {
        wipeMarkers()
        optionalList.ifSome { list ->
            list.forEach { sound ->
                sound.geotag?.let { addMarker(it, sound.name) }
            }
        }
    }
}
