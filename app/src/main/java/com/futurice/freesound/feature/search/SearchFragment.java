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
import com.futurice.freesound.core.BindingBaseFragment;
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.feature.common.ui.adapter.RecyclerViewAdapter;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.viewmodel.DataBinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import polanski.option.AtomicOption;
import polanski.option.Option;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;

public final class SearchFragment extends BindingBaseFragment<SearchFragmentComponent> {

    @Inject
    SearchFragmentViewModel searchFragmentViewModel;

    @Inject
    RecyclerViewAdapter searchResultAdapter;

    @Inject
    SchedulerProvider schedulerProvider;

    @BindView(R.id.recyclerView_searchResults)
    RecyclerView resultsRecyclerView;

    @BindView(R.id.textView_searchNoResults)
    TextView noResultsTextView;

    @BindView(R.id.progressBar_searchProgress)
    ProgressBar progressBar;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final DataBinder dataBinder = new DataBinder() {

        @Override
        public void bind(@NonNull final CompositeDisposable disposables) {
            disposables.add(viewModel().getSoundsOnceAndStream()
                                       .subscribeOn(schedulerProvider.computation())
                                       .observeOn(schedulerProvider.ui())
                                       .subscribe(SearchFragment.this::handleResults,
                                                  e -> Timber.e(e, "Error setting Sound items")));
            disposables.add(viewModel().getSearchStateOnceAndStream()
                                       .subscribeOn(schedulerProvider.computation())
                                       .observeOn(schedulerProvider.ui())
                                       .subscribe(SearchFragment.this::showProgress,
                                                  e -> Timber.e(e,
                                                                "Error receiving search triggered Events")));
        }

        @Override
        public void unbind() {
            viewModel().stopPlayback();
        }
    };

    @NonNull
    static SearchFragment create() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder.setIfNone(bind(this, view));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setRecycleChildrenOnDetach(true);
        resultsRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resultsRecyclerView.setAdapter(searchResultAdapter);
    }

    @Override
    public void inject() {
        component().inject(this);
    }

    @NonNull
    @Override
    protected SearchFragmentComponent createComponent() {
        return ((SearchActivity) getActivity()).component()
                                               .plusSearchFragmentComponent(
                                                       new BaseFragmentModule(this));
    }

    @NonNull
    @Override
    protected SearchFragmentViewModel viewModel() {
        return searchFragmentViewModel;
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

    private void handleResults(@NonNull final Option<List<DisplayableItem>> sounds) {
        sounds.matchAction(this::showResults, this::showNothing);
    }

    private void showNothing() {
        noResultsTextView.setVisibility(View.GONE);
        resultsRecyclerView.setVisibility(View.GONE);
    }

    private void showResults(@NonNull final List<DisplayableItem> sounds) {
        if (sounds.isEmpty()) {
            noResultsTextView.setVisibility(View.VISIBLE);
            resultsRecyclerView.setVisibility(View.GONE);
        } else {
            noResultsTextView.setVisibility(View.GONE);
            resultsRecyclerView.setVisibility(View.VISIBLE);
            searchResultAdapter.update(sounds);
        }
    }

    private void showProgress(@NonNull final SearchState searchState) {
        progressBar.setVisibility(searchState.isInProgress() ? View.VISIBLE : View.GONE);
    }

}
