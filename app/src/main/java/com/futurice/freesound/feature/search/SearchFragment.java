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
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.viewmodel.Binder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import polanski.option.Option;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.futurice.freesound.utils.Preconditions.get;

public final class SearchFragment extends BindingBaseFragment<SearchFragmentComponent> {

    private static final String TAG = SearchFragment.class.getSimpleName();

    @Nullable
    @Inject
    SearchViewModel searchViewModel;

    @Nullable
    @Inject
    SoundItemAdapter soundItemAdapter;

    @Nullable
    private RecyclerView resultsRecyclerView;

    @Nullable
    private TextView noResultsTextView;

    @NonNull
    private final Binder binder = new Binder() {

        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            subscription.add(viewModel().getSounds()
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(SearchFragment.this::handleResults,
                                                   e -> Log.e(TAG, "Error setting Sound items",
                                                              e)));
        }

        @Override
        public void unbind() {
            // Nothing to do here
        }

    };

    @NonNull
    public static SearchFragment create() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resultsRecyclerView = (RecyclerView) view
                .findViewById(R.id.recyclerView_searchResults);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setRecycleChildrenOnDetach(true);
        get(resultsRecyclerView).setLayoutManager(layoutManager);
        noResultsTextView = (TextView) view.findViewById(R.id.textView_searchNoResults);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        get(resultsRecyclerView).setAdapter(get(soundItemAdapter));
    }

    @Override
    public void inject() {
        component().inject(this);
    }

    @NonNull
    @Override
    protected SearchFragmentComponent createComponent() {
        return DaggerSearchFragmentComponent.builder()
                                            .searchActivityComponent(
                                                    ((SearchActivity) getActivity()).component())
                                            .baseFragmentModule(new BaseFragmentModule(this))
                                            .searchFragmentModule(new SearchFragmentModule())
                                            .build();
    }

    @NonNull
    @Override
    protected SearchViewModel viewModel() {
        return get(searchViewModel);
    }

    @NonNull
    @Override
    protected Binder binder() {
        return binder;
    }

    private void handleResults(@NonNull final Option<List<Sound>> sounds) {
        sounds.matchAction(this::showResults, this::showNothing);
    }

    private void showNothing() {
        get(noResultsTextView).setVisibility(View.GONE);
        get(resultsRecyclerView).setVisibility(View.GONE);
    }

    private void showResults(@NonNull final List<Sound> sounds) {
        if (sounds.isEmpty()) {
            get(noResultsTextView).setVisibility(View.VISIBLE);
            get(resultsRecyclerView).setVisibility(View.GONE);
        } else {
            get(noResultsTextView).setVisibility(View.GONE);
            get(resultsRecyclerView).setVisibility(View.VISIBLE);
            get(soundItemAdapter).setItems(get(sounds));
        }
    }

}
