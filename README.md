# Java-AStar-Algorithm
Implement A* Algorithm Using Java, OSM, Kakaomap API


- 브레인스토밍
    - A* 알고리즘을 구현하는 방식에 대해 토의
    - 최단 경로 탐색이 중요한 경우에 대해 고민
        - 공장, 택배, 맛집 탐색
        - 신체, 우주
        - 범인 추적
    - 실제 지도 위에서 알고리즘을 적용하는 것을 목표로 구현
        
        ⇒ OSM Library, Kakao Map API, html을 활용하여 간단한 웹사이트 제작
        
        [Kakao 지도 API](https://apis.map.kakao.com/)
        
        [Export | OpenStreetMap](https://www.openstreetmap.org/export#map=16/37.4968/127.0294)
        
- 강남 지역의 OSM 정보
    - OSM Structure
        - Node = 각 사거리
            
            ```java
            public long osmId;
            public double latitude;
            public double longitude;
            ```
            
        - Edge
            
            ```java
            public int headNode;
            public double length;
            public double travelTime;
            ```
            
            ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/47da377c-f131-4ecc-bdc1-fb395d255524/Untitled.png)
            
    - Total number of nodes and edges:
    - nodes: 14081
    - edges: 29299
    - 구현하기 전 전처리작업
        - 실제 강남 지역의 node와 edge를 사용하다보니 edge가 없는 node도 존재해서, 
        connected component만 고려하도록 전처리
    - Total number of connected nodes and edges
        
        - nodes: 5163
        - edges: 11828
        
- Dijkstra 알고리즘 → A* 알고리즘
    - 모든 node를 탐색하는 알고리즘 = Dijkstra
        - Priority Queue를 활용
        - 부모와 연결된 모든 자식 노드 탐색
        - `public double computeShortestPathCost(int sourceNodeId, int targetNodeId)`
        - `public ArrayList<Integer> getShortestPath(int source, int target)`
    - 이후에 Dijkstra에 Huristic 함수를 구현하여 A* 로 응용
    - Dijkstra Algorithm 참고 코드
        
        [https://github.com/aminefalek/osm-to-graph](https://github.com/aminefalek/osm-to-graph)
        
- A* 알고리즘 구현
    - A* 알고리즘이란
        - A* 알고리즘은 Heuristic 함수를 활용하여 탐색
        - 각 노드에 대해 실제 비용과 Heuristic 함수의 추정 비용을 고려하여 가장 유망한 노드를 선택하고, 이를 통해 목적지에 도달하는 최단 경로를 탐색
        - Heuristic 함수:  각 노드에서 목적지까지의 예상 비용을 제공하는 함수로, A* 알고리즘의 핵심 개념
    - Heuristic 계산하기
        - Heuristic은 예상 비용 또는 추정치로서 사용
        - 현재 노드에서 목표 노드까지의 실제 비용을 고려하여 얼마나 멀리 떨어져 있는지
        - 차별점: Haversine Distance를 활용
        Haversine Distance는 지구상에서 두 지점 사이의 거리를 구하는 데 유용하게 사용되기 때문에, 위치 기반 서비스나 지리 정보 시스템에서 주로 활용되며, 경로 탐색이나 거리 기반 검색 등에 유용하다.
    - Haversine Distance
        - 두 지점 간의 구면 좌표계에서의 거리를 계산하기 위한 공식
        - 두 지점의 위도와 경도를 사용하여 계산
        - 구면 위에서의 직선 거리가 아니라 구의 표면을 따라 이동하는 거리로서, 지구의 곡률을 고려하여 두 지점 사이의 실제 거리를 근사적으로 계산
        - Haversine Distance 공식
            
            ```java
            a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)
            c = 2 * atan2(√a, √(1-a))
            d = R * c
            ```
            
            **`lat1`**, **`lat2`**는 각각 첫 번째 지점과 두 번째 지점의 위도이고, **`lon1`**, **`lon2`**는 경도이다.
            
            **`Δlat`**은 위도의 차이, **`Δlon`**은 경도의 차이이고, **`R`**은 지구의 반지름을 나타냄
            
- A* 알고리즘의 문제
    1. 전처리 과정에서, A* 알고리즘은 부모와 연결된 edge만 탐색하다보니 edge가 없는 node를 탐색하지 못함
    2. Heuristic이 Grid 좌표에서는 최적의 효율을 나타내지만, 실제 지도(구면)에 적용했을 때는 edge의 방향이 다양해지는 등의 문제가 발생
- B* 알고리즘 구현
    - 1번 문제: 전처리 과정에서, A* 알고리즘은 부모와 연결된 edge만 탐색하다보니 edge가 없는 node를 탐색하지 못함
    - 1번 문제 해결: 새로운 source node & target node를 생성
        - 임의로 source node에서 새로운 source node까지 edge를 새로 생성
        - source node로부터 20m 내의 모든 node를 source node로 지정
        - target node로부터 20m 내의 모든 node를 target node로 지정
        - → 모든 경우에 A* 알고리즘을 적용하여 가장 최단 경로를 탐색
- C* 알고리즘 구현
    - 2번 문제: Huristic이 Grid 좌표에서는 최적의 효율을 나타내지만, 실제 지도(구면)에 적용했을 때는 edge의 방향이 다양해지는 등의 문제가 발생
    - 2번 문제 해결: Heuristic 함수 수정
        - 가중치를 1/2, 1/4로 수정한 후 A* 알고리즘에 적용
        - 결과적으로 가중치가 1/2일때 가장 효율적임을 확
- 추가 기능 구현
    1. ~~도둑의 위치가 변경되는 경우~~
        1. ~~도둑의 위치가 변경되는 경우 event 발생하여 새로운 도둑의 위도/경도 전달~~
        2. ~~event가 감지되면 알고리즘에서 탐색중이던 현재 node 저장~~
        3. ~~저장된 node를 source로 지정~~
        4. ~~전달받은 도둑의 위치에서 가장 가까운 node 탐색 후 target으로 지정~~
        5. ~~새로운 source와 target으로 알고리즘 다시 시작~~
        
        ~~→ 경찰이 이동중인 경우 도둑의 위치가 변하면?~~
        
    2. 장애물이 있는 경우
        1. 도둑과 경찰의 위치를 정하고 마우스 우클릭으로 새로운 장애물들을 설치 
        → 이벤트가 발생할 때마다 각 장애물들의 위도/경도를 nodes라는 배열에 담음
        → 체포 버튼을 누를 때 도둑, 경찰, 장애물의 위치 정보를 한번에 전달
        2. 장애물 배열을 받아 위도/경도를 가진 리스트로 저장(obstacleList) 후 전체 node에서 장애물 node를 제외함
        3. 장애물 하나를 기준으로 반경 15미터 안에 있는 node들을 모두 장애물에 포함시켜 그 근처(공사장)로 못가게 제한
    3. 성능 평가
        1. 화면 왼쪽에 Dijkstra, A*, B*, C*의 cost값을 비교할 수 있도록 함
- 예시화면
    
    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/e644ed23-cd33-469d-b62b-a6e2aad9ee33/Untitled.png)
    
    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/bcd813fb-2231-44fb-9ba3-ea9b2bb79c5d/Untitled.png)
    
    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/c450a6cf-967a-4513-8c1a-8e695f04fded/Untitled.png)
    
    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/856ee9a2-547a-40c3-aca3-dd39c277ff7e/Untitled.png)
    
- 테스트케이스
    1. 양재파출소 < - > 역삼역 신한은행 강남 중앙지점
    2. 서초현대렉시온 < - > 강남 센트럴 아이파크 앞
    
- 코드 구조
    
    ![KakaoTalk_Photo_2023-05-18-15-11-55.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/f0716948-72d8-450c-af65-e4b36b84b10e/KakaoTalk_Photo_2023-05-18-15-11-55.png)
    
    - DTO
    - Service
    - Controller
    - Algorithm
        - Node, Edge, Vertex, Road Network
