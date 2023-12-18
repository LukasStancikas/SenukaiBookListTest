package com.lukasstancikas.booklists.network

interface NetworkChecker {
    fun isConnected(): Boolean
}