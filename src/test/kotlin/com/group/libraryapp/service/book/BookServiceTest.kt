package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
  private val bookService: BookService,
  private val bookRepository: BookRepository,
  private val userRepository: UserRepository,
  private val userLoanHistoryRepository: UserLoanHistoryRepository
) {

  companion object {
    @JvmStatic
    private val 책이름: String = "삼국지연의"
    @JvmStatic
    private val 인물명: String = "장합"
  }

  @BeforeEach
  fun clean() {
    bookRepository.deleteAll()
    userRepository.deleteAll()
  }

  @Test
  fun saveBook() {
    // given
    val request = BookRequest(책이름)

    // when
    bookService.saveBook(request)

    // then
    val results = bookRepository.findAll()
    assertThat(results).hasSize(1)
    val book = results[0]
    assertThat(book.name).isEqualTo(책이름)
  }

  @Test
  fun loanBookTest() {
    // given
    bookRepository.save(Book(책이름))
    val savedUser = userRepository.save(User(인물명, null))
    val request = BookLoanRequest(인물명, 책이름)

    // when
    bookService.loanBook(request)

    // then
    val results = userLoanHistoryRepository.findAll()
    assertThat(results).hasSize(1)
    assertThat(results[0].bookName).isEqualTo(책이름)
    assertThat(results[0].user.id).isEqualTo(savedUser.id)
    assertThat(results[0].isReturn).isFalse
  }

  @Test
  fun loanBookFailTest() {
    // given
    bookRepository.save(Book(책이름))
    userRepository.save(User(인물명, null))
    val request = BookLoanRequest(인물명, 책이름)
    bookService.loanBook(request)

    // when & then
    assertThrows<IllegalArgumentException> { bookService.loanBook(request) }
  }

  @Test
  fun returnBookTest() {
    // given
    bookRepository.save(Book(책이름))
    userRepository.save(User(인물명, null))
    bookService.loanBook(BookLoanRequest(인물명, 책이름))
    val request = BookReturnRequest(인물명, 책이름)

    // when
    bookService.returnBook(request)

    // then
    val results = userLoanHistoryRepository.findAll()
    assertThat(results).hasSize(1)
    assertThat(results[0].isReturn).isTrue
  }
}
