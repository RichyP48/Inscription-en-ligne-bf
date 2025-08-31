package com.richardmogou.service;

import com.richardmogou.model.dto.ApplicationDetailDTO;
import com.richardmogou.model.dto.DocumentDTO;
import com.richardmogou.model.dto.DocumentStatusUpdateDTO;
import com.richardmogou.model.dto.UserSummaryDTO;
import com.richardmogou.model.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    /**
     * Retrieves a paginated list of all users (or potentially filtered by role, e.g., applicants).
     *
     * @param pageable Pagination information (page number, size, sort).
     * @return A Page containing UserSummaryDTOs.
     */
    Page<UserSummaryDTO> getAllUsers(Pageable pageable);

    /**
     * Retrieves a list of all users (or potentially filtered by role, e.g., applicants).
     * Note: Use pagination (getAllUsers) for potentially large user bases.
     *
     * @return A List containing UserSummaryDTOs.
     */
    List<UserSummaryDTO> getAllUsersList(); // Simple list version, use with caution

    /**
     * Retrieves the full application details for a specific user by their ID.
     * Includes personal info, contact info, academic history, and documents.
     *
     * @param userId The ID of the user whose application details are to be retrieved.
     * @return An Optional containing the ApplicationDetailDTO if the user is found, otherwise empty.
     */
    Optional<ApplicationDetailDTO> getApplicationDetails(Long userId);

    /**
     * Updates the status of a specific document.
     * Typically used by admins to mark documents as VALIDATED or REJECTED.
     *
     * @param documentId The ID of the document to update.
     * @param statusUpdateDTO DTO containing the new status and optional notes.
     * @return The updated DocumentDTO.
     * @throws com.richardmogou.exception.ResourceNotFoundException if the documentId is not found.
     * @throws IllegalArgumentException if the status transition is invalid.
     */
    DocumentDTO updateDocumentStatus(Long documentId, DocumentStatusUpdateDTO statusUpdateDTO);


    // We will add methods later for:
    // - Exporting data

    /**
     * Calculates the application completion rate (percentage of APPROVED users in the last 30 days).
     *
     * @return The completion rate as a string formatted to two decimal places (e.g., "75.50").
     */
    String getApplicationCompletionRateLast30Days(); // Change return type to String

    /**
     * Gets the total count of applications (users with the APPLICANT role).
     *
     * @return The total number of applicant users.
     */
    long getTotalApplicationsCount();

    /**
     * Gets the count of applications with PENDING status.
     *
     * @return The number of pending applications.
     */
    long getPendingApplicationsCount();

    /**
     * Gets the count of applications with REJECTED status.
     *
     * @return The number of rejected applications.
     */
    long getRejectedApplicationsCount();

    long getApprovedApplicationsCount();


    /**
     * Updates the application status for a specific user.
     *
     * @param userId The ID of the user whose status is to be updated.
     * @param newStatus The new application status.
     * @return The updated UserSummaryDTO.
     * @throws com.richardmogou.exception.ResourceNotFoundException if the user is not found.
     * @throws IllegalArgumentException if the status transition is invalid (if rules are implemented).
     */
    UserSummaryDTO updateUserApplicationStatus(Long userId, ApplicationStatus newStatus);
}