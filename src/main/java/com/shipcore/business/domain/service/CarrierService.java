package com.shipcore.business.domain.service;

import com.shipcore.business.api.dto.request.CarrierRequest;
import com.shipcore.business.api.dto.response.CarrierResponse;

import java.util.List;

public interface CarrierService {

    CarrierResponse create(CarrierRequest request);

    List<CarrierResponse> findAll();

    CarrierResponse findById(Long id);

    CarrierResponse update(Long id, CarrierRequest request);

    void delete(Long id);

}
