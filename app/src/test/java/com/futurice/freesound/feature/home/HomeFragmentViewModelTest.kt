package com.futurice.freesound.feature.home

import com.futurice.freesound.network.api.model.User
import com.futurice.freesound.test.data.TestData
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

    private val testUser: User get() = TestData.user()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getImage_returnsLargeImage() {
        arrange {
            homeUser { testUser }
        }

        with(createVm()) {
            image.test()
                    .assertValue(testUser.avatar.large)
        }
    }

    @Test
    fun getUserName_returnsName() {
        arrange {
            homeUser { testUser }
        }

        with(createVm()) {
            userName.test()
                    .assertValue(testUser.username)
        }
    }

    @Test
    fun getAbout_returnsAbout() {
        arrange {
            homeUser { testUser }
        }

        with(createVm()) {
            about.test()
                    .assertValue(testUser.about)
        }
    }

    @Test
    fun getHomeUser_doesNotSubscribeInCtor() {
        val isSubscribed = AtomicBoolean()
        `when`(userDataModel.homeUser)
                .thenReturn(Single.never<User>()
                        .doOnSubscribe { isSubscribed.set(true) })

        ignore {
            createVm()
        }

        assertThat(isSubscribed.get()).isFalse()
    }

    @Test
    fun getProperties_subscribesOnlyOnce() {
        arrange {
            homeUser { testUser }
        }

        with(createVm()) {
            image.test()
            userName.test()
        }

        verify(userDataModel).homeUser
    }

    private fun createVm(): HomeFragmentViewModel =
            HomeFragmentViewModel(userDataModel)

    fun ignore(ignoreReturnValue: () -> Unit) {
        ignoreReturnValue()
    }

    fun arrange(init: Arrangement.() -> Unit) = Arrangement().apply(init)

    inner class Arrangement {

        fun homeUser(init: () -> User) {
            `when`(userDataModel.homeUser).thenReturn(Single.just(init()))
        }

    }
}
