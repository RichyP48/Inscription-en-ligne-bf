package com.richardmogou.repository;

import com.richardmogou.model.entity.AcademicHistory;
import com.richardmogou.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicHistoryRepository extends JpaRepository<AcademicHistory, Long> {

    // Find all academic history entries for a specific user
    // Ordering can be specified here or rely on the @OrderBy in the User entity
    List<AcademicHistory> findByUserOrderByStartDateDesc(User user);

    // Find by ID and User (useful for checking ownership before update/delete)
    // Optional<AcademicHistory> findByIdAndUser(Long id, User user);
    // JpaRepository already provides findById, ownership check should be done in service layer

}