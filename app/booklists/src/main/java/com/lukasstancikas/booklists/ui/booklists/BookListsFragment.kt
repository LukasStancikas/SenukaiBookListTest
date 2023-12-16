package com.lukasstancikas.booklists.ui.booklists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.lukasstancikas.booklists.R
import com.lukasstancikas.booklists.databinding.FragmentListsBinding
import com.lukasstancikas.booklists.ui.base.FragmentWithCommonStreams
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookListsFragment : FragmentWithCommonStreams<BookListsUiState>(R.layout.fragment_lists) {

    private var _binding: FragmentListsBinding? = null
    private val binding get() = _binding!!
    override val viewModel: BookListsViewModel by viewModel()
    private val adapter by lazy {
        BookListsAdapter(viewModel::onAllClick, viewModel::onBookClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.onPullRefresh()
        }
    }

    //region $ViewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    //region $MVVMStreams
    override fun renderState(state: BookListsUiState) = with(binding) {
        adapter.setItems(state.bookLists)
        bookListsSwipeRefresh.isRefreshing = state.isLoading
    }

    override fun showError(errorResId: Int) {
        context?.let {
            Snackbar.make(binding.root, errorResId, Snackbar.LENGTH_SHORT).show()
        }
    }
    //endregion

    private fun setupViews() {
        binding.bookListsRecycler.adapter = adapter
        binding.bookListsSwipeRefresh.setOnRefreshListener { viewModel.onPullRefresh() }
    }
}