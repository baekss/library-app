package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
  private val userRepository: UserRepository,
  private val userService: UserService
) {

  @BeforeEach
  fun clean() {
    userRepository.deleteAll();
  }

  @Test
  fun saveUserTest() {
    // given
    val request = UserCreateRequest("장합", null)

    // when
    userService.saveUser(request)

    // then
    val results = userRepository.findAll()
    assertThat(results).hasSize(1)
    val user: User = results[0]
    assertThat(user.name).isEqualTo("장합")
    assertThat(user.age).isNull()
  }

  @Test
  fun getUsersTest() {
    // given
    userRepository.saveAll(listOf(
      User("감녕", 20),
      User("학소", null)
    ))

    // when
    val results = userService.getUsers()

    // then
    assertThat(results).hasSize(2)
    assertThat(results).extracting("name").containsExactlyInAnyOrder("감녕", "학소")
    assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)
  }

  @Test
  fun updateUserNameTest() {
    // given
    val savedUser = userRepository.save(User("손책", 20))
    val request = UserUpdateRequest(savedUser.id, "손권")

    // when
    userService.updateUserName(request)

    // then
    val result = userRepository.findAll()[0]
    assertThat(result.name).isEqualTo("손권")
  }

  @Test
  fun deleteUserTest() {
    // given
    userRepository.save(User("손책", 20))

    // when
    userService.deleteUser("손책")

    // then
    assertThat(userRepository.findAll()).isEmpty()
  }
}
