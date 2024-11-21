package com.tech.test;

import com.tech.test.entity.User;
import com.tech.test.repository.UserRepository;
import com.tech.test.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestApplicationTests {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;
	
	@Test
	@Order(1)
	void contextLoads() {
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setName("testing");
		user.setPassword("123456");
		user.setEnabled(true);
		userService.createUser(user);

	}

	@Test
	@Order(2)
	void createUserSub() {
//		User parent = new User(1 );
		User parent = userRepository.findById(1).get();
		User user = new User("testing1", parent);
		user.setEmail("test1@gmail.com");
		user.setPassword("123456");
		user.setEnabled(true);

		userService.createUser(user);
	}

	@Test
	void testCreateChildren(){
		User user1 = userRepository.findById(2).get();
		User user2 = userRepository.findById(3).get();
		user1.addChildren(user2);
		userService.updateUser(user1.getId(), user1);
	}

	@Test
	void testCreateAllParent(){
		User user1 = userRepository.findById(3).get();
		user1.updateAllParentIDs(); // Update allParentIDs for all users
		userService.updateUser(user1.getId(), user1);
	}



	@Test
	@Order(3)
	void createUserSub2() {
		User parent = new User(2 );
		User user = new User("testing2", parent);
		user.setEmail("test2@gmail.com");
		user.setPassword("123456");
		user.setEnabled(true);

		userService.createUser(user);
	}

	@Test
	@Order(4)
	public void testGetUser() {
		User user = userRepository.findById(2).get();
		System.out.println(user.getName());
		Set<User> children = user.getChildren();

		for (User subUser : children) {
			System.out.println(subUser.getName());
		}
	}

	@Test
	public void testGetUserParent() {
		User user = userRepository.findById(4).get();
		System.out.println(user);
		var parents = user.getParent();
		System.out.println(parents);
	}

	@Test
	@Order(5)
	public void testPrintHierarchicalCategories() {
		Iterable<User> users = userRepository.findAll();

		for (User user : users) {
			System.out.println(user.getName());
			if (user.getParent() == null) {
//				System.out.println(user.getName());

				Set<User> children = user.getChildren();

				for (User subUser : children) {
					System.out.println("--" + subUser.getName());
					printChildren(subUser, 1);
				}
			}
		}
	}

	private void printChildren(User parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<User> children = parent.getChildren();

		for (User subUser : children) {
			for (int i = 0; i < newSubLevel; i++) {
				System.out.print("--");
			}

			System.out.println(subUser.getName());

			printChildren(subUser, newSubLevel);
		}
	}

	@Test
	@Order(6)
	public void testListRootCategories() {
		List<User> rootCategories = userRepository.findRootCategories(Sort.by("name").ascending());
		rootCategories.forEach(cat -> System.out.println(cat.getName()));
	}

	@Test
	public void testDeleteUser(){
		userService.deleteUser(4);
	}
}
