package io.videos.application

fun <A, B> A.pipe(fn: (A) -> B): B = fn(this)

sealed class Result<O, E> {
    data class Ok<O, E>(val okValue: O) : Result<O, E>()
    data class Error<O, E>(val errorValue: E) : Result<O, E>()
}

fun <O, E, T> Result<O, E>.map(fn: (O) -> T): Result<T, E> {
    return when (this) {
        is Result.Ok -> Result.Ok(fn(this.okValue))
        is Result.Error -> Result.Error(this.errorValue)
    }
}

fun <O, E> Result<O, E>.onError(toDefault: (E) -> O): O {
    return when (this) {
        is Result.Ok -> this.okValue
        is Result.Error -> toDefault(this.errorValue)
    }
}