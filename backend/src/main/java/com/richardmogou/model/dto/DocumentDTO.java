package com.richardmogou.model.dto;

import com.richardmogou.model.entity.DocumentStatus;
import com.richardmogou.model.entity.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {

    private Long id;
    private DocumentType documentType;
    private String originalFilename;
    private Long fileSize; // Size in bytes
    private String contentType; // MIME type
    private DocumentStatus status;
    private LocalDateTime uploadedAt;
    private LocalDateTime validatedAt;
    private String validationNotes;

    // We generally don't expose the storedFilename directly in list/get DTOs
    // unless specifically needed by the client for actions like download/delete.
    // It might be better to use the 'id' for those actions.
}