package com.lukasstancikas.booklists.ui.bookdetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.network.BooksRepository
import com.lukasstancikas.booklists.ui.MainCoroutineRule
import com.lukasstancikas.booklists.ui.booklists.BookListsViewModel
import com.lukasstancikas.booklists.usecase.PopulatedBookListsUseCase
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
class BookListsViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: BookListsViewModel
    private val repository = mockk<BooksRepository>()

    @Before
    fun setup() {
        viewModel = BookListsViewModel(
            savedStateHandle = SavedStateHandle(),
            booksListUseCase = PopulatedBookListsUseCase(repository)
        )
    }

    @Test
    fun `onPullRefresh should fetch book lists and update UI state`() = runTest {
        turbineScope {

            coEvery { repository.getBookLists(any()) } returns flowOf(listBookLists)
            coEvery { repository.getAllBooks(any()) } returns flowOf(listBooks)

            val turbine = viewModel.uiState.testIn(backgroundScope)
            viewModel.onPullRefresh()
            turbine.awaitItem().let {
                Truth.assertThat(it.isLoading).isFalse()
            }

            turbine.awaitItem().let {
                Truth.assertThat(it.bookLists).isEmpty()
                Truth.assertThat(it.isLoading).isTrue()
            }

            turbine.awaitItem().let {
                Truth.assertThat(it.bookLists).hasSize(listBookLists.size)
                Truth.assertThat(it.bookLists[0].books).hasSize(2)
                Truth.assertThat(it.bookLists[1].books).isEmpty()
                Truth.assertThat(it.bookLists[2].books).isEmpty()
            }
            turbine.awaitItem().let {
                Truth.assertThat(it.bookLists[0].books).hasSize(2)
                Truth.assertThat(it.bookLists[1].books).hasSize(1)
                Truth.assertThat(it.bookLists[2].books).isEmpty()
            }
            turbine.awaitItem().let {
                Truth.assertThat(it.bookLists[0].books).hasSize(2)
                Truth.assertThat(it.bookLists[1].books).hasSize(1)
                Truth.assertThat(it.bookLists[2].books).hasSize(1)
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
            coEvery { repository.getBookLists(any()) } throws unknownHostException

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
                Truth.assertThat(it.bookLists).isEmpty()
                Truth.assertThat(it.isLoading).isFalse()
            }
            errorTurbine.awaitItem().let {
                Truth.assertThat(it).isEqualTo(NetworkError.FailedToReachServer)
            }
        }
    }
}

