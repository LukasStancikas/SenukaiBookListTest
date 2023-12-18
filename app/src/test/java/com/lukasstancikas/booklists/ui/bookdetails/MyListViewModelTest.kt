package com.lukasstancikas.booklists.ui.bookdetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.network.BooksRepository
import com.lukasstancikas.booklists.ui.MainCoroutineRule
import com.lukasstancikas.booklists.ui.booklist.MyListViewModel
import com.lukasstancikas.booklists.usecase.PopulatedMyListUseCase
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
class MyListViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MyListViewModel
    private val repository = mockk<BooksRepository>()
    private val initialBookList = BookList(1, "List")

    @Before
    fun setup() {
        viewModel = MyListViewModel(
            savedStateHandle = SavedStateHandle(),
            bookList = initialBookList,
            myListUseCase = PopulatedMyListUseCase(repository)
        )
    }

    @Test
    fun `onPullRefresh should fetch the list and update UI state`() = runTest {
        turbineScope {

            coEvery { repository.getBookDetails(bookDetails.id, any()) } returns flowOf(bookDetails)
            coEvery { repository.getBookDetails(bookDetails2.id, any()) } returns flowOf(bookDetails2)
            coEvery { repository.getAllBooks(any()) } returns flowOf(listBooks)

            val turbine = viewModel.uiState.testIn(backgroundScope)
            viewModel.onPullRefresh()
            turbine.awaitItem().let {
                Truth.assertThat(it.isLoading).isFalse()
            }

            turbine.awaitItem().let {
                Truth.assertThat(it.bookList.books).isEmpty()
                Truth.assertThat(it.isLoading).isTrue()
            }

            turbine.awaitItem().let {
                Truth.assertThat(it.bookList.books).hasSize(2)
                Truth.assertThat(it.isLoading).isTrue()
                Truth.assertThat(it.bookList.books[0].isLoading).isFalse()
                Truth.assertThat(it.bookList.books[0].isWithoutDetails).isFalse()
                Truth.assertThat(it.bookList.books[1].isLoading).isTrue()
            }
            turbine.awaitItem().let {
                Truth.assertThat(it.bookList.books).hasSize(2)
                Truth.assertThat(it.isLoading).isTrue()
                Truth.assertThat(it.bookList.books[1].isLoading).isFalse()
                Truth.assertThat(it.bookList.books[1].isWithoutDetails).isFalse()
            }
            turbine.awaitItem().let {
                Truth.assertThat(it.isLoading).isFalse()
            }
        }
    }

    @Test
    fun `fetching book lists should handle network errors`() = runTest {
        turbineScope {
            val unknownHostException = UnknownHostException()
            coEvery { repository.getAllBooks(any()) } throws unknownHostException

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
                Truth.assertThat(it.bookList.books).isEmpty()
                Truth.assertThat(it.isLoading).isFalse()
            }
            errorTurbine.awaitItem().let {
                Truth.assertThat(it).isEqualTo(NetworkError.FailedToReachServer)
            }
        }
    }
}

