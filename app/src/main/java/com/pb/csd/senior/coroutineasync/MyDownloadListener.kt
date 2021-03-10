package com.pb.csd.senior.coroutineasync

/**
 * Current name : MyDownloadListener in `CoroutineAsync`
 *
 *
 * Created by Tanck on 2021/3/10 3:16 PM.
 *
 *
 * Note : N/A
 */
interface MyDownloadListener {
    fun onCompleted(task: Task)
    fun onFailed(e: Exception)
}