package com.lukasstancikas.booklists.ui.base

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lukasstancikas.booklists.R
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.navigator.NavigationIntent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class FragmentWithCommonStreams<UiState>(
    @LayoutRes resourceId: Int,
) : Fragment(resourceId) {

    abstract val viewModel: ViewModelCommonStreams<UiState>
    abstract fun renderState(state: UiState)

    abstract fun showError(@StringRes errorResId: Int)

    private val navController: NavController? = try {
        findNavController()
    } catch (e: IllegalStateException) {
        e.printStackTrace()
        null
    }

    init {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .onEach(::renderState)
                    .launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.errorStream.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .onEach(::getErrorString)
                    .launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.navigationStream
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .onEach(::navigate)
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
        }
    }

    private fun getErrorString(error: NetworkError) {
        val errorResId = when (error) {
            NetworkError.Cancelled -> R.string.error_cancelled
            NetworkError.FailedToReachServer -> R.string.error_reach_server
        }
        showError(errorResId)
    }

    private fun navigate(intent: NavigationIntent) = navController?.let {
        intent.navigate(it)
    }
}