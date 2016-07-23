package com.futurice.freesound.feature.home;

import com.futurice.freesound.R;
import com.futurice.freesound.app.FreesoundApplication;
import com.futurice.freesound.core.BaseActivity;
import com.futurice.freesound.inject.activity.BaseActivityModule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

public class HomeActivity extends BaseActivity<HomeActivityComponent> {

    @Inject
    @Nullable
    HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
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

    @NonNull
    @Override
    protected HomeActivityComponent createComponent() {
        return DaggerHomeActivityComponent.builder()
                                          .freesoundApplicationComponent(
                                                  ((FreesoundApplication) this
                                                          .getApplication()).component())
                                          .baseActivityModule(new BaseActivityModule(this))
                                          .homeActivityModule(new HomeActivityModule())
                                          .build();
    }

    @Override
    public void inject() {
        component().inject(this);
    }
}
