package com.skincheck_backend.auth.dto;


import com.skincheck_backend.common.enumtype.SkinConcernType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class SignupRequest {

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String name;

    private String gender;
    private Integer birthYear;
    private List<SkinConcernType> concerns;
}
