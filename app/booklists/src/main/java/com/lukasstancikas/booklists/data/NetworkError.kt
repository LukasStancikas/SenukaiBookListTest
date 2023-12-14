package com.lukasstancikas.booklists.data

sealed class NetworkError {
    data object Cancelled : NetworkError()
    data object FailedToReachServer : NetworkError()
}
