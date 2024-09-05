package ru.savin.rest_api_aws_s3.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.savin.rest_api_aws_s3.model.Status;
import ru.savin.rest_api_aws_s3.model.User;
import ru.savin.rest_api_aws_s3.model.UserRole;
import ru.savin.rest_api_aws_s3.repositiry.UserRepository;
import ru.savin.rest_api_aws_s3.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}


	@Test
	void save() {

		User user = new User();
		user.setPassword("plainPassword");

		// Настраиваем поведение кодировщика паролей
		when(passwordEncoder.encode(user.getPassword())).thenReturn("plainPassword");

		// Настраиваем поведение репозитория
		when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

		Mono<User> result = userService.save(user);

		StepVerifier.create(result)
				.expectNext(user)  // Проверяем, что возвращается сохраненный пользователь
				.expectComplete()
				.verify();

		// Проверяем, что метод encode был вызван с правильным паролем
		verify(passwordEncoder).encode(user.getPassword());

		// Проверяем, что метод save в репозитории был вызван с правильным пользователем
		verify(userRepository).save(user);

	}

	@Test
	void update() {

		User existingUser = new User();
		existingUser.setId(1L);
		existingUser.setPassword("oldPassword");

		User userToUpdate = new User();
		userToUpdate.setId(1L);
		userToUpdate.setPassword("newPassword");

		// Настраиваем поведение мока для репозитория
		when(userRepository.findById(1L)).thenReturn(Mono.just(existingUser));
		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
		when(userRepository.save(any(User.class))).thenAnswer(t -> Mono.just(t.getArgument(0)));

		Mono<User> result = userService.update(userToUpdate);

		StepVerifier.create(result)
				.expectNextMatches(user -> {
					// Проверяем, что пароль был закодирован
					return "encodedNewPassword".equals(user.getPassword()) && user.getId().equals(1L);
				})
				.expectComplete()
				.verify();

		// Проверяем, что метод encode был вызван с правильным паролем
		verify(passwordEncoder).encode("newPassword");

		// Проверяем, что метод save в репозитории был вызван с правильным пользователем
		verify(userRepository).save(userToUpdate);

	}

	@Test
	void getById() {

		User user = User.builder()
				.Id(1L)
				.username("test")
				.build();

		when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

		Mono<User> result = userService.getById(user.getId());

		StepVerifier.create(result)
				.expectNextMatches(foundUser -> {
					// Проверяем, что найденный пользователь совпадает с ожидаемым
					return foundUser.getId().equals(1L) && "test".equals(foundUser.getUsername());
				})
				.expectComplete()
				.verify();
	}

	@Test
	void getAll() {

		User user = User.builder()
				.Id(1L)
				.username("test")
				.build();

		User user2 = User.builder()
				.Id(2L)
				.username("test2")
				.build();

		List<User> users = Arrays.asList(user, user2);

		when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));

		Flux<User> result = userService.getAll();

		StepVerifier.create(result)
				.expectNext(user)
				.expectNext(user2)
				.expectComplete()
				.verify();
	}

	@Test
	void deleteById() {

		Long idToDelete = 1L;
		when(userRepository.deleteById(idToDelete)).thenReturn(Mono.empty());
		Mono<Void> result = userService.deleteById(idToDelete);

		StepVerifier.create(result)
				.expectComplete()
				.verify();

		verify(userRepository).deleteById(idToDelete);

	}

	@Test
	void findByUserName() {

		User user = User.builder()
				.Id(1L)
				.username("test")
				.build();

		when(userRepository.findByUsername(user.getUsername())).thenReturn(Mono.just(user));

		Mono<User> result = userService.findByUserName(user.getUsername());

		StepVerifier.create(result)
				.expectNext(user)  // Ожидаем, что Mono содержит пользователя
				.expectComplete()  // Ожидаем, что Mono завершится успешно
				.verify();

	}

	@Test
	void registerUser() {

		User inputUser = User.builder()
				.username("testUser")
				.password("plainPassword")
				.build();

		User savedUser = User.builder()
				.username("testUser")
				.password("encodedPassword")
				.role(UserRole.ADMIN)
				.status(Status.ACTIVE)
				.build();

		when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
		when(userRepository.save(savedUser)).thenReturn(Mono.just(savedUser));

		Mono<User> result = userService.registerUser(inputUser);

		StepVerifier.create(result)
				.expectNext(savedUser)  // Ожидаем, что Mono содержит сохраненного пользователя
				.expectComplete()  // Ожидаем, что Mono завершится успешно
				.verify();

	}
}