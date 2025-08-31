package com.richardmogou.repository;

import com.richardmogou.model.entity.PersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalInfoRepository extends JpaRepository<PersonalInfo, Long> {
    // Basic CRUD methods are inherited from JpaRepository.
    // Add custom query methods here if needed later, e.g.:
    // Optional<PersonalInfo> findByUserId(Long userId); // Already implicitly handled by findById since ID is shared
}