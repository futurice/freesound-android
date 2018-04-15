package com.futurice.freesound.feature.search

import com.futurice.freesound.network.api.model.Sound
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import polanski.option.Option
import javax.inject.Singleton

enum class TabType {
    RESULTS, MAP
}

data class SoundInfo(val tabType: TabType, val sound: Option<Sound>)

@Singleton
internal class TabController {
    private val currentTab: BehaviorSubject<SoundInfo> = BehaviorSubject.createDefault(SoundInfo(TabType.RESULTS, Option.none()))


    val tabRequestStream : Observable<SoundInfo>
        get() = currentTab.observeOn(Schedulers.computation())

    @Synchronized
    fun requestTab(soundInfo: SoundInfo) = currentTab.onNext(soundInfo)


}

