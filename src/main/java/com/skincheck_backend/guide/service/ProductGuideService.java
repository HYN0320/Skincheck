package com.skincheck_backend.guide.service;


import com.skincheck_backend.guide.repository.ProductGuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductGuideService {
    private final ProductGuideRepository productGuideRepository;
}
