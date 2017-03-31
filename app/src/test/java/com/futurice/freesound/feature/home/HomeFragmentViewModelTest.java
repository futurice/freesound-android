package com.futurice.freesound.feature.home;

import com.futurice.freesound.network.api.model.Avatar;
import com.futurice.freesound.network.api.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HomeFragmentViewModelTest {

    private static final User USER = createUser();

    @Mock
    private UserDataModel userDataModel;

    private HomeFragmentViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getImage_returnsLargeImage() {
        when(userDataModel.getHomeUser()).thenReturn(Single.just(USER));

        viewModel = createVm();

        viewModel.getImage()
                 .test()
                 .assertValue(USER.avatar().large());
    }

    @Test
    public void getUserName_returnsName() {
        when(userDataModel.getHomeUser()).thenReturn(Single.just(USER));

        viewModel = createVm();

        viewModel.getUserName()
                 .test()
                 .assertValue(USER.username());
    }

    @Test
    public void getAbout_returnsAbout() {
        when(userDataModel.getHomeUser()).thenReturn(Single.just(USER));

        viewModel = createVm();

        viewModel.getAbout()
                 .test()
                 .assertValue(USER.about());
    }

    @Test
    public void getHomeUser_doesNotSubscribeInCtor() {
        AtomicBoolean isSubscribed = new AtomicBoolean();
        when(userDataModel.getHomeUser())
                .thenReturn(Single.<User>never()
                                    .doOnSubscribe(__ -> isSubscribed.set(true)));

        viewModel = createVm();

        assertThat(isSubscribed.get()).isFalse();
    }

    @Test
    public void getProperties_subscribesOnlyOnce() {
        when(userDataModel.getHomeUser()).thenReturn(Single.just(USER));
        viewModel = createVm();

        viewModel.getImage().test();
        viewModel.getUserName().test();

        verify(userDataModel).getHomeUser();
    }

    @NonNull
    private HomeFragmentViewModel createVm() {
        return new HomeFragmentViewModel(userDataModel);
    }

    private static User createUser() {
        return new UserBuilder()
                .largeAvatar("large.com")
                .userName("user name")
                .about("aboutTextView")
                .build();
    }

    private static class UserBuilder {

        @Nullable
        private String largeAvatar;

        @Nullable
        private String userName;

        @Nullable
        private String about;

        UserBuilder largeAvatar(String largeAvatar) {
            this.largeAvatar = largeAvatar;
            return this;
        }

        UserBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        UserBuilder about(String about) {
            this.about = about;
            return this;
        }

        User build() {
            Avatar avatar = mock(Avatar.class);
            when(avatar.large()).thenReturn(largeAvatar);
            User user = mock(User.class);
            when(user.avatar()).thenReturn(avatar);
            when(user.username()).thenReturn(userName);
            when(user.about()).thenReturn(about);
            return user;
        }

    }
}
