package com.shipcore.organization.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {

    private Long id;
    private String name;
    private String ruc;
    private String address;
    private String phone;
    private Boolean active;

}