package com.richardmogou.repository;

import com.richardmogou.model.entity.Document;
import com.richardmogou.model.entity.DocumentType;
import com.richardmogou.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Find all documents uploaded by a specific user
    List<Document> findByUser(User user);

    // Find all documents uploaded by a specific user, ordered by upload time
    List<Document> findByUserOrderByUploadedAtDesc(User user);

    // Find a specific document type for a specific user
    // Useful for checking if a required non-repeatable document exists (e.g., ID_PHOTO)
    Optional<Document> findByUserAndDocumentType(User user, DocumentType documentType);

    // Find documents by user and a list of types (e.g., find all diplomas for a user)
    List<Document> findByUserAndDocumentTypeIn(User user, List<DocumentType> documentTypes);

    // Find a document by its unique stored filename (useful for retrieval/deletion)
    Optional<Document> findByStoredFilename(String storedFilename);

}