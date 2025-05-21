package com.example.myapplication.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides Coroutine Dispatchers for different use cases
 */
@Singleton
class CoroutineDispatchers @Inject constructor() {
    val main: CoroutineDispatcher = Dispatchers.Main
    val default: CoroutineDispatcher = Dispatchers.Default
    val io: CoroutineDispatcher = Dispatchers.IO
    val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}

/**
 * Interface for providing coroutine dispatchers
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

/**
 * Default implementation of [DispatcherProvider] using [CoroutineDispatchers]
 */
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    private val dispatchers = CoroutineDispatchers()
    
    override val main: CoroutineDispatcher = dispatchers.main
    override val default: CoroutineDispatcher = dispatchers.default
    override val io: CoroutineDispatcher = dispatchers.io
    override val unconfined: CoroutineDispatcher = dispatchers.unconfined
}

/**
 * Test implementation of [DispatcherProvider] for unit tests
 */
class TestDispatcherProvider(
    override val main: CoroutineDispatcher = Dispatchers.Unconfined,
    override val default: CoroutineDispatcher = Dispatchers.Unconfined,
    override val io: CoroutineDispatcher = Dispatchers.Unconfined,
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
) : DispatcherProvider

/**
 * Executes the given [block] in the IO context
 */
suspend fun <T> withIO(block: suspend () -> T): T = kotlinx.coroutines.withContext(Dispatchers.IO,
    block as suspend CoroutineScope.() -> T
)

/**
 * Executes the given [block] in the Default context
 */
suspend fun <T> withDefault(block: suspend () -> T): T = kotlinx.coroutines.withContext(Dispatchers.Default,
    block as suspend CoroutineScope.() -> T
)

/**
 * Executes the given [block] in the Main context
 */
suspend fun <T> withMain(block: suspend () -> T): T = kotlinx.coroutines.withContext(Dispatchers.Main,
    block as suspend CoroutineScope.() -> T
)

/**
 * Executes the given [block] in the Unconfined context
 */
suspend fun <T> withUnconfined(block: suspend () -> T): T = kotlinx.coroutines.withContext(Dispatchers.Unconfined,
    block as suspend CoroutineScope.() -> T
)
