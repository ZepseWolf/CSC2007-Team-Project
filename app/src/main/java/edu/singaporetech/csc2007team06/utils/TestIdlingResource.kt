package edu.singaporetech.csc2007team06.utils

/**
 * A simple counter implementation of [IdlingResource].
 * This is useful for testing purposes, where you want to wait for a certain
 * operation to finish before proceeding with the test.
 */
object TestIdlingResource {
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = SimpleCountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}