package com.pb.csd.senior.coroutineasync

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
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
            Log.d("Tanck", "Async Start")
            val task = async { downloadTask() }
            Log.d("Tanck", "Async End")
            Log.d("Tanck", "Async done, name:${task.await().name}")

            Log.d("Tanck", "Sync Start")
            val task2 = downloadTask()
            Log.d("Tanck", "Sync End")
            Log.d("Tanck", "Sync done, name:${task2.name}")
        }
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

}