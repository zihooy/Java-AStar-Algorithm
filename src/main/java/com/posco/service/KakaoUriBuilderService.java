package com.posco.service;

import com.posco.domain.CategoryCode;
import java.net.URI;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoUriBuilderService {

    private static final String KAKAO_LOCAL_CATEGORY_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/category.json";
    private static final String KAKAO_LOCAL_KEYWORD_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    private static final String 강남역_경찰서 = "강남역 경찰서";
    private static final String 강남구_X좌표 = "127.03221660594751";

    private static final String 강남구_Y좌표 = "37.49388566996322";
    private static final String 조회거리 = "10000"; //10km

    public URI builderUriByCategorySearch(CategoryCode code){
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_CATEGORY_SEARCH_URL);
        uriBuilder.queryParam("category_group_code", code.getCode());
        uriBuilder.queryParam("x", 강남구_X좌표);
        uriBuilder.queryParam("y", 강남구_Y좌표);
        uriBuilder.queryParam("radius", 조회거리);

        URI uri = uriBuilder.build().encode().toUri();
        return uri;
    }

    public URI builderUriByKeywordSearch(CategoryCode code){
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_KEYWORD_SEARCH_URL);
        uriBuilder.queryParam("query", 강남역_경찰서);
        uriBuilder.queryParam("category_group_code", code.getCode());
        uriBuilder.queryParam("size", 10);
        URI uri = uriBuilder.build().encode().toUri();
        return uri;
    }

}
