package com.familyhub.core.common.extensions

import com.familyhub.core.common.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Wrap Flow emissions in Result.Success and handle errors as Result.Error
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it as? Exception ?: Exception(it))) }
}

/**
 * Map Flow<Result<T>> to Flow<Result<R>>
 */
fun <T, R> Flow<Result<T>>.mapResult(transform: (T) -> R): Flow<Result<R>> {
    return map { result ->
        when (result) {
            is Result.Success -> Result.Success(transform(result.data))
            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}

/**
 * Filter successful results
 */
fun <T> Flow<Result<T>>.onlySuccess(): Flow<T> {
    return map { result ->
        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> null
        }
    }
        .map { it!! }
}
