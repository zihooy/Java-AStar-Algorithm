package com.posco.domain;

import java.util.Arrays;

public enum Algorithm {

    다익스트라("dijkstra"),
    CSTAR("cstar"),
    BSTAR("bstar"),
    ASTAR("astar");
    private final String code;

    Algorithm(String code) {
        this.code = code;
    }

    public static Algorithm findByCode(String code) {
        return Arrays.stream(values())
                .filter(c -> c.code.equals(code))
                .findFirst()
                .orElseThrow();
    }

    public String getCode() {
        return code;
    }
}


