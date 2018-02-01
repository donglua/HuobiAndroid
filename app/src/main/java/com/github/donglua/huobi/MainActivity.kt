package com.github.donglua.huobi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.donglua.huobi.ws.OkWebSocketClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val webSocket = OkWebSocketClient.instance.init()

        webSocket.send("{\n" +
                "  \"req\": \"market.btcusdt.kline.1min\",\n" +
                "  \"id\": \"id10\"\n" +
                "}")
    }
}
