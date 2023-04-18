package edu.singaporetech.csc2007team06.utils

/**
 *  This inline function will allow us to make safe network requests.
 */
inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        Resource.Error(e.message ?: "An unknown Error Occurred")
    }
}