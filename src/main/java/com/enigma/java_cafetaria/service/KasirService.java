package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.dto.requets.AuthRequest;
import com.enigma.java_cafetaria.dto.requets.KasirRequest;
import com.enigma.java_cafetaria.dto.response.KasirResponse;
import com.enigma.java_cafetaria.entity.Customer;

import java.util.List;

public interface KasirService {

    KasirResponse create(AuthRequest request);

    KasirResponse update(KasirRequest kasirRequest);

    void delete(String id);

    KasirResponse getById(String id);

    List<KasirResponse> getAll();
}