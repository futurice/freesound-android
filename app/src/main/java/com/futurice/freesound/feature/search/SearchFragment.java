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
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.viewmodel.Binder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import polanski.option.AtomicOption;
import polanski.option.Option;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static com.futurice.freesound.utils.Preconditions.get;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public final class SearchFragment extends BindingBaseFragment<SearchFragmentComponent> {

    @Nullable
    @Inject
    SearchFragmentViewModel searchFragmentViewModel;

    @Nullable
    @Inject
    SoundItemAdapter soundItemAdapter;

    @Nullable
    @BindView(R.id.recyclerView_searchResults)
    RecyclerView resultsRecyclerView;

    @Nullable
    @BindView(R.id.textView_searchNoResults)
    TextView noResultsTextView;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final Binder binder = new Binder() {

        @Override
        public void bind(@NonNull final CompositeDisposable disposables) {
            disposables.add(viewModel().getSoundsStream()
                                       .subscribeOn(Schedulers.computation())
                                       .observeOn(mainThread())
                                       .subscribe(SearchFragment.this::handleResults,
                                                  e -> Timber.e(e, "Error setting Sound items")));
        }

        @Override
        public void unbind() {
            // Nothing to do here
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
        get(resultsRecyclerView).setLayoutManager(layoutManager);
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
                                            .build();
    }

    @NonNull
    @Override
    protected SearchFragmentViewModel viewModel() {
        return get(searchFragmentViewModel);
    }

    @NonNull
    @Override
    protected Binder binder() {
        return binder;
    }

    private void handleResults(@NonNull final Option<List<DisplayableItem>> sounds) {
        sounds.matchAction(this::showResults, this::showNothing);
    }

    private void showNothing() {
        get(noResultsTextView).setVisibility(View.GONE);
        get(resultsRecyclerView).setVisibility(View.GONE);
    }

    private void showResults(@NonNull final List<DisplayableItem> sounds) {
        if (get(sounds).isEmpty()) {
            get(noResultsTextView).setVisibility(View.VISIBLE);
            get(resultsRecyclerView).setVisibility(View.GONE);
        } else {
            get(noResultsTextView).setVisibility(View.GONE);
            get(resultsRecyclerView).setVisibility(View.VISIBLE);
            get(soundItemAdapter).setItems(get(sounds));
        }
    }

}
