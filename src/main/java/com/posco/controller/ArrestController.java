package com.posco.controller;

import com.posco.algorithm.AlgorithmService;
import com.posco.domain.Algorithm;
import com.posco.domain.CategoryCode;
import com.posco.domain.Node;
import com.posco.dto.ArrestRequest;
import com.posco.dto.ArrestNodeResponse;
import com.posco.dto.kakao.KakaoApiResponse;
import com.posco.service.ArrestService;
import com.posco.service.KakaoSearchService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArrestController {

    private final KakaoSearchService kakaoSearchService;
    private final ArrestService arrestService;
    private final AlgorithmService algorithmService;

    @Autowired
    public ArrestController(KakaoSearchService kakaoSearchService, ArrestService arrestService,
                            AlgorithmService algorithmService) {
        this.kakaoSearchService = kakaoSearchService;
        this.arrestService = arrestService;
        this.algorithmService = algorithmService;
    }

    @GetMapping("/api/v1/places")
    public ResponseEntity<KakaoApiResponse> getPlace(@RequestParam String code) {
        KakaoApiResponse response = kakaoSearchService.requestSearch(CategoryCode.findByCode(code));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/nodes")
    public ResponseEntity<List<Node>> getAllNodes() {
        List<Node> response = algorithmService.findAll();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/arrests")
    public ResponseEntity<ArrestNodeResponse> arrest(@RequestBody ArrestRequest request, @RequestParam String code) {
        ArrestNodeResponse response = arrestService.arrest(request, Algorithm.findByCode(code));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/registrations")
    public ResponseEntity<Void> saveArrest() {

        return ResponseEntity.noContent().build();
    }
}
