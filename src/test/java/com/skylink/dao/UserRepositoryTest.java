
package com.skylink.dao;

import com.skylink.entity.Role;
import com.skylink.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1, user2, user3;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user1 = new User("Alice", "alice@example.com", "9876543210", "pass123", Role.CUSTOMER);
        user2 = new User("Bob", "bob@example.com", "9876543211", "pass123", Role.ADMIN);
        user3 = new User("Charlie", "charlie@example.com", "9876543212", "pass123", Role.CUSTOMER);

        user2.setActive(false); // inactive user
        userRepository.saveAll(List.of(user1, user2, user3));
    }

    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("alice@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
    }

    @Test
    void testFindByMobileNumber() {
        Optional<User> found = userRepository.findByMobileNumber("9876543211");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Bob");
    }

    @Test
    void testExistsByEmail() {
        assertThat(userRepository.existsByEmail("bob@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
    }

    @Test
    void testExistsByMobileNumber() {
        assertThat(userRepository.existsByMobileNumber("9876543212")).isTrue();
        assertThat(userRepository.existsByMobileNumber("1234567890")).isFalse();
    }

    @Test
    void testFindByRole() {
        List<User> customers = userRepository.findByRole(Role.CUSTOMER);
        assertThat(customers).hasSize(2);
    }

    @Test
    void testFindByActiveTrue() {
        List<User> activeUsers = userRepository.findByActiveTrue();
        assertThat(activeUsers).hasSize(2); // Alice & Charlie
    }

    @Test
    void testFindActiveUsersByRole() {
        List<User> activeCustomers = userRepository.findActiveUsersByRole(Role.CUSTOMER);
        assertThat(activeCustomers).hasSize(2); // Alice & Charlie
    }

    @Test
    void testCountByRole() {
        long adminCount = userRepository.countByRole(Role.ADMIN);
        assertThat(adminCount).isEqualTo(1);
    }

    @Test
    void testSearchByNameOrEmail() {
        List<User> results = userRepository.searchByNameOrEmail("Ali", "none");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Alice");

        List<User> resultsByEmail = userRepository.searchByNameOrEmail("none", "charlie@example.com");
        assertThat(resultsByEmail).hasSize(1);
        assertThat(resultsByEmail.get(0).getName()).isEqualTo("Charlie");
    }
}
