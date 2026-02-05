package com.example.demo.services;

import com.example.demo.entities.Request;
import com.example.demo.repositories.RequestRepository;
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
