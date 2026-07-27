package com.shipcore.business.domain.service;

import com.shipcore.business.api.dto.request.CarrierRateRequest;
import com.shipcore.business.api.dto.response.CarrierRateResponse;

import java.util.List;

public interface CarrierRateService {

    CarrierRateResponse create(CarrierRateRequest request);

    List<CarrierRateResponse> findAll();

    CarrierRateResponse findById(Long id);

    CarrierRateResponse update(Long id, CarrierRateRequest request);

    void delete(Long id);

    List<CarrierRateResponse> findVersions(Long carrierId, String zone);

}
