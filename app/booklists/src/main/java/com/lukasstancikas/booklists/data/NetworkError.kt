package com.lukasstancikas.booklists.data

sealed class NetworkError {
    data object FailedToReachServer : NetworkError()
    data class Unexpected(val message: String?) : NetworkError()
}
