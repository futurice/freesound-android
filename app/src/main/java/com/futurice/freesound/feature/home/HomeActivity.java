package com.futurice.freesound.feature.home;

import com.futurice.freesound.R;
import com.futurice.freesound.app.FreesoundApplication;
import com.futurice.freesound.core.BindingBaseActivity;
import com.futurice.freesound.inject.activity.BaseActivityModule;
import com.futurice.freesound.viewmodel.Binder;
import com.futurice.freesound.viewmodel.ViewModel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

import static com.futurice.freesound.utils.Preconditions.get;

public class HomeActivity extends BindingBaseActivity<HomeActivityComponent> {

    @Inject
    @Nullable
    HomeViewModel homeViewModel;

    @NonNull
    private final Binder binder = new Binder() {

        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            // Nothing
        }

        @Override
        public void unbind() {
            // Nothing to do here
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
    }

    @NonNull
    @Override
    protected ViewModel viewModel() {
        return get(homeViewModel);
    }

    @NonNull
    @Override
    protected Binder binder() {
        return binder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                return true;
            case R.id.action_search:
                get(homeViewModel).openSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
