package io.videos.application

fun <A, B> A.pipe(fn: (A) -> B): B = fn(this)