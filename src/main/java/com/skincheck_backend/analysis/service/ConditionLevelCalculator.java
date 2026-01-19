package com.skincheck_backend.analysis.service;

import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import org.springframework.stereotype.Component;

@Component
public class ConditionLevelCalculator {

    /**
     * AIHub 한국인 피부 측정 데이터(measurement_data.csv) 기반 기준
     *
     * 모든 value는 "0 ~ 100 정규화 점수"를 전제로 한다.
     * (AI 서버 또는 변환 레이어에서 이미 정규화되어 들어온다고 가정)
     */
    public ConditionLevel calc(ConditionType type, int value) {
        return switch (type) {

            /**
             * 수분 (MOISTURE)
             * - 4부위 평균 (이마/좌볼/우볼/턱)
             * - 값이 높을수록 좋음
             */
            case MOISTURE -> {
                if (value < 54) yield ConditionLevel.BAD;      // 하위 25%
                else if (value < 61) yield ConditionLevel.LOW;
                else if (value < 68) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;                // 상위 25%
            }

            /**
             * 탄력 (ELASTICITY)
             * - Q0 기준 (0~100)
             * - 값이 높을수록 좋음
             */
            case ELASTICITY -> {
                if (value < 55) yield ConditionLevel.BAD;
                else if (value < 65) yield ConditionLevel.LOW;
                else if (value < 75) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }

            /**
             * 모공 (PORE)
             * - 원본: 0~3000
             * - 서비스 점수: (평균 모공 개수 / 1500) * 100
             * - 값이 높을수록 나쁨
             */
            case PORE -> {
                if (value >= 100) yield ConditionLevel.BAD;
                else if (value >= 67) yield ConditionLevel.LOW;
                else if (value >= 40) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }

            /**
             * 색소침착 (PIGMENTATION)
             * - 원본: 0~300 count
             * - 서비스 점수: (count / 300) * 100
             * - 값이 높을수록 나쁨
             */
            case PIGMENTATION -> {
                if (value >= 100) yield ConditionLevel.BAD;
                else if (value >= 60) yield ConditionLevel.LOW;
                else if (value >= 30) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }

            /**
             * 트러블 (ACNE)
             * - 개수/심각도 기반 점수화
             * - 값이 높을수록 나쁨
             */
            case ACNE -> {
                if (value >= 80) yield ConditionLevel.BAD;
                else if (value >= 60) yield ConditionLevel.LOW;
                else if (value >= 30) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }
        };
    }
}
