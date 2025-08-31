package com.richardmogou.service;

import com.richardmogou.model.dto.DocumentDTO;
import com.richardmogou.model.entity.DocumentType;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    /**
     * Stores an uploaded document for the authenticated user.
     * Performs validation checks (file type, size).
     *
     * @param file           The uploaded file.
     * @param documentType   The type of the document being uploaded.
     * @param authentication The current user's authentication object.
     * @return DocumentDTO representing the saved document metadata.
     * @throws com.richardmogou.exception.FileStorageException if storage or validation fails.
     * @throws IllegalArgumentException if document type constraints are violated.
     */
    DocumentDTO storeDocument(MultipartFile file, DocumentType documentType, Authentication authentication);

    /**
     * Retrieves metadata for all documents uploaded by the authenticated user.
     *
     * @param authentication The current user's authentication object.
     * @return A list of DocumentDTOs.
     */
    List<DocumentDTO> getUserDocuments(Authentication authentication);

    /**
     * Retrieves metadata for a specific document by its ID, ensuring it belongs to the authenticated user.
     *
     * @param documentId     The ID of the document to retrieve.
     * @param authentication The current user's authentication object.
     * @return DocumentDTO representing the document metadata.
     * @throws com.richardmogou.exception.FileNotFoundException if the document is not found or doesn't belong to the user.
     */
    DocumentDTO getDocumentMetadata(Long documentId, Authentication authentication);


    /**
     * Loads a specific document file as a Resource, ensuring it belongs to the authenticated user.
     *
     * @param documentId     The ID of the document to load.
     * @param authentication The current user's authentication object.
     * @return The Resource object for the file.
     * @throws com.richardmogou.exception.FileNotFoundException if the document or file is not found or doesn't belong to the user.
     */
    Resource loadDocumentAsResource(Long documentId, Authentication authentication);

    /**
     * Deletes a specific document (both metadata and the physical file),
     * ensuring it belongs to the authenticated user.
     *
     * @param documentId     The ID of the document to delete.
     * @param authentication The current user's authentication object.
     * @throws com.richardmogou.exception.FileNotFoundException if the document is not found or doesn't belong to the user.
     * @throws com.richardmogou.exception.FileStorageException if file deletion fails.
     */
    void deleteDocument(Long documentId, Authentication authentication);

}