package com.learning.mockingsamples

import io.reactivex.Single

/**
 * @author mohit.sharma
 */
class AuthenticationRepository {
    fun authenticateUser(email: String?, password: String?): Single<User> {
        return Single.fromCallable { getUser() }
    }

    private fun getUser(): User {
        return User("Mohit")
    }
}