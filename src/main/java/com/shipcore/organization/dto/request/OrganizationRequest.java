package com.shipcore.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrganizationRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "El RUC es obligatorio")
    @Size(min = 11, max = 11)
    private String ruc;

    @Size(max = 255)
    private String address;

    @Size(max = 20)
    private String phone;

}