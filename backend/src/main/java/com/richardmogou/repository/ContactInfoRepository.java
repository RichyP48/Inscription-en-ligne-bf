package com.richardmogou.repository;

import com.richardmogou.model.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> {
    // Basic CRUD methods are inherited.
    // findById(userId) will retrieve the contact info for a specific user
    // due to the shared primary key (@MapsId).
}