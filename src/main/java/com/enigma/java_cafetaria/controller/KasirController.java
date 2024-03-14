package com.enigma.java_cafetaria.controller;

import com.enigma.java_cafetaria.constant.AppPath;
import com.enigma.java_cafetaria.dto.requets.AuthRequest;
import com.enigma.java_cafetaria.dto.requets.CustomerRequest;
import com.enigma.java_cafetaria.dto.requets.KasirRequest;
import com.enigma.java_cafetaria.dto.response.*;
import com.enigma.java_cafetaria.service.KasirService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppPath.KASIR)
@RequiredArgsConstructor
public class KasirController {

    private final KasirService kasirService;

    @PostMapping
    public ResponseEntity<?> createKasir(@RequestBody AuthRequest request) {
        KasirResponse kasirResponse = kasirService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<KasirResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully created new Kasir")
                        .data(kasirResponse)
                        .build());
    }

    @PutMapping
    public ResponseEntity<?> updateKasir(@RequestBody KasirRequest kasirRequest) {
        KasirResponse kasirResponse = kasirService.update(kasirRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<KasirResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully Updated Kasir")
                        .data(kasirResponse)
                        .build());
    }

    @GetMapping
    public ResponseEntity<?> getAllKasir() {
        List<KasirResponse> kasirList = kasirService.getAll();

        return ResponseEntity.ok(
                CommonResponse.<List<KasirResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully retrieved all kasir")
                        .data(kasirList)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getKasirById(@PathVariable String id) {
        KasirResponse kasirResponse = kasirService.getById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<KasirResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get kasir by id")
                        .data(kasirResponse)
                        .build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKasir(@PathVariable String id) {
        kasirService.delete(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully Delete Kasir")
                        .data(HttpStatus.OK)
                        .build());
    }

}
