package de.torbenphotography.services;

import de.torbenphotography.entities.Request;
import de.torbenphotography.repositories.RequestRepository;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public Request save(Request request) {
        return requestRepository.save(request);
    }
}
