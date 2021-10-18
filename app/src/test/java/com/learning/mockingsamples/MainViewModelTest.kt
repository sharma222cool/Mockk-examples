package com.learning.mockingsamples

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * @author mohit.sharma
 */

@RunWith(JUnit4::class)
class MainViewModelTest {
    @RelaxedMockK
    private lateinit var authenticationRepository: AuthenticationRepository

    private lateinit var mainViewModel: MainViewModel

    @RelaxedMockK
    private lateinit var authenticationStateObserver: Observer<AuthenticationState>

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkObject(Utility, MySingleton)
        mainViewModel = MainViewModel(authenticationRepository)
        mainViewModel.authenticationLiveData.observeForever(authenticationStateObserver)
    }

    @After
    fun after() {
        unmockkObject(Utility, MySingleton)
    }

    @Test
    fun `on Submit Clicked When Email Empty Show Empty Email Message`() {
        val email: String? = null
        val password: String? = null

        mainViewModel.onSubmitClicked(email, password)

        verify { authenticationStateObserver.onChanged(AuthenticationState.Error(EMPTY_EMAIL)) }
    }

    @Test
    fun `on Submit Clicked When Password Empty Show Empty Password Message`() {
        val email = "email@email.com"
        val password: String? = null

        mainViewModel.onSubmitClicked(email, password)
        verify { authenticationStateObserver.onChanged(AuthenticationState.Error(EMPTY_PASSWORD)) }
    }

    @Test
    fun `on Submit Clicked When Email,Password Valid Show Username Message`() {
        val email = "email@email.com"
        val password = "123456"
        val user = User("Mohit Sharma")

        every { authenticationRepository.authenticateUser(email, password) }.returns(Single.fromCallable { user })
        every { Utility.isValidUser() }.returns(true)
        every { MySingleton.isValidUser() }.returns(true)

        mainViewModel.onSubmitClicked(email, password)

        verify { authenticationStateObserver.onChanged(AuthenticationState.Success(user)) }
    }

    @Test
    fun `on Submit Clicked When Email,Password Invalid Show Error`() {
        val email = "email@email.com"
        val password = "123456"
        val user = User("Mohit Sharma")
        val exception = Throwable("Exception")
        every { authenticationRepository.authenticateUser(email, password) }.returns(Single.error { exception })

        mainViewModel.onSubmitClicked(email, password)

        verify { authenticationStateObserver.onChanged(AuthenticationState.Error(exception.message)) }
    }
}