package com.skincheck_backend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkinStatusDto {

    private int moisture;       // 수분
    private int elasticity;     // 탄력
    private int pore;           // 모공
    private int pigmentation;   // 색소/톤
}
