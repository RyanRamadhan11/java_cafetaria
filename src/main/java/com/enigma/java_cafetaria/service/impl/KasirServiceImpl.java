package com.enigma.java_cafetaria.service.impl;

import com.enigma.java_cafetaria.constant.ERole;
import com.enigma.java_cafetaria.dto.requets.AuthRequest;
import com.enigma.java_cafetaria.dto.requets.KasirRequest;
import com.enigma.java_cafetaria.dto.response.CustomerResponse;
import com.enigma.java_cafetaria.dto.response.KasirResponse;
import com.enigma.java_cafetaria.dto.response.RegisterResponse;
import com.enigma.java_cafetaria.entity.Customer;
import com.enigma.java_cafetaria.entity.Kasir;
import com.enigma.java_cafetaria.repository.KasirRepository;
import com.enigma.java_cafetaria.service.KasirService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class KasirServiceImpl implements KasirService {
    private final KasirRepository kasirRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;

    @Override
    public KasirResponse create(AuthRequest request) {
        try {
            //TODO 1: Set Role
            String roleId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_role (id, name) VALUES (?, ?)")
                    .setParameter(1, roleId)
                    .setParameter(2, ERole.ROLE_KASIR.name())
                    .executeUpdate();

            //TODO 2: Set Credential
            String userCredentialId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_user_credential (id, username, password, role_id) VALUES (?, ?, ?, ?)")
                    .setParameter(1, userCredentialId)
                    .setParameter(2, request.getUsername())
                    .setParameter(3, passwordEncoder.encode(request.getPassword()))
                    .setParameter(4, roleId)
                    .executeUpdate();

            //TODO 3: Set Kasir
            String kasirId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_kasir (id, user_credential_id, name, address, email, phone) VALUES (?, ?, ?, ?, ?, ?)")
                    .setParameter(1, kasirId)
                    .setParameter(2, userCredentialId)
                    .setParameter(3, request.getCustomerName())
                    .setParameter(4, request.getAddress())
                    .setParameter(5, request.getEmail())
                    .setParameter(6, request.getMobilePhone())
                    .executeUpdate();

            // Ambil data customer yang baru ditambahkan dari database menggunakan query
            Kasir newKasir = entityManager.find(Kasir.class, kasirId);

            return KasirResponse.builder()
                    .id(newKasir.getId())
                    .kasirName(request.getCustomerName())
                    .address(request.getAddress())
                    .email(request.getEmail())
                    .phone(request.getMobilePhone())
                    .build();

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kasir already exists");
        }
    }

    @Override
    public KasirResponse update(KasirRequest kasirRequest) {
        // Menggunakan native query untuk menemukan Kasir berdasarkan ID
        Query findKasirQuery = entityManager.createNativeQuery(
                        "SELECT * FROM m_kasir WHERE id = ?", Kasir.class)
                .setParameter(1, kasirRequest.getId());

        // Eksekusi query native untuk mendapatkan Customer
        List<Kasir> resultList = findKasirQuery.getResultList();

        if (!resultList.isEmpty()) {
            Kasir existingKasir = resultList.get(0);

            // Menggunakan native query untuk update
            Query updateQuery = entityManager.createNativeQuery(
                            "UPDATE m_kasir SET name = ?, address = ?, phone = ?, email = ? WHERE id = ?")
                    .setParameter(1, kasirRequest.getName())
                    .setParameter(2, kasirRequest.getAddress())
                    .setParameter(3, kasirRequest.getMobilePhone())
                    .setParameter(4, kasirRequest.getEmail())
                    .setParameter(5, kasirRequest.getId());

            // Eksekusi query native untuk update
            updateQuery.executeUpdate();

            // Setelah eksekusi, ambil data terbaru dari database
            Kasir updatedKasir = entityManager.find(Kasir.class, kasirRequest.getId());

            // Kembalikan response
            return KasirResponse.builder()
                    .id(updatedKasir.getId())
                    .kasirName(updatedKasir.getName())
                    .address(updatedKasir.getAddress())
                    .email(updatedKasir.getEmail())
                    .phone(updatedKasir.getPhoneNumber())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
    }

    @Override
    public void delete(String id) {
        // Menggunakan native query untuk menghapus
        Query nativeQuery = entityManager.createNativeQuery("DELETE FROM m_kasir WHERE id = ?")
                .setParameter(1, id);

        // Eksekusi query native
        int deletedRows = nativeQuery.executeUpdate();

        if (deletedRows > 0) {
            System.out.println("Delete kasir success");
        } else {
            System.out.println("ID not found");
        }
    }

    @Override
    public KasirResponse getById(String id) {
        // Gunakan native query untuk mendapatkan data customer berdasarkan ID
        Object[] result = (Object[]) entityManager.createNativeQuery(
                "SELECT id, name, address, phone, email FROM m_kasir WHERE id = ?")
                .setParameter(1, id)
                .getSingleResult();

        if (result != null) {
            return KasirResponse.builder()
                    .id((String) result[0])
                    .kasirName((String) result[1])
                    .address((String) result[2])
                    .phone((String) result[3])
                    .email((String) result[4])
                    .build();
        }
        return null;
    }

    @Override
    public List<KasirResponse> getAll() {
        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT id, name, address, phone, email FROM m_kasir")
                .getResultList();

        return resultList.stream()
                .map(row -> KasirResponse.builder()
                        .id((String) row[0])
                        .kasirName((String) row[1])
                        .address((String) row[2])
                        .phone((String) row[3])
                        .email((String) row[4])
                        .build())
                .collect(Collectors.toList());
    }

}
