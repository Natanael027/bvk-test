package com.tech.test.repository;

import com.tech.test.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.name = ?1")
    public User findByName(String name);

    @Query("SELECT u FROM User u WHERE u.parent.id is NULL")
    public List<User> findRootCategories(Sort sort);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public Optional<User> findByEmail(String email);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = ?1")
    public void deleteUserById(Integer id);

    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
