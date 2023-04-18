package edu.singaporetech.csc2007team06.utils

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicInteger

/**
 * A simple counter implementation of [IdlingResource].
 * This is useful for testing purposes, where you want to wait for a certain
 * operation to finish before proceeding with the test.
 */
class SimpleCountingIdlingResource(
    private val resourceName: String,
) : IdlingResource {

    private val counter = AtomicInteger(0)

    @Volatile
    private var resourceCallback:
            IdlingResource.ResourceCallback? = null

    override fun getName() = resourceName

    override fun isIdleNow() = counter.get() == 0

    override fun registerIdleTransitionCallback(
        resourceCallback: IdlingResource.ResourceCallback,
    ) {
        this.resourceCallback = resourceCallback
    }

    fun increment() {
        counter.getAndIncrement()
    }

    fun decrement() {
        val counterVal = counter.decrementAndGet()
        if (counterVal == 0) {
            resourceCallback?.onTransitionToIdle()
        } else if (counterVal < 0) {
            throw IllegalStateException(
                "Counter has been corrupted!"
            )
        }
    }
}