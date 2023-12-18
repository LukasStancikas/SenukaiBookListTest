package com.lukasstancikas.booklists.ui.booklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.lukasstancikas.booklists.R
import com.lukasstancikas.booklists.databinding.FragmentMyListBinding
import com.lukasstancikas.booklists.ui.base.FragmentWithCommonStreams
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MyListFragment : FragmentWithCommonStreams<MyListUiState>(R.layout.fragment_my_list) {

    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!
    private val arguments: MyListFragmentArgs by navArgs()
    override val viewModel: MyListViewModel by viewModel { parametersOf(arguments.bookList) }
    private val adapter by lazy {
        MyListAdapter(viewModel::onBookClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.onInitialize()
        }
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
        setupViews()
    }

    //region $MVVMStreams
    override fun renderState(state: MyListUiState) = with(binding) {
        adapter.setItems(state.bookList.books)
        myListSwipeRefresh.isRefreshing = state.isLoading
    }
    //endregion

    private fun setupViews() {
        binding.myListRecycler.adapter = adapter
        binding.myListSwipeRefresh.setOnRefreshListener { viewModel.onPullRefresh() }
    }
}