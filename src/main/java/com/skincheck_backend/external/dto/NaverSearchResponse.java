package com.skincheck_backend.external.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class NaverSearchResponse {

    private List<NaverShoppingItem> items;
}
