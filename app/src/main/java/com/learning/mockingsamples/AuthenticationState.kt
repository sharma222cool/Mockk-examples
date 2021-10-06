package com.learning.mockingsamples

/**
 * @author mohit.sharma
 */
sealed class AuthenticationState
{
    data class Success(val user: User): AuthenticationState()
    data class Error(val message: String?): AuthenticationState()
}
