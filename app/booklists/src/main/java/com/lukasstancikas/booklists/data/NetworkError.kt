package com.lukasstancikas.booklists.data

sealed class NetworkError {
    data object Cancelled : NetworkError()
    data object FailedToReachServer : NetworkError()
    data class Unexpected(val message: String?) : NetworkError()
}
