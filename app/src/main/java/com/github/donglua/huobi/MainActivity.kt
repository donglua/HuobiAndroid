package com.github.donglua.huobi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.donglua.huobi.ws.OkWebSocketClient
import okhttp3.WebSocket

class MainActivity : AppCompatActivity() {
    private lateinit var webSocket: WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webSocket = OkWebSocketClient.instance.init()

        webSocket.send("{\n" +
                "  \"req\": \"market.btcusdt.kline.1min\",\n" +
                "  \"id\": \"id10\"\n" +
                "}")
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.cancel()
    }
}
