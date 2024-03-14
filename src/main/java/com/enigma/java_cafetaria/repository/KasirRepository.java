package com.enigma.java_cafetaria.repository;

import com.enigma.java_cafetaria.entity.Admin;
import com.enigma.java_cafetaria.entity.Kasir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KasirRepository extends JpaRepository<Kasir, String> {
}
