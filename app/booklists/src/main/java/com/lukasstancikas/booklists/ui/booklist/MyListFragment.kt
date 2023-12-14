package com.lukasstancikas.booklists.ui.booklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.lukasstancikas.booklists.R
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.databinding.FragmentMyListBinding
import com.lukasstancikas.booklists.navigator.NavigationIntent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MyListFragment : Fragment(R.layout.fragment_my_list) {

    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!
    private val arguments: MyListFragmentArgs by navArgs()
    private val viewModel: MyListViewModel by viewModel { parametersOf(arguments.bookList) }
    private val adapter by lazy {
        MyListAdapter(viewModel::onBookClick)
    }

    //region $ViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyListBinding.inflate(inflater, container, false)
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
        viewModel.uiState.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach(::renderState)
            .launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.errorStream.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach(::showError)
            .launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.navigationStream
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::navigate)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupViews() {
        binding.myListRecycler.adapter = adapter
        binding.myListSwipeRefresh.setOnRefreshListener { viewModel.onPullRefresh() }
    }

    private fun renderState(state: MyListUiState) = with(binding) {
        adapter.setItems(state.bookList.books)
        myListSwipeRefresh.isRefreshing = state.isLoading
    }

    private fun showError(error: NetworkError) {
        context?.let {
            val errorResId = when (error) {
                NetworkError.Cancelled -> R.string.error_cancelled
                NetworkError.FailedToReachServer -> R.string.error_reach_server
            }
            Snackbar.make(binding.root, errorResId, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun navigate(intent: NavigationIntent) {
        intent.navigate(findNavController())
    }

}