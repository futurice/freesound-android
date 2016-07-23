package com.futurice.freesound.feature.search;

import com.futurice.freesound.R;
import com.futurice.freesound.app.FreesoundApplication;
import com.futurice.freesound.core.BaseActivity;
import com.futurice.freesound.inject.activity.BaseActivityModule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import polanski.option.Option;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;

public class SearchActivity extends BaseActivity<SearchActivityComponent> {

    @Inject
    SearchViewModel searchViewModel;

    @Nullable
    private SearchView searchView;

    public static void open(@NonNull final Context context) {
        checkNotNull(context, "Launching context cannot be null");
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Option.ofObj(savedInstanceState)
              .ifNone(this::addSearchFragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_about || super.onOptionsItemSelected(item);
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
