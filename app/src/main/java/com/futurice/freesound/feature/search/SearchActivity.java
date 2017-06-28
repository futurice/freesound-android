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

import com.futurice.freesound.R;
import com.futurice.freesound.app.FreesoundApplication;
import com.futurice.freesound.core.BindingBaseActivity;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.inject.activity.BaseActivityModule;
import com.futurice.freesound.viewmodel.DataBinder;
import com.futurice.freesound.viewmodel.SimpleDataBinder;
import com.futurice.freesound.viewmodel.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import polanski.option.Option;

import static butterknife.ButterKnife.findById;
import static com.futurice.freesound.common.utils.Preconditions.checkNotNull;
import static com.futurice.freesound.common.utils.Preconditions.get;
import static timber.log.Timber.e;

public class SearchActivity extends BindingBaseActivity<SearchActivityComponent> {

    @Inject
    SearchActivityViewModel searchViewModel;

    @Inject
    SearchSnackbar searchSnackbar;

    @Inject
    SchedulerProvider schedulerProvider;

    @BindView(R.id.search_view)
    SearchView searchView;

    @BindView(R.id.search_close_btn)
    ImageView closeButton;

    @BindView(R.id.search_coordinatorlayout)
    CoordinatorLayout coordinatorLayout;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {

        @NonNull
        private Observable<String> getTextChangeStream(@NonNull final SearchView view,
                                                       @NonNull final Scheduler uiScheduler) {
            return Observable.<String>create(e -> subscribeToSearchView(view, e))
                    .subscribeOn(uiScheduler);
        }

        @Override
        public void bind(@NonNull final CompositeDisposable d) {
            checkNotNull(searchViewModel, "View Model cannot be null.");
            checkNotNull(searchView, "Search view cannot be null.");
            checkNotNull(schedulerProvider, "Scheduler Provider cannot be null.");

            d.add(searchViewModel.isClearEnabledOnceAndStream()
                                 .observeOn(schedulerProvider.ui())
                                 .subscribe(SearchActivity.this::setClearSearchVisible,
                                            e -> e(e, "Error setting query string")));

            d.add(getTextChangeStream(searchView, schedulerProvider.ui())
                          .observeOn(schedulerProvider.computation())
                          .subscribe(searchViewModel::search,
                                     e -> e(e, "Error getting changed text")));

            d.add(searchViewModel.getSearchStateOnceAndStream()
                                 .observeOn(schedulerProvider.ui())
                                 .subscribe(SearchActivity.this::handleErrorState,
                                            e -> e(e, "Error receiving Errors")));
        }

        private void subscribeToSearchView(@NonNull final SearchView view,
                                           @NonNull final ObservableEmitter<String> emitter) {
            view.setOnQueryTextListener(new OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(final String newText) {
                    emitter.onNext(get(newText));
                    return true;
                }
            });
        }

    };

    public static void open(@NonNull final Context context) {
        checkNotNull(context, "Launching context cannot be null");

        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Option.ofObj(savedInstanceState)
              .ifNone(this::addSearchFragment);

        Toolbar toolbar = findById(this, R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView.setIconified(false);

        searchView.setOnCloseListener(() -> {
            searchView.setQuery(SearchActivityViewModel.NO_SEARCH, true);
            return true;
        });

    }

    @NonNull
    @Override
    protected ViewModel viewModel() {
        return get(searchViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

    @Override
    public void inject() {
        component().inject(this);
    }

    @NonNull
    @Override
    protected SearchActivityComponent createComponent() {
        return ((FreesoundApplication) getApplication())
                .component().plusSearchActivityComponent(new BaseActivityModule(this));
    }

    @Override
    public void onPause() {
        dismissSnackbar();
        super.onPause();
    }

    private void addSearchFragment() {
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.container, SearchFragment.create())
                                   .commit();
    }

    private void handleErrorState(@NonNull final SearchState searchState) {
        searchState.error()
                   .ifSome(__ -> showSnackbar(getString(R.string.search_error)))
                   .ifNone(this::dismissSnackbar);
    }

    private void setClearSearchVisible(final boolean isClearButtonVisible) {
        closeButton.setVisibility(isClearButtonVisible ? View.VISIBLE : View.GONE);
    }

    private void showSnackbar(@NonNull final CharSequence charSequence) {
        checkNotNull(charSequence);
        searchSnackbar.showNewSnackbar(coordinatorLayout, charSequence);
    }

    private void dismissSnackbar() {
        searchSnackbar.dismissSnackbar();
    }
}
