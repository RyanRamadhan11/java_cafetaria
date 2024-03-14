package com.enigma.java_cafetaria.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class KasirResponse {
    private String id;
    private String kasirName;
    private String address;
    private String phone;
    private String email;

}