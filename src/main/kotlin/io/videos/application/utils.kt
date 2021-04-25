package io.videos.application

fun <A, B> A.pipe(fn: (A) -> B): B = fn(this)

sealed class Result<O, E> {
    data class Ok<O, E>(val ok: O) : Result<O, E>()
    data class Error<O, E>(val err: E) : Result<O, E>()
}

fun <O, E, T> Result<O, E>.map(fn: (O) -> T): Result<T, E> {
    return when (this) {
        is Result.Ok -> Result.Ok(fn(this.ok))
        is Result.Error -> Result.Error(this.err)
    }
}

fun <O, E> Result<O, E>.onError(toDefault: (E) -> O): O {
    return when (this) {
        is Result.Ok -> this.ok
        is Result.Error -> toDefault(this.err)
    }
}