package com.futurice.freesound.feature.search;

import com.futurice.freesound.R;
import com.futurice.freesound.app.FreesoundApplication;
import com.futurice.freesound.core.BaseActivity;
import com.futurice.freesound.inject.activity.BaseActivityModule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import polanski.option.Option;

public class SearchActivity extends BaseActivity<SearchActivityComponent> {

    @Inject
    SearchViewModel searchViewModel;

    @Nullable
    private SearchView searchView;

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
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //   searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //  searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v == searchView && !hasFocus) {
                    searchView.setIconified(true);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                searchViewModel.search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchViewModel.clear();
                return false;
            }
        });
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
