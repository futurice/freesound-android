package com.futurice.freesound.feature.home

import com.futurice.freesound.network.api.model.Avatar
import com.futurice.freesound.network.api.model.User
import com.futurice.freesound.test.mock
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.concurrent.atomic.AtomicBoolean

class HomeFragmentViewModelTest {

    @Mock
    private lateinit var userDataModel: UserDataModel

    private lateinit var viewModel: HomeFragmentViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getImage_returnsLargeImage() {
        `when`(userDataModel.homeUser).thenReturn(Single.just(USER))

        with(createVm()) {
            image.test()
                    .assertValue(USER.avatar().large())
        }


    }

    @Test
    fun getUserName_returnsName() {
        `when`(userDataModel.homeUser).thenReturn(Single.just(USER))

        with(createVm()) {
            userName.test()
                    .assertValue(USER.username())
        }
    }

    @Test
    fun getAbout_returnsAbout() {
        `when`(userDataModel.homeUser).thenReturn(Single.just(USER))

        with(createVm()) {
            about.test()
                    .assertValue(USER.about())
        }
    }

    @Test
    fun getHomeUser_doesNotSubscribeInCtor() {
        val isSubscribed = AtomicBoolean()
        `when`(userDataModel.homeUser)
                .thenReturn(Single.never<User>()
                        .doOnSubscribe { isSubscribed.set(true) })

        createVm()

        assertThat(isSubscribed.get()).isFalse()
    }

    @Test
    fun getProperties_subscribesOnlyOnce() {
        `when`(userDataModel.homeUser).thenReturn(Single.just(USER))
        with(createVm()) {
            image.test()
            userName.test()
        }

        verify(userDataModel).homeUser
    }

    private fun createVm(): HomeFragmentViewModel =
            HomeFragmentViewModel(userDataModel)


    private class UserBuilder {

        internal lateinit var largeAvatar: String

        internal lateinit var userName: String

        internal lateinit var about: String

        internal fun build() = mock<User>().apply {
            val avatar = mock<Avatar>()
            `when`(avatar.large()).thenReturn(largeAvatar)
            `when`(avatar()).thenReturn(avatar)
            `when`(username()).thenReturn(userName)
            `when`(about()).thenReturn(about)
        }

    }

    companion object {

        private val USER = UserBuilder()
                .apply {
                    largeAvatar = "large.com"
                    userName = "user name"
                    about = "aboutTextView"
                }.build()
    }
}
