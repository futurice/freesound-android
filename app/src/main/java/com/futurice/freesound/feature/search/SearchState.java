package com.futurice.freesound.feature.search;

import com.google.auto.value.AutoValue;

import com.futurice.freesound.network.api.model.Sound;

import android.support.annotation.NonNull;

import java.util.List;

import polanski.option.Option;

@AutoValue
abstract class SearchState {

    @NonNull
    abstract Option<List<Sound>> results();

    @NonNull
    abstract Option<Throwable> error();

    abstract boolean isInProgress();

    static SearchState idle(){
        return new AutoValue_SearchState(Option.none(), Option.none(), false);
    }

    static SearchState inProgress(){
        return new AutoValue_SearchState(Option.none(), Option.none(), true);
    }

    static SearchState error(@NonNull Throwable throwable){
        return new AutoValue_SearchState(Option.none(), Option.ofObj(throwable), false);
    }

    static SearchState success(@NonNull List<Sound> results){
        return new AutoValue_SearchState(Option.ofObj(results), Option.none(), false);
    }
}
