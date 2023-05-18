var container = document.getElementById('map');
var options = {
    center: new kakao.maps.LatLng(37.50081053, 127.03368504),
    level: 5
};
var map = new kakao.maps.Map(container, options);
var lineStore = [];
var nodeStore = [];
var policeStore = [];
var thiefStore = [];
var obstacleStore = [];
var policeCarStore = [];


var obstacle = [];
var click = 0;

var thiefImage = 'https://cdn-icons-png.flaticon.com/512/4321/4321425.png';
var policeImage = 'https://cdn-icons-png.flaticon.com/512/2991/2991186.png'
var policeCarImage = 'https://velog.velcdn.com/images/zihooy/post/e4a09917-c89d-485b-b27a-0fcf7ab8dd69/image.png';
var obstacleImage = 'https://cdn.pixabay.com/photo/2012/04/25/01/45/sign-41667_960_720.png';

kakao.maps.event.addListener(map, 'rightclick', (mouseEvent) => { // 우클릭 적용
    var latlng = mouseEvent.latLng;

    var imageSrc = 'https://cdn.pixabay.com/photo/2012/04/25/01/45/sign-41667_960_720.png';
    var imageSize = new kakao.maps.Size(40, 40);
    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);

    obstacleStore.push(
        new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(latlng.getLat(),latlng.getLng()),
        image: markerImage
    }));

    var x = latlng.La;
    var y = latlng.Ma;
    var node = {x, y};
    obstacle.push(node);
});


function onClickEventGetPlace(code){
    $.ajax({
        url: "/api/v1/places?code=" + code,
        type: 'GET',
    }).done((data) => {
        onHandleCreatePlaceMarkers(data.documents);
    });
}

function onClickEventPostArrest(code){
    $.ajax({
        url: "/api/v1/arrests?code=" + code,
        type: 'POST',
        data: JSON.stringify({
            startX: policeMarker.x,
            startY: policeMarker.y,
            endX : thiefMarker.x,
            endY : thiefMarker.y,
            obstacleList: obstacle,
        }),
        dataType: 'JSON',
        contentType: "application/json; charset=utf-8",
    }).done((data) => {
        let color = null;
        switch (data.algorithm) {
            case 'dijkstra':
                color = 'red';
                break;
            case 'astar':
                color = 'orange';
                break;
            case 'bstar':
                color = 'blue';
                break;
            case 'cstar':
                color = 'green';
                break;
        }

        onHandleCreateLines(data.nodes, color);
        //추가
        onHandlerChangeAlgorithmResult(data.cost, data.algorithm);
    });
}

function onClickEventGetNodes(){
    $.ajax({
        url: "/api/v1/nodes",
        type: 'GET',
    }).done((data) => {
        onHandlerCreateMarker(data);
    });
}

//추가
function onHandlerChangeAlgorithmResult(cost, algorithm) {
    console.log({cost, algorithm});
    switch (algorithm) {
        case 'dijkstra':
            var element = document.getElementById('D_cost');
            element.innerText = cost.toFixed(2);
            break;
        case 'astar':
            var element = document.getElementById('A_cost');
            element.innerText = cost.toFixed(2);
            break;
        case 'bstar':
            var element = document.getElementById('B_cost');
            element.innerText = cost.toFixed(2);
            break;
        case 'cstar':
            var element = document.getElementById('C_cost');
            element.innerText = cost.toFixed(2);
            break;
    }
}


function onHandlerCreateMarker(nodes){
    for (var i = 0; i < nodes.length; i++) {
        nodeStore.push(
            new kakao.maps.Marker({
            map:map,
            position: new kakao.maps.LatLng(nodes[i].y, nodes[i].x)
        }));
    }
}



function drawLineSequentially(lines, color){
    var currentIndex = 0;
    var imageSize = new kakao.maps.Size(40, 40);
    var markers = [];
    function drawNextLine() {
        var partialLine = []
        for(var i=0;i<markers.length;i++){
            markers[i].setMap(null);
        }

        partialLine.push(lines[currentIndex])
        partialLine.push(lines[currentIndex + 1])

        lineStore.push(
            new kakao.maps.Polyline({
                map: map,
                path: partialLine,
                strokeWeight: 3,
                strokeColor: color,
                strokeOpacity: 0.7,
                strokeStyle: 'solid'
            })
        )

        var m =  new kakao.maps.Marker({
            map: map,
            position: lines[currentIndex+1],
            image: new kakao.maps.MarkerImage(policeCarImage, imageSize),
        });
        markers.push(m);
        policeCarStore.push(m);

        currentIndex++;
        if(currentIndex+1 < lines.length){
            setTimeout(drawNextLine, 300); // 일정 시간 후에 다음 선 그리기 호출
        }
        else{
            console.log("끝");
        }
    }
    drawNextLine();
}

function onHandleCreateLines(nodes, color) {
    var lines = []
    for (var i = 0; i < nodes.length; i++) {
        lines.push(new kakao.maps.LatLng(nodes[i].y, nodes[i].x));
    }
    drawLineSequentially(lines, color);
}


function onClickEventClear(){
    for (let i = 0; i < lineStore.length; i++) {
        lineStore[i].setMap(null);
    }
    for (let i = 0; i < nodeStore.length; i++) {
        nodeStore[i].setMap(null);
    }
    for (let i = 0; i < policeStore.length; i++) {
        policeStore[i].setMap(null);
    }
    for (let i = 0; i < thiefStore.length; i++) {
        thiefStore[i].setMap(null);
    }
    for (let i = 0; i < obstacleStore.length; i++) {
        obstacleStore[i].setMap(null);
    }
    for (let i = 0; i < policeCarStore.length; i++) {
        policeCarStore[i].setMap(null);
    }
    thiefMarker = null;
    policeMarker = null;

    //추가
    document.getElementById('D_cost').innerText = "0.00";
    document.getElementById('A_cost').innerText = "0.00";
    document.getElementById('B_cost').innerText = "0.00";
    document.getElementById('C_cost').innerText = "0.00";
}

function onHandleCreatePlaceMarkers(documents){
    for (var i = 0; i < documents.length; i++){
        addPlaceMarker(documents[i].y,documents[i].x, documents[i].place_name, documents[i].category_group_name);
    }
}

var thiefMarker = null;
var policeMarker = null;

function addPlaceMarker(y, x, content, category) {
    var position = new kakao.maps.LatLng(y, x);
    var imageSrc = category === "은행" ?
            'https://cdn-icons-png.flaticon.com/512/2830/2830284.png' : "https://cdn-icons-png.flaticon.com/512/1669/1669667.png",
        imageSize = new kakao.maps.Size(40, 40);

    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);

    var marker = new kakao.maps.Marker({
        map: map,
        position: position,
        content: content,
        image: markerImage
    });

    kakao.maps.event.addListener(marker, 'click', () => {
        var imageSrc = category === "은행"? 'https://cdn-icons-png.flaticon.com/512/4321/4321425.png' : 'https://cdn-icons-png.flaticon.com/512/2991/2991186.png',
            imageSize = new kakao.maps.Size(40, 40);
        var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);
        if (thiefMarker === null && category === "은행") {
            thiefStore.push(new kakao.maps.Marker({
                map: map,
                position: position,
                image: markerImage
            }));
            thiefMarker = {y , x};
        }
        if(policeMarker === null && category === "공공기관"){
            policeStore.push(
                new kakao.maps.Marker({
                    map: map,
                    position: position,
                    image: markerImage
                })
            )
            policeMarker = {y, x};
        }
    });
}