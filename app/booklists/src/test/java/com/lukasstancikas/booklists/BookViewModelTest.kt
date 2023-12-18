package com.lukasstancikas.booklists

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.network.BooksRepository
import com.lukasstancikas.booklists.ui.bookdetails.BookViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class BookViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: BookViewModel
    private val repository = mockk<BooksRepository>()
    private val initialBook = Book(1, 1, "Title", "https://google.com")

    @Before
    fun setup() {
        viewModel = BookViewModel(
            book = initialBook,
            savedStateHandle = SavedStateHandle(),
            booksRepository = repository
        )
    }

    @Test
    fun `viewModel should have initial book ready`() = runTest {
        Truth.assertThat(viewModel.uiState.value).isNotNull()
        Truth.assertThat(viewModel.uiState.value.book).isEqualTo(initialBook)
    }

    @Test
    fun `onPullRefresh should fetch book details and update UI state`() = runTest {
        turbineScope {
            val bookDetails = initialBook.copy(author = "Fake Author")
            coEvery { repository.getBookDetails(viewModel.uiState.value.book.id, any()) } returns flowOf(bookDetails)

            val turbine = viewModel.uiState.testIn(backgroundScope)

            viewModel.onPullRefresh()

            turbine.awaitItem().let {
                Truth.assertThat(it.isLoading).isFalse()
                Truth.assertThat(it.book.author).isNull()
            }

            turbine.awaitItem().let {
                Truth.assertThat(it.isLoading).isTrue()
            }

            turbine.awaitItem().let {
                Truth.assertThat(it.book.author).isEqualTo(bookDetails.author)
            }

            turbine.awaitItem().let {
                Truth.assertThat(it.isLoading).isFalse()
            }
        }
    }

    @Test
    fun `fetchBookDetails should handle network errors`() = runTest {
        turbineScope {
            val unknownHostException = UnknownHostException()
            coEvery { repository.getBookDetails(viewModel.uiState.value.book.id, any()) } throws unknownHostException

            val stateTurbine = viewModel.uiState.testIn(backgroundScope)
            val errorTurbine = viewModel.errorStream.testIn(backgroundScope)

            viewModel.onPullRefresh()

            stateTurbine.awaitItem().let {
                Truth.assertThat(it.isLoading).isFalse()
            }
            stateTurbine.awaitItem().let {
                Truth.assertThat(it.isLoading).isTrue()
            }
            stateTurbine.awaitItem().let {
                Truth.assertThat(it.book.author).isNull()
                Truth.assertThat(it.isLoading).isFalse()
            }
            errorTurbine.awaitItem().let {
                Truth.assertThat(it).isEqualTo(NetworkError.FailedToReachServer)
            }
        }
    }
}

