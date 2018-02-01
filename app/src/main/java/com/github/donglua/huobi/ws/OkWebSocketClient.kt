package com.github.donglua.huobi.ws

import okhttp3.*
import java.io.IOException
import java.nio.charset.Charset

import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import kotlin.properties.Delegates
import okhttp3.RequestBody
import okhttp3.internal.http.HttpHeaders
import okio.*
import okio.GzipSource
import okio.Okio
import okhttp3.internal.http.RealResponseBody
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import javax.net.SocketFactory


/**
 * OkHttp WebSocket Client
 *
 * Created by donglua on 18-2-1.
 */

class OkWebSocketClient private constructor() : WebSocketListener() {

    private object Holder { val INSTANCE = OkWebSocketClient() }

    fun init(): WebSocket {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    Timber.d("----- addInterceptor")
                    val userRequest = chain.request()
                    val requestBuilder = userRequest.newBuilder()

                    val body = userRequest.body()
                    if (body != null) {
                        val contentType = body.contentType()
                        if (contentType != null) {
                            requestBuilder.header("Content-Type", contentType.toString())
                        }
                        val contentLength = body.contentLength()
                        if (contentLength != -1L) {
                            requestBuilder.header("Content-Length", java.lang.Long.toString(contentLength))
                            requestBuilder.removeHeader("Transfer-Encoding")
                        } else {
                            requestBuilder.header("Transfer-Encoding", "chunked")
                            requestBuilder.removeHeader("Content-Length")
                        }
                    }
                    val networkResponse = chain.proceed(chain.request())
                    val responseBuilder = networkResponse.newBuilder().request(userRequest)
                    if (HttpHeaders.hasBody(networkResponse)) {
                        Timber.d("----- hasBody")
                        val responseBody = GzipSource(networkResponse.body()!!.source())
                        val contentType = networkResponse.header("Content-Type")
                        responseBuilder.body(RealResponseBody(contentType, -1L, Okio.buffer(responseBody)))
                    }
                    return@addInterceptor responseBuilder.build();
                }
                .addNetworkInterceptor(logging)
                .build()
        val request = Request.Builder()
                .url(WS_URL)
                .build()
        return client.newWebSocket(request, this)
    }

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        Timber.d("onOpen -> %s", response?.body()?.string())
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        Timber.d("onMessage text -> %s", text)
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
        val input: InputStream = ByteArrayInputStream(bytes.toByteArray())
        val source = GzipSource(Okio.source(input))
        val msg = Okio.buffer(source).readUtf8()
        Timber.d("onMessage bytes -> %s", msg)
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        Timber.d("onClosed reason -> %d, %s", code, reason)
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        Timber.e(t, "onFailure -> %s", response?.message())
    }

    companion object {

        private val WS_URL = "wss://api.huobi.pro/ws"

        val instance: OkWebSocketClient by lazy { Holder.INSTANCE }
    }

}
