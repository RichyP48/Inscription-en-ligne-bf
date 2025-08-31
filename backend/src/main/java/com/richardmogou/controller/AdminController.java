package com.richardmogou.controller;

import com.richardmogou.exception.ResourceNotFoundException;
import com.richardmogou.model.dto.ApplicationDetailDTO;
import com.richardmogou.model.dto.DocumentDTO;
import com.richardmogou.model.dto.DocumentStatusUpdateDTO;
import com.richardmogou.model.dto.UserSummaryDTO;
import com.richardmogou.model.entity.ApplicationStatus;
import com.richardmogou.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// Note: Access to this controller is already restricted to ROLE_ADMIN
// by the SecurityConfig rule for "/api/admin/**".
// @PreAuthorize("hasRole('ADMIN')") // Could be added for extra method-level security if needed

@RestController
@RequestMapping("/api/admin/users") // Base path for user management by admin
@RequiredArgsConstructor
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<Page<UserSummaryDTO>> getAllUsers(Pageable pageable) {
        // Pageable object is automatically populated by Spring from request parameters
        // (e.g., ?page=0&size=20&sort=email,asc)
        log.info("Admin request received to list users with pagination: {}", pageable);
        try {
            Page<UserSummaryDTO> userPage = adminService.getAllUsers(pageable);
            return ResponseEntity.ok(userPage);
        } catch (Exception e) {
            log.error("Error retrieving users for admin: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve users", e);
        }
    }

    @GetMapping("/{userId}/application")
    public ResponseEntity<ApplicationDetailDTO> getApplicationDetails(@PathVariable Long userId) {
        log.info("Admin request received for application details of user ID: {}", userId);
        try {
            return adminService.getApplicationDetails(userId)
                    .map(ResponseEntity::ok) // If found, return 200 OK with the DTO
                    .orElseThrow(() -> { // If not found, throw 404
                        log.warn("Application details not found for user ID: {}", userId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Application details not found for user ID: " + userId);
                    });
        } catch (ResponseStatusException rse) {
            throw rse; // Re-throw specific exceptions
        } catch (Exception e) {
            log.error("Error retrieving application details for user ID {}: {}", userId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve application details", e);
        }
    }

    // Endpoint to update document status (could be in a separate DocumentAdminController)
    @PutMapping("/documents/{documentId}/status")
    public ResponseEntity<?> updateDocumentStatus(@PathVariable Long documentId,
                                                  @Valid @RequestBody DocumentStatusUpdateDTO statusUpdateDTO) {
        log.info("Admin request received to update status for document ID: {} to {}", documentId, statusUpdateDTO.getNewStatus());
        try {
            DocumentDTO updatedDocument = adminService.updateDocumentStatus(documentId, statusUpdateDTO);
            log.info("Document ID: {} status updated successfully to {}", documentId, updatedDocument.getStatus());
            return ResponseEntity.ok(updatedDocument);
        } catch (ResourceNotFoundException e) {
            log.warn("Update status failed, document ID {} not found: {}", documentId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.warn("Update status failed for document ID {}: {}", documentId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage()); // Return 400 for invalid transitions/data
        } catch (Exception e) {
            log.error("Error updating status for document ID {}: {}", documentId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update document status", e);
        }
    }


    // Add other admin endpoints here later:
    // - GET /api/admin/applications -> List applications (might be different from users)
    // - GET /api/admin/dashboard/stats
    // - GET /api/admin/applications/export?format=csv

    /**
     * Admin endpoint to update a user's application status.
     *
     * @param userId The ID of the user whose status to update.
     * @param newStatus The new application status (PENDING, APPROVED, REJECTED).
     * @return The updated UserSummaryDTO.
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateUserApplicationStatus(@PathVariable Long userId,
                                                         @RequestBody ApplicationStatus newStatus) {
        log.info("Admin request received to update application status for user ID: {} to {}", userId, newStatus);
        try {
            UserSummaryDTO updatedUser = adminService.updateUserApplicationStatus(userId, newStatus);
            log.info("User ID: {} application status updated successfully to {}", userId, updatedUser.getApplicationStatus());
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            log.warn("Update application status failed, user ID {} not found: {}", userId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.warn("Update application status failed for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage()); // Return 400 for invalid status/transitions
        } catch (Exception e) {
            log.error("Error updating application status for user ID {}: {}", userId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update application status", e);
        }
    }

    // --- Dashboard Statistics Endpoints ---

    @GetMapping("/dashboard/completion-rate")
    public ResponseEntity<String> getCompletionRateLast30Days() { // Change return type to String
        log.debug("Admin request received for 30-day completion rate");
        try {
            String rate = adminService.getApplicationCompletionRateLast30Days(); // Expect String
            return ResponseEntity.ok(rate);
        } catch (Exception e) {
            log.error("Error calculating completion rate: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to calculate completion rate", e);
        }
    }

    @GetMapping("/dashboard/total-applications")
    public ResponseEntity<Long> getTotalApplicationsCount() {
        log.debug("Admin request received for total applications count");
        try {
            long count = adminService.getTotalApplicationsCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error getting total applications count: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get total applications count", e);
        }
    }

    @GetMapping("/dashboard/pending-count")
    public ResponseEntity<Long> getPendingApplicationsCount() {
        log.debug("Admin request received for pending applications count");
        try {
            long count = adminService.getPendingApplicationsCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error getting pending applications count: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get pending applications count", e);
        }
    }

    @GetMapping("/dashboard/rejected-count")
    public ResponseEntity<Long> getRejectedApplicationsCount() {
        log.debug("Admin request received for rejected applications count");
        try {
            long count = adminService.getRejectedApplicationsCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error getting rejected applications count: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get rejected applications count", e);
        }
    }

    @GetMapping("/dashboard/approved-count")
    public ResponseEntity<Long> getApprovedApplicationsCount() {
        log.debug("Admin request received for approved applications count");
        try {
            long count = adminService.getApprovedApplicationsCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error getting rejected applications count: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get rejected applications count", e);
        }
    }
}