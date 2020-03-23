package com.kharchenkovitaliy.intouch.shared

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class Result<out V, out E : Any> {

    companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        fun <V> success(value: V): Result<V, Nothing> = Success(value)

        /**
         * Returns an instance that encapsulates the given [error] as failure.
         */
        fun <E : Any> failure(error: E): Result<Nothing, E> = Failure(error)
    }

    class Success<out V>(val value: V) : Result<V, Nothing>() {

        override fun hashCode(): Int = value.hashCode()

        override fun equals(other: Any?): Boolean =
            this === other || other is Success<*> && value == other.value

        override fun toString() = "[Success: $value]"
    }

    class Failure<out E : Any>(val error: E) : Result<Nothing, E>() {

        override fun hashCode(): Int = error.hashCode()

        override fun equals(other: Any?): Boolean =
            this === other || other is Failure<*> && error == other.error

        override fun toString() = "[Failure: $error]"
    }
}

fun Result.Companion.success() = success(Unit)

/**
 * Returns the encapsulated value if this instance represents [success][Result.Success] or `null`
 * if it is [failure][Result.Failure].
 *
 * This function is shorthand for `getOrElse { null }` (see [getOrElse]) or
 * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
 */
fun <V> Result<V, *>.getOrNull(): V? =
    when (this) {
        is Result.Success -> value
        is Result.Failure -> null
    }

/**
 * Returns the encapsulated exception if this instance represents [failure][Result.Failure] or `null`
 * if it is [success][Result.Success].
 *
 * This function is shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
 */
fun <E : Any> Result<*, E>.errorOrNull(): E? =
    when (this) {
        is Result.Success -> null
        is Result.Failure -> error
    }

/**
 * Returns the encapsulated value if this instance represents [success][Result.Success] or the
 * result of [onFailure] function for encapsulated error if it is [failure][Result.Failure].
 *
 * Note, that an exception thrown by [onFailure] function is rethrown by this function.
 *
 * This function is shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
 */
inline fun <R, V : R, E : Any> Result<V, E>.getOrElse(
    onFailure: (error: E) -> R
): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result.Success -> value
        is Result.Failure -> onFailure(error)
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][Result.Success] or the
 * [defaultValue] if it is [failure][Result.Failure].
 *
 * This function is shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
 */
fun <R, V : R, E : Any> Result<V, E>.getOrDefault(
    defaultValue: R
): R =
    when (this) {
        is Result.Success -> value
        is Result.Failure -> defaultValue
    }

/**
 * Returns the the result of [onSuccess] for encapsulated value if this instance represents [success][Result.Success]
 * or the result of [onFailure] function for encapsulated error if it is [failure][Result.Failure].
 *
 * Note, that an exception thrown by [onSuccess] or by [onFailure] function is rethrown by this function.
 */
inline fun <R : Any, V, E : Any> Result<V, E>.fold(
    onSuccess: (value: V) -> R,
    onFailure: (error: E) -> R
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result.Success -> onSuccess(value)
        is Result.Failure -> onFailure(error)
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to encapsulated value
 * if this instance represents [success][Result.Success] or the
 * original encapsulated error if it is [failure][Result.Failure].
 *
 * Note, that an exception thrown by [transform] function is rethrown by this function.
 */
inline fun <R, V, E : Any> Result<V, E>.map(
    transform: (value: V) -> R
): Result<R, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result.Success -> Result.success(transform(value))
        is Result.Failure -> this
    }
}

inline fun <R, V, E : Any> Result<V, E>.flatMap(
    transform: (value: V) -> Result<R, E>
): Result<R, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result.Success -> transform(value)
        is Result.Failure -> this
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to encapsulated error
 * if this instance represents [failure][Result.Failure] or the
 * original encapsulated value if it is [success][Result.Success].
 *
 * Note, that an exception thrown by [transform] function is rethrown by this function.
 */
inline fun <R : Any, V, E : Any> Result<V, E>.mapError(
    transform: (value: E) -> R
): Result<V, R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> Result.failure(transform(error))
    }
}

inline fun <R : Any, V, E : Any> Result<V, E>.flatMapError(
    transform: (error: E) -> Result<V, R>
): Result<V, R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> transform(error)
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to encapsulated error
 * if this instance represents [failure][Result.Failure] or the
 * original encapsulated value if it is [success][Result.Success].
 *
 * Note, that an exception thrown by [transform] function is rethrown by this function.
 */
inline fun <R, V : R, E : Any> Result<V, E>.recover(
    transform: (error: E) -> R
): Result<R, Nothing> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> Result.success(transform(error))
    }
}

/**
 * Performs the given [action] on encapsulated exception if this instance represents [failure][Result.Failure].
 * Returns the original `Result` unchanged.
 */
inline fun <V, E : Any> Result<V, E>.onFailure(
    action: (error: E) -> Unit
): Result<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (this is Result.Failure) {
        action(error)
    }
    return this
}

/**
 * Performs the given [action] on encapsulated value if this instance represents [success][Result.Success].
 * Returns the original `Result` unchanged.
 */
inline fun <V, E : Any> Result<V, E>.onSuccess(
    action: (value: V) -> Unit
): Result<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (this is Result.Success) {
        action(value)
    }
    return this
}

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching and encapsulating any thrown exception as a failure.
 */
inline fun <R> tryCatch(block: () -> R): Result<R, Exception> =
    try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(e)
    }

// Only for test
fun <V> Result<V, *>.getOrThrow(): V =
    getOrElse { error ->
        throw RuntimeException(error.toString())
    }
