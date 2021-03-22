package com.pb.csd.senior.coroutineasync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
//            Log.d("Tanck", "Async Start")
//            val task = async { downloadTask() }
//            Log.d("Tanck", "Async End")
//            Log.d("Tanck", "Async done, name:${task.await().name}")
//
//            Log.d("Tanck", "Sync Start")
//            val task2 = downloadTask()
//            Log.d("Tanck", "Sync End")
//            Log.d("Tanck", "Sync done, name:${task2.name}")

            Log.d("Tanck", "Sync Start")
            getDownLoadTask().retry(retries = 1) { cause ->
                if (cause is Exception) {
                    cause.message?.let { Log.e("Tanck", it) }
                    return@retry true
                } else {
                    return@retry false
                }
            }.collect { value ->
                Log.d("Tanck", "getDownLoadTask:$value")
            }
//            networkAvailableFlow().cancellable().collect { value ->
//                Log.d("Tanck", "Callback:$value")
//            }
            Log.d("Tanck", "Sync End")
        }
    }

    override fun onPause() {
        super.onPause()
        GlobalScope.cancel("onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalScope.cancel("onDestroy")
    }

    private suspend fun downloadTask(): Task = suspendCancellableCoroutine { cancellableContinuation ->
        cancellableContinuation.invokeOnCancellation {
            // Cancel somethings
        }
        DownloadTask(object : MyDownloadListener {
            override fun onFailed(e: Exception) {
                Log.d("Tanck", "onFailed")
                cancellableContinuation.resumeWithException(e)
            }

            override fun onCompleted(task: Task) {
                Log.d("Tanck", "onCompleted")
                cancellableContinuation.resume(task)
            }
        }).start()
    }

    fun getDownLoadTask(): Flow<Int> = flow {
        getBaseEvent().collect { value ->
            emit(if (value) 999 else 0)
        }
        emit(1)
//        throw IOException()
    }

    fun getBaseEvent(): Flow<Boolean> = flow {
        try {
            withTimeout(2000) {
                delay(100000)
                emit(true)
            }
        } catch (e: TimeoutCancellationException) {
            Log.d("Tanck", "getBaseEvent:${e.message}")
            emit(true)
        }

    }

    private fun Context.networkAvailableFlow(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                offer(true)
                offer(false)
                channel.close()
            }

            override fun onLost(network: Network) {
                offer(false)
                channel.close()
            }
        }
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager.registerNetworkCallback(NetworkRequest.Builder().run {
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            build()
        }, callback)

        awaitClose { // Suspends until channel closed
            Log.d("Tanck", "awaitClose")
            manager.unregisterNetworkCallback(callback)
        }
    }
}
