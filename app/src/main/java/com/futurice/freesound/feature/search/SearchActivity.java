package com.futurice.freesound.feature.search;

import com.futurice.freesound.R;
import com.futurice.freesound.app.FreesoundApplication;
import com.futurice.freesound.core.BaseActivity;
import com.futurice.freesound.inject.activity.BaseActivityModule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

public class SearchActivity extends BaseActivity<SearchActivityComponent> {

    @Nullable
    private SearchActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.container, SearchFragment.create())
                                       .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
}
