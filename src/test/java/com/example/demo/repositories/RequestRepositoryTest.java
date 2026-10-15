package com.example.demo.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import com.example.demo.entities.Request;
import com.example.demo.images.PhotoCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void savesRequestWithCategoryAndCreationTime() {
        Request saved = requestRepository.saveAndFlush(validRequest());

        Request loaded = requestRepository.findById(saved.getId()).orElseThrow();
        assertThat(loaded.getCategory()).isEqualTo(PhotoCategory.AUTO);
        assertThat(loaded.getEmail()).isEqualTo("max@example.com");
        assertThat(loaded.getCreatedAt()).isNotNull();
    }

    @Test
    void rejectsRequestWithoutRequiredFields() {
        Request request = validRequest();
        request.setEmail(null);

        assertThatThrownBy(() -> requestRepository.saveAndFlush(request))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private static Request validRequest() {
        Request request = new Request(PhotoCategory.AUTO);
        request.setSalutation("Herr");
        request.setFirstName("Max");
        request.setLastName("Mustermann");
        request.setEmail("max@example.com");
        request.setDate(LocalDate.now().plusDays(7));
        request.setComment("Oldtimer, gerne bei Sonnenuntergang");
        return request;
    }
}
