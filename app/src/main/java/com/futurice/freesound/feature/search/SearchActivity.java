package com.futurice.freesound.feature.search;

import com.futurice.freesound.R;
import com.futurice.freesound.app.FreesoundApplication;
import com.futurice.freesound.core.BindingBaseActivity;
import com.futurice.freesound.inject.activity.BaseActivityModule;
import com.futurice.freesound.utils.Preconditions;
import com.futurice.freesound.viewmodel.Binder;
import com.futurice.freesound.viewmodel.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import javax.inject.Inject;

import polanski.option.Option;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;
import static com.futurice.freesound.utils.Preconditions.get;

public class SearchActivity extends BindingBaseActivity<SearchActivityComponent> {

    private static final String TAG = SearchActivity.class.getSimpleName();

    @Inject
    SearchViewModel searchViewModel;

    @Nullable
    private SearchView searchView;

    @Nullable
    private ImageView closeButton;

    @NonNull
    private final Binder binder = new Binder() {
        @Override
        public void bind(final CompositeSubscription subscriptions) {
            subscriptions.add(searchViewModel.getClearButtonVisibleStream()
                                             .subscribeOn(Schedulers.computation())
                                             .observeOn(AndroidSchedulers.mainThread())
                                             .subscribe(
                                                     isVisible -> setClearSearchVisible(isVisible),
                                                     err -> Log.e(TAG,
                                                                  "Error setting query string",
                                                                  err)));
        }

        @Override
        public void unbind() {
            // Nothing
        }
    };

    private void setClearSearchVisible(@NonNull final Boolean clearVisible) {
        Preconditions.checkNotNull(closeButton, "Close Button has not been bound");

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
        setContentView(R.layout.activity_search);
        Option.ofObj(savedInstanceState)
              .ifNone(this::addSearchFragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setIconified(false);
        closeButton = (ImageView) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                search(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(() -> {
            searchView.setQuery(SearchViewModel.NO_SEARCH, true);
            return true;
        });

    }

    private boolean search(final String query) {
        searchViewModel.search(query);
        return true;
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
                                                    ((FreesoundApplication) this
                                                            .getApplication()).component())
                                            .baseActivityModule(new BaseActivityModule(this))
                                            .searchActivityModule(new SearchActivityModule())
                                            .build();
    }

    private void addSearchFragment() {
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.container, SearchFragment.create())
                                   .commit();
    }

}
