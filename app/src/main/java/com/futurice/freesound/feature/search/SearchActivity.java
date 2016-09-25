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

package com.futurice.freesound.feature.search;

import com.futurice.freesound.R.id;
import com.futurice.freesound.R.layout;
import com.futurice.freesound.app.FreesoundApplication;
import com.futurice.freesound.core.BindingBaseActivity;
import com.futurice.freesound.inject.activity.BaseActivityModule;
import com.futurice.freesound.viewmodel.Binder;
import com.futurice.freesound.viewmodel.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableEmitter.BackpressureMode;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import polanski.option.Option;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;
import static com.futurice.freesound.utils.Preconditions.get;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.computation;
import static timber.log.Timber.e;

public class SearchActivity extends BindingBaseActivity<SearchActivityComponent> {

    @Inject
    SearchViewModel searchViewModel;

    @Nullable
    private SearchView searchView;

    @Nullable
    private ImageView closeButton;

    @NonNull
    private final Binder binder = new Binder() {
        @Override
        public void bind(@NonNull final CompositeDisposable disposables) {
            checkNotNull(searchView, "Search view cannot be null.");

            disposables.add(searchViewModel.getClearButtonVisibleStream()
                                           .observeOn(mainThread())
                                           .subscribe(SearchActivity.this::setClearSearchVisible,
                                                      e -> e(e, "Error setting query string")));

            disposables.add(getTextChange(searchView)
                                    .observeOn(computation())
                                    .subscribe(searchViewModel::search,
                                               e -> e(e, "Error getting changed text")));
        }

        @Override
        public void unbind() {
            // Nothing
        }
    };

    private void setClearSearchVisible(final boolean clearVisible) {
        checkNotNull(closeButton, "Close Button has not been bound");

        get(closeButton).setVisibility(clearVisible ? View.VISIBLE : View.GONE);
    }

    public static void open(@NonNull final Context context) {
        checkNotNull(context, "Launching context cannot be null");

        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_search);
        Option.ofObj(savedInstanceState)
              .ifNone(this::addSearchFragment);

        Toolbar toolbar = (Toolbar) findViewById(id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = get((SearchView) findViewById(id.search_view));
        searchView.setIconified(false);
        closeButton = (ImageView) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        searchView.setOnCloseListener(() -> {
            searchView.setQuery(SearchViewModel.NO_SEARCH, true);
            return true;
        });

    }

    @NonNull
    private static Flowable<String> getTextChange(@NonNull final SearchView view) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(final FlowableEmitter<String> e) throws Exception {
                view.setOnQueryTextListener(new OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(final String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(final String newText) {
                        e.onNext(get(newText));
                        return true;
                    }
                });
            }
        }, BackpressureMode.LATEST)
                       .subscribeOn(mainThread());
    }

    @NonNull
    @Override
    protected ViewModel viewModel() {
        return get(searchViewModel);
    }

    @NonNull
    @Override
    protected Binder binder() {
        return binder;
    }

    @Override
    public void inject() {
        component().inject(this);
    }

    @NonNull
    @Override
    protected SearchActivityComponent createComponent() {
        return DaggerSearchActivityComponent.builder()
                                            .freesoundApplicationComponent(
                                                    ((FreesoundApplication) getApplication())
                                                            .component())
                                            .baseActivityModule(new BaseActivityModule(this))
                                            .build();
    }

    private void addSearchFragment() {
        getSupportFragmentManager().beginTransaction()
                                   .add(id.container, SearchFragment.create())
                                   .commit();
    }

}
