package com.posco.service;

import com.posco.domain.CategoryCode;
import com.posco.dto.kakao.KakaoApiResponse;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoSearchService {

    public static final String KAKAO_AK = "KakaoAK ";
    private final RestTemplate restTemplate;

    private final KakaoUriBuilderService kakaoUriBuilderService;

    @Autowired
    public KakaoSearchService(RestTemplate restTemplate, KakaoUriBuilderService kakaoUriBuilderService) {
        this.restTemplate = restTemplate;
        this.kakaoUriBuilderService = kakaoUriBuilderService;
    }

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    public KakaoApiResponse requestSearch(CategoryCode code){
        URI uri = null;
        switch (code){
            case 은행: uri = kakaoUriBuilderService.builderUriByCategorySearch(code);
                    break;
            case 경철서: uri = kakaoUriBuilderService.builderUriByKeywordSearch(code);
                    break;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, KAKAO_AK + kakaoRestApiKey);
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), KakaoApiResponse.class).getBody();
    }
}
