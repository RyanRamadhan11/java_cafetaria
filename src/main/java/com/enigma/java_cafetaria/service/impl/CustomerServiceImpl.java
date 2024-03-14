package com.enigma.java_cafetaria.service.impl;

import com.enigma.java_cafetaria.constant.ERole;
import com.enigma.java_cafetaria.dto.requets.AuthRequest;
import com.enigma.java_cafetaria.dto.requets.CustomerRequest;
import com.enigma.java_cafetaria.dto.response.CustomerResponse;
import com.enigma.java_cafetaria.entity.Customer;
import com.enigma.java_cafetaria.entity.UserCredential;
import com.enigma.java_cafetaria.repository.CustomerRepository;
import com.enigma.java_cafetaria.service.CustomerService;
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
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<CustomerResponse> getAll() {
        // Gunakan native query untuk mendapatkan semua data customer
        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT id, name, address, mobile_phone, email FROM m_customer")
                .getResultList();

        return resultList.stream()
                .map(row -> CustomerResponse.builder()
                        .id((String) row[0])
                        .customerName((String) row[1])
                        .address((String) row[2])
                        .phone((String) row[3])
                        .email((String) row[4])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse createNewCustomer(Customer request) {
        // Generate UUID sebagai ID baru
        String newCustomerId = UUID.randomUUID().toString();

        // Gunakan native query untuk menyimpan data customer baru
        entityManager.createNativeQuery(
                        "INSERT INTO m_customer (id, name, address, mobile_phone, email) " +
                                "VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, newCustomerId)
                .setParameter(2, request.getName())
                .setParameter(3, request.getAddress())
                .setParameter(4, request.getMobilePhone())
                .setParameter(5, request.getEmail())
                .executeUpdate();

        // Ambil data customer yang baru ditambahkan dari database menggunakan query
        Customer newCustomer = entityManager.find(Customer.class, newCustomerId);

        // Kembalikan response
        return CustomerResponse.builder()
                .id(newCustomer.getId())
                .customerName(newCustomer.getName())
                .phone(newCustomer.getMobilePhone())
                .build();
    }

    @Override
    public CustomerResponse create(AuthRequest request) {
        try {
            //TODO 1 : Set Role
            String roleId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_role (id, name) VALUES (?, ?)")
                    .setParameter(1, roleId)
                    .setParameter(2, ERole.ROLE_CUSTOMER.name())
                    .executeUpdate();

            //TODO 2 : Set credential
            String userCredentialId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_user_credential (id, username, password, role_id) VALUES (?, ?, ?, ?)")
                    .setParameter(1, userCredentialId)
                    .setParameter(2, request.getUsername())
                    .setParameter(3, passwordEncoder.encode(request.getPassword()))
                    .setParameter(4, roleId)
                    .executeUpdate();

            //TODO 3 : Set customer
            String customerId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_customer (id, user_credential_id, name, address, email, mobile_phone) VALUES (?, ?, ?, ?, ?, ?)")
                    .setParameter(1, customerId)
                    .setParameter(2, userCredentialId)
                    .setParameter(3, request.getCustomerName())
                    .setParameter(4, request.getAddress())
                    .setParameter(5, request.getEmail())
                    .setParameter(6, request.getMobilePhone())
                    .executeUpdate();

            // Ambil data customer yang baru ditambahkan dari database menggunakan query
            Customer newCustomer = entityManager.find(Customer.class, customerId);

            return CustomerResponse.builder()
                    .id(newCustomer.getId())
                    .customerName(request.getCustomerName())
                    .address(request.getAddress())
                    .email(request.getEmail())
                    .phone(request.getMobilePhone())
                    .build();

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer already exist");
        }
    }

    @Override
    public CustomerResponse update(CustomerRequest customerRequest) {
        // Menggunakan native query untuk menemukan Customer berdasarkan ID
        Query findCustomerQuery = entityManager.createNativeQuery(
                        "SELECT * FROM m_customer WHERE id = ?", Customer.class)
                .setParameter(1, customerRequest.getId());

        // Eksekusi query native untuk mendapatkan Customer
        List<Customer> resultList = findCustomerQuery.getResultList();

        if (!resultList.isEmpty()) {
            Customer existingCustomer = resultList.get(0);

            // Menggunakan native query untuk update
            Query updateQuery = entityManager.createNativeQuery(
                            "UPDATE m_customer SET name = ?, address = ?, mobile_phone = ?, email = ? WHERE id = ?")
                    .setParameter(1, customerRequest.getName())
                    .setParameter(2, customerRequest.getAddress())
                    .setParameter(3, customerRequest.getMobilePhone())
                    .setParameter(4, customerRequest.getEmail())
                    .setParameter(5, customerRequest.getId());

            // Eksekusi query native untuk update
            updateQuery.executeUpdate();

            // Setelah eksekusi, ambil data terbaru dari database
            Customer updatedCustomer = entityManager.find(Customer.class, customerRequest.getId());

            // Kembalikan response
            return CustomerResponse.builder()
                    .id(updatedCustomer.getId())
                    .customerName(updatedCustomer.getName())
                    .address(updatedCustomer.getAddress())
                    .phone(updatedCustomer.getMobilePhone())
                    .email(updatedCustomer.getEmail())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
    }


    @Override
    public CustomerResponse getById(String id) {
        // Gunakan native query untuk mendapatkan data customer berdasarkan ID
        Object[] result = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, name, address, mobile_phone, email FROM m_customer WHERE id = ?")
                .setParameter(1, id)
                .getSingleResult();

        if (result != null) {
            return CustomerResponse.builder()
                    .id((String) result[0])
                    .customerName((String) result[1])
                    .address((String) result[2])
                    .phone((String) result[3])
                    .email((String) result[4])
                    .build();
        }
        return null;
    }

    @Override
    public void delete(String id) {
        // Gunakan native query untuk menghapus data customer berdasarkan ID
        int deletedRows = entityManager.createNativeQuery(
                        "DELETE FROM m_customer WHERE id = ?")
                .setParameter(1, id)
                .executeUpdate();

        if (deletedRows > 0) {
            System.out.println("delete succeed");
        } else {
            System.out.println("id not found");
        }
    }
}
