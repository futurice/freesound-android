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

package com.futurice.freesound.feature.home;

import com.futurice.freesound.R;
import com.futurice.freesound.core.BindingBaseFragment;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.utils.Preconditions;
import com.futurice.freesound.viewmodel.DataBinder;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import polanski.option.AtomicOption;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static com.futurice.freesound.utils.Preconditions.get;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.computation;

public final class HomeFragment extends BindingBaseFragment<HomeFragmentComponent> {

    @Nullable
    @Inject
    HomeFragmentViewModel homeFragmentViewModel;

    @Nullable
    @Inject
    Picasso picasso;

    @Nullable
    @BindView(R.id.avatar_image)
    ImageView avatarImage;

    @Nullable
    @BindView(R.id.username_textView)
    TextView userName;

    @Nullable
    @BindView(R.id.about_textView)
    TextView about;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final DataBinder dataBinder = new DataBinder() {

        @Override
        public void bind(@NonNull final CompositeDisposable d) {
            Preconditions.checkNotNull(homeFragmentViewModel);

            d.add(homeFragmentViewModel.getImage()
                                       .subscribeOn(computation())
                                       .observeOn(mainThread())
                                       .subscribe(it -> get(picasso).load(it)
                                                                    .into(avatarImage),
                                                  e -> Timber.e(e, "Error setting image")));

            d.add(homeFragmentViewModel.getUserName()
                                       .subscribeOn(computation())
                                       .observeOn(mainThread())
                                       .subscribe(it -> get(userName).setText(it),
                                                  e -> Timber.e(e, "Error setting user")));

            d.add(homeFragmentViewModel.getAbout()
                                       .subscribeOn(computation())
                                       .observeOn(mainThread())
                                       .subscribe(it -> get(about).setText(it),
                                                  e -> Timber.e(e, "Error setting about")));
        }

        @Override
        public void unbind() {
            get(picasso).cancelRequest(avatarImage);
        }

    };

    @NonNull
    static HomeFragment create() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder.setIfNone(bind(this, view));
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    @Override
    public void inject() {
        component().inject(this);
    }

    @NonNull
    @Override
    protected HomeFragmentComponent createComponent() {
        return DaggerHomeFragmentComponent.builder()
                                          .homeActivityComponent(
                                                  ((HomeActivity) getActivity()).component())
                                          .baseFragmentModule(new BaseFragmentModule(this))
                                          .build();
    }

    @NonNull
    @Override
    protected HomeFragmentViewModel viewModel() {
        return get(homeFragmentViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

}
