package com.futurice.freesound.feature.search;

import com.futurice.freesound.R;
import com.futurice.freesound.core.BaseBindingFragment;
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

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.futurice.freesound.utils.Preconditions.get;

public class SearchFragment extends BaseBindingFragment<SearchFragmentComponent> {

    private static final String TAG = SearchFragment.class.getSimpleName();

    @Nullable
    @Inject
    SearchViewModel searchViewModel;

    @Nullable
    @Inject
    SoundItemAdapter soundItemAdapter;

    @Nullable
    private RecyclerView searchResultsRecyclerView;

    @NonNull
    private final Binder binder = new Binder() {

        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            subscription.add(viewModel().getSounds()
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(SearchFragment.this::setItems,
                                                   e -> Log.e(TAG, "Error setting Sound items",
                                                              e)));
        }

        @Override
        public void unbind() {
            // Nothing to do here
        }

    };

    private void setItems(@NonNull final List<Sound> sounds) {
        Log.d(TAG, "#### Setting items: " + sounds);
        get(soundItemAdapter).setItems(get(sounds));
    }

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
        searchResultsRecyclerView = (RecyclerView) view
                .findViewById(R.id.recyclerView_searchResults);

        // TODO
        get(searchResultsRecyclerView).setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        get(searchResultsRecyclerView).setAdapter(get(soundItemAdapter));
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel().search("cats");
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

}
