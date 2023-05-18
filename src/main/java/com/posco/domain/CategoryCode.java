package com.posco.domain;

import java.util.Arrays;

public enum CategoryCode {

    경철서("PO3"),
    은행("BK9");

    private final String code;

    CategoryCode(String code) {
        this.code = code;
    }

    public static CategoryCode findByCode(String code) {
        return Arrays.stream(values())
                .filter(c -> c.code.equals(code))
                .findFirst()
                .orElseThrow();
    }

    public String getCode() {
        return code;
    }
}
