package com.lukasstancikas.booklists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar

import com.lukasstancikas.booklists.R
import com.lukasstancikas.booklists.databinding.FragmentBookListsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookListsFragment : Fragment(R.layout.fragment_book_lists) {

    private var _binding: FragmentBookListsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookListsViewModel by viewModel()
    private val adapter by lazy {
        BookListsAdapter(viewModel::onAllClick, viewModel::onBookClick)
    }

    //region $ViewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
//endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        setupViews()
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::renderState)
            .launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.errorStream
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::showError)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupViews() {
        binding.bookListsRecycler.adapter = adapter
        binding.bookListsSwipeRefresh.setOnRefreshListener { viewModel.onPullRefresh() }
    }

    private fun renderState(state: BookListsUiState) = with(binding) {
        adapter.setItems(state.bookLists)
        bookListsSwipeRefresh.isRefreshing = state.isLoading
    }

    private fun showError(error: BookListsUiState.BookListsError) {
        context?.let {
            val errorResId = when (error) {
                BookListsUiState.BookListsError.Cancelled -> R.string.error_cancelled
                BookListsUiState.BookListsError.FailedToReachServer -> R.string.error_reach_server
            }
            Snackbar.make(binding.root, errorResId, Snackbar.LENGTH_SHORT).show()
        }
    }

}