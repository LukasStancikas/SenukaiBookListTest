package com.lukasstancikas.booklists.ui.bookdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.lukasstancikas.booklists.R
import com.lukasstancikas.booklists.databinding.FragmentBookDetailsBinding
import com.lukasstancikas.booklists.ui.base.FragmentWithCommonStreams
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.time.format.DateTimeFormatter


class BookDetailsFragment :
    FragmentWithCommonStreams<BookDetailsUiState>(R.layout.fragment_book_details) {

    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!
    private val arguments: BookDetailsFragmentArgs by navArgs()
    override val viewModel: BookViewModel by viewModel { parametersOf(arguments.book) }
    private val formatter = DateTimeFormatter.ofPattern("yyyy LLLL dd")

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
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
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
    override fun renderState(state: BookDetailsUiState) = with(binding) {
        Glide
            .with(bookImage.context)
            .load(state.book.img)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(android.R.drawable.progress_indeterminate_horizontal)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(bookImage)

        bookTitle.text = state.book.title

        bookAuthor.isVisible = state.book.author != null
        bookAuthor.text = state.book.author

        bookIsbn.isVisible = state.book.publicationDate != null
        bookIsbn.text = getString(R.string.book_isbn, state.book.isbn)

        bookDescription.isVisible = state.book.description != null
        bookDescription.text = state.book.description

        bookPublicationDate.isVisible = state.book.publicationDate != null
        state.book.publicationDate?.format(formatter)?.let {
            bookPublicationDate.text =
                getString(R.string.book_publication_date, it)
        }
        println("state.isLoading = ${state.isLoading}")
        bookListsSwipeRefresh.isRefreshing = state.isLoading
    }
    //endregion

    private fun setupViews() {
        binding.bookListsSwipeRefresh.setOnRefreshListener { viewModel.onPullRefresh() }
    }
}