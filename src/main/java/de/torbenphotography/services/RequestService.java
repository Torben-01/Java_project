package de.torbenphotography.services;

import java.util.List;
import java.util.Locale;

import de.torbenphotography.entities.Request;
import de.torbenphotography.entities.RequestStatus;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.repositories.RequestRepository;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestService {

    public static final String ADMIN_ROLE = "ADMIN";

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public Request save(Request request) {
        return requestRepository.save(request);
    }

    /*
     * Returns the requests matching the filter, newest first. Every filter value may be null.
     * category PhotoCategory only requests of this category
     * status RequestStatus only requests with this status
     * search String text contained in first name, last name or e-mail
     */
    @RolesAllowed(ADMIN_ROLE)
    public List<Request> find(PhotoCategory category, RequestStatus status, String search) {
        String needle = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        return requestRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .filter(request -> category == null || request.getCategory() == category)
                .filter(request -> status == null || request.getStatus() == status)
                .filter(request -> needle.isEmpty() || contains(request, needle))
                .toList();
    }

    @RolesAllowed(ADMIN_ROLE)
    @Transactional
    public void changeStatus(Request request, RequestStatus status) {
        requestRepository.findById(request.getId()).ifPresent(stored -> {
            stored.setStatus(status);
            request.setStatus(status);
        });
    }

    @RolesAllowed(ADMIN_ROLE)
    public void delete(Request request) {
        requestRepository.deleteById(request.getId());
    }

    private static boolean contains(Request request, String needle) {
        return (request.getFirstName() + " " + request.getLastName() + " " + request.getEmail())
                .toLowerCase(Locale.ROOT)
                .contains(needle);
    }
}
