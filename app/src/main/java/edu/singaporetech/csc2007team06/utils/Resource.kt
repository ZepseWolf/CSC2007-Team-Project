package edu.singaporetech.csc2007team06.utils

/**
 * This sealed class will represent the three states of our network calls, either Loading, Success, or Error
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}