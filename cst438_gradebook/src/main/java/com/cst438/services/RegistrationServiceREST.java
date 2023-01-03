package com.cst438.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.CourseDTOG;

import java.net.URI;
import java.net.URISyntaxException;

public class RegistrationServiceREST extends RegistrationService {
    RestTemplate restTemplate = new RestTemplate();

    @Value("${registration.url}")
    String registration_url;

    public RegistrationServiceREST() {
        System.out.println("REST registration service ");
    }

    @Override
    public void sendFinalGrades(int course_id, CourseDTOG courseDTO) throws URISyntaxException {
        System.out.println("sending final grades " + course_id + " " + courseDTO);

        URI putPath = new URI(registration_url + "/course/" + course_id);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<CourseDTOG> entity = new HttpEntity<>(courseDTO, headers);
        ResponseEntity<Boolean> response = restTemplate.exchange(putPath, HttpMethod.PUT, entity, Boolean.class);

        HttpStatus status = response.getStatusCode();
        System.out.println("sendFinalGrades response status: " + status);
    }
}