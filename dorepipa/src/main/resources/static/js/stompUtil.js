var StompUtil = function(){};
var sup = StompUtil.prototype;
sup.sendTimer = 3000;

/**
 * エンドポイントへの接続処理
 */
sup.connect = function () {
    var socket = new ReconnectingWebSocket('ws://' + location.host + '/pac2017/endpoint'); // エンドポイントのURL
    this.stompClient = Stomp.over(socket); // WebSocketを使ったStompクライアントを作成
    this.stompClient.connect({}, this.onConnected.bind(this)); // エンドポイントに接続し、接続した際のコールバックを登録
};

/**
 * エンドポイントへ接続したときの処理
 */
sup.onConnected = function (frame) {
    console.log('Connected: ' + frame);
    // 宛先が'/topic/greetings'のメッセージを購読し、コールバック処理を登録
    this.stompClient.subscribe('/topic/test', this.onSubscribeTest.bind(this));
    this.onConnectedFinaly();
};

/**
 * 接続完了したときの処理
 */
sup.onConnectedFinaly = function () {
};

/**
 * 宛先'/topic/stateChanged'なメッセージを受信したときの処理
 */
sup.onSubscribeTest = function (message) {
};


/**
 * 接続切断処理
 */
sup.disconnect = function () {
    if (this.stompClient) {
        this.stompClient.disconnect();
        this.stompClient = null;
    }
};
