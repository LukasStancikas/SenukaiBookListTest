package com.lukasstancikas.booklists

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import java.time.OffsetDateTime

internal val listBooks = listOf(
    Book(
        1,
        1,
        "The old man and the sea ",
        "https://covers.openlibrary.org/b/id/7884851-L.jpg"
    ),
    Book(
        2,
        1,
        "The old man and the sea 1",
        "https://covers.openlibrary.org/b/id/7884851-L.jpg"
    ),
    Book(
        2,
        2,
        "The old man and the sea 2",
        "https://covers.openlibrary.org/b/id/7884851-L.jpg"
    ),
    Book(
        3,
        3,
        "The old man and the sea 3",
        "https://covers.openlibrary.org/b/id/7884851-L.jpg"
    )
)
internal val listBookLists = listOf(
    BookList(
        1,
        "First List"
    ),
    BookList(
        2,
        "Second List"
    ),
    BookList(
        3,
        "Third List"
    ),
)

internal val bookDetails = Book(
    1,
    1,
    "First Book Ever made",
    "",
    "Some Description",
    "Some Author",
    "01110",
    OffsetDateTime.now()
)
internal val bookDetails2 = Book(
    2,
    1,
    "First Book Ever made",
    "",
    "Some Description",
    "Some Author",
    "01110",
    OffsetDateTime.now()
)