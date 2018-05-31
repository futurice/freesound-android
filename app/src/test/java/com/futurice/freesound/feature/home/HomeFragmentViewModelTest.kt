package com.futurice.freesound.feature.home

import com.futurice.freesound.feature.common.streams.Fetch
import com.futurice.freesound.feature.common.streams.Operation
import com.futurice.freesound.network.api.model.User
import com.futurice.freesound.test.data.TestData
import com.futurice.freesound.test.rx.TrampolineSchedulerProvider
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class HomeFragmentViewModelTest {

    @Mock
    private lateinit var homeUserIteractor: HomeUserInteractor

    @Mock
    private lateinit var refreshInteractor: RefreshInteractor

    private lateinit var schedulers: TrampolineSchedulerProvider

    private val testUser: User get() = TestData.user()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        schedulers = TrampolineSchedulerProvider()
    }

    @Test
    fun `uiModel onInitial`() {
        arrange {
            homeUserStream { emptyList() }
            refresh { emptyList() }
        }

        val homeUiModel = HomeUiModel(
                user = null,
                isLoading = false,
                isRefreshing = false,
                errorMsg = null)

        with(createVm()) {
            uiModels().test().assertValue(homeUiModel)
        }
    }

    @Test
    fun `uiModel onInitialSuccess`() {
        arrange {
            homeUserStream { listOf(Fetch.Success(testUser)) }
            refresh { emptyList() }
        }

        val userUiModel = UserUiModel(
                username = testUser.username,
                about = testUser.about,
                avatarUrl = testUser.avatar.large)
        val homeUiModel = HomeUiModel(
                user = userUiModel,
                isLoading = false,
                isRefreshing = false,
                errorMsg = null)

        with(createVm()) {
            uiModels().skip(1).test().assertValue(homeUiModel)
        }
    }

    @Test
    fun `uiModel onInitiaInProgress`() {
        arrange {
            homeUserStream { listOf(Fetch.InProgress()) }
            refresh { emptyList() }
        }

        val homeUiModel = HomeUiModel(
                user = null,
                isLoading = true,
                isRefreshing = false,
                errorMsg = null)

        with(createVm()) {
            uiModels().skip(1).test().assertValue(homeUiModel)
        }
    }

    @Test
    fun `uiModel usesLocalizedExceptionMessage onInitialFailure`() {
        val t = Throwable("Failure message")
        arrange {
            homeUserStream { listOf(Fetch.Failure(t)) }
            refresh { emptyList() }
        }

        val homeUiModel = HomeUiModel(
                user = null,
                isLoading = false,
                isRefreshing = false,
                errorMsg = t.localizedMessage)

        with(createVm()) {
            uiModels().skip(1).test().assertValue(homeUiModel)
        }
    }

    private fun createVm(): HomeFragmentViewModel =
            HomeFragmentViewModel(homeUserIteractor, refreshInteractor, schedulers)

    fun arrange(init: Arrangement.() -> Unit) = Arrangement().apply(init)

    inner class Arrangement {

        fun homeUserStream(init: () -> List<Fetch<User>>) {
            `when`(homeUserIteractor.homeUserStream()).thenReturn(init().asStream())
        }

        fun refresh(init: () -> List<Operation>) {
            `when`(refreshInteractor.refresh()).thenReturn(init().asStream())
        }
    }
}


fun <T> List<T>.asStream(): Observable<T> {
    return Observable.fromIterable(this).concatWith(Observable.never<T>())
}
