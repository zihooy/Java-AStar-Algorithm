package com.posco.service;

import com.posco.algorithm.AlgorithmService;
import com.posco.domain.Algorithm;
import com.posco.dto.ArrestRequest;
import com.posco.dto.ResultResponse;
import com.posco.dto.ArrestNodeResponse;
import org.springframework.stereotype.Service;

@Service
public class ArrestService {

    private final AlgorithmService algorithmService;

    public ArrestService(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    public ArrestNodeResponse arrest(ArrestRequest request, Algorithm code) {
        String color = RandomColorGenerator.randomColorGenerate();
        ResultResponse response = getNodesByCode(request, code);
        return new ArrestNodeResponse(color, response.getCost(), code.getCode(), response.getNodes());
    }

    private ResultResponse getNodesByCode(ArrestRequest request, Algorithm code) {
        switch (code){
            case 다익스트라: return algorithmService.findWithDijkstra(request.getStartX(), request.getStartY(), request.getEndX(), request.getEndY(), request.getObstacleList());
            case CSTAR: return algorithmService.findWithCStar(request.getStartX(), request.getStartY(), request.getEndX(), request.getEndY(), request.getObstacleList());
            case BSTAR: return algorithmService.findWithBStar(request.getStartX(), request.getStartY(), request.getEndX(), request.getEndY(), request.getObstacleList());
            default: return algorithmService.findWithAStar(request.getStartX(), request.getStartY(), request.getEndX(), request.getEndY(), request.getObstacleList());
        }
    }
}
