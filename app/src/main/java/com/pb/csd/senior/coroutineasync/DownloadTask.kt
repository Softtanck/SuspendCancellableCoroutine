package com.pb.csd.senior.coroutineasync

import java.io.IOException

/**
 *
 *  Current name : DownloadTask in `CoroutineAsync`
 *
 *  Created by Tanck on 2021/3/10 11:14 AM.
 *
 *  Note : N/A
 *
 */

class DownloadTask(private var downloadListener: MyDownloadListener) {

    fun start() {
        Thread {
            // downloadListener.onFailed(IOException())
            Thread.sleep(5000)
            val person = Task()
            person.name = "Hello"
            downloadListener.onCompleted(person)
        }.start()
    }
}