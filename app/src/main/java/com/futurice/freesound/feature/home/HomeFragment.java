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
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.mvi.UiBinder;
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
import polanski.option.AtomicOption;

import static butterknife.ButterKnife.bind;
import static com.futurice.freesound.common.utils.Preconditions.get;

public final class HomeFragment extends BindingBaseFragment<HomeFragmentComponent> {

    @Inject
    UiBinder<Fragment.UiEvent, Fragment.UiModel> uiBinder;

    @Inject
    Picasso picasso;

    @Inject
    SchedulerProvider schedulerProvider;

    @BindView(R.id.avatar_image)
    ImageView avatarImage;

    @BindView(R.id.username_textView)
    TextView userNameTextView;

    @BindView(R.id.about_textView)
    TextView aboutTextView;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

//    @NonNull
//    private final DataBinder dataBinder = new DataBinder() {
//
//        @Override
//        public void bind(@NonNull final CompositeDisposable d) {
//            Preconditions.checkNotNull(homeFragmentViewModel);
//
//            d.add(homeFragmentViewModel.getImage()
//                                       .subscribeOn(schedulerProvider.computation())
//                                       .observeOn(schedulerProvider.ui())
//                                       .subscribe(it -> picasso.load(it)
//                                                               .transform(PicassoTransformations
//                                                                                  .circular())
//                                                               .into(avatarImage),
//                                                  e -> Timber.e(e, "Error setting image")));
//
//            d.add(homeFragmentViewModel.getUserName()
//                                       .subscribeOn(schedulerProvider.computation())
//                                       .observeOn(schedulerProvider.ui())
//                                       .subscribe(it -> userNameTextView.setText(it),
//                                                  e -> Timber.e(e, "Error setting user")));
//
//            d.add(homeFragmentViewModel.getAbout()
//                                       .subscribeOn(schedulerProvider.computation())
//                                       .observeOn(schedulerProvider.ui())
//                                       .subscribe(it -> aboutTextView.setText(it),
//                                                  e -> Timber.e(e, "Error setting aboutTextView")));
//        }
//
//        @Override
//        public void unbind() {
//            picasso.cancelRequest(avatarImage);
//        }
//
//    };

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        unbinder = new UiBinder<>(this,  );
        // Get or create the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();
        uiBinder.bind(this); // TODO Need to define the handling behavior here
    }

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
        return ((HomeActivity) getActivity())
                .component().plus(new BaseFragmentModule(this));
    }

    @NonNull
    @Override
    protected HomeFragmentViewModel viewModel() {
        return get(homeFragmentViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return uiBinder;
    }

}
