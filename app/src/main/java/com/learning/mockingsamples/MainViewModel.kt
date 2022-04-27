package com.learning.mockingsamples

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * @author mohit.sharma
 */
class MainViewModel(private val repository: AuthenticationRepository) {

    private val compositeDisposable = CompositeDisposable()
    private val authenticationMutableLiveData = MutableLiveData<AuthenticationState>()
    val authenticationLiveData: LiveData<AuthenticationState> = authenticationMutableLiveData

    fun onSubmitClicked(email: String?, password: String?) {
        if (email.isNullOrEmpty())
        {
            authenticationMutableLiveData.postValue(AuthenticationState.Error(EMPTY_EMAIL))
        }
        else if (password.isNullOrEmpty())
        {
            authenticationMutableLiveData.postValue(AuthenticationState.Error(EMPTY_PASSWORD))
        }
        if(Utility.isValidUser() && MySingleton.isValidUser()) {
            compositeDisposable.add(repository.authenticateUser(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSuccess(it) }) { onError(it) })
        }else{
            authenticationMutableLiveData.postValue(AuthenticationState.Error(INVALID_USER))
        }
    }

    private fun onError(it: Throwable?) {
        authenticationMutableLiveData.postValue(AuthenticationState.Error(it?.message))
    }

    private fun onSuccess(user: User) {
        authenticationMutableLiveData.postValue(AuthenticationState.Success(user))
    }
}