package com.h.downloaddemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.h.downloaddemo.util.DemoUtil
import com.liuguangqiang.swipeback.SwipeBackLayout
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.StatusUtil
import com.liulishuo.okdownload.core.Util
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.kotlin.enqueue4WithSpeed
import com.liulishuo.okdownload.kotlin.listener.onTaskStart
import com.liulishuo.okdownload.kotlin.spChannel
import kotlinx.android.synthetic.main.activity_single_download.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest

class SingleDownloadActivity : AppCompatActivity() {
    private var task: DownloadTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_download)

        initTask()
        initStatus()
        eventOnClick()
    }

    private fun initTask() {
        val filename = "single-test"
        val url = "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk"
        val parentFile = DemoUtil.getParentFile(this)
        task = DownloadTask.Builder(url, parentFile)
            .setFilename(filename)
            // the minimal interval millisecond for callback progress
            .setMinIntervalMillisCallbackProcess(16)
            // ignore the same task has already completed in the past.
            .setPassIfAlreadyCompleted(false)
            .build()
    }


    private fun initStatus() = task.let {
        val status = StatusUtil.getStatus(it!!)
        if (status == StatusUtil.Status.COMPLETED) {
            progressBar.progress == progressBar.max
        }
        tvStatus.text = status.toString()
        StatusUtil.getCurrentInfo(it).let { info ->
            {
                Log.d("Status", "init status with : $info")
                DemoUtil.calcProgressToView(progressBar, info!!.totalOffset, info!!.totalLength)
            }
        }
    }

    private fun eventOnClick() {
        btnSingleDownload.text = "Start"
        btnSingleDownload.setOnClickListener {
            task.let {
                if (it!!.tag != null) {
                    it.cancel()
                } else {
                    btnSingleDownload.text = "Cancel"
                    startTask()
                    it.tag = "task started"
                }
            }
        }

        sblSingleDownload.setDragEdge(SwipeBackLayout.DragEdge.RIGHT)
    }

    private fun startTask() {
        var totalLength: Long = 0
        var readableTotalLeng: String? = null
        task!!.enqueue4WithSpeed(
            onTaskStart = { tvStatus.text = "Start Task" },
            onInfoReadyWithSpeed = { _, info, _, _ ->
                tvStatus.text = "Info ready"
                totalLength = info.totalLength
                readableTotalLeng = Util.humanReadableBytes(totalLength, true)
                DemoUtil.calcProgressToView(progressBar, info.totalOffset, totalLength)
            },

            onConnectStart = { _, blockIndex, _ ->
                val status = "Connect End $blockIndex"
                tvStatus.text = status
            }) { task, cause, realCause, taskSpeed ->
            val statusWithSpeed = cause.toString() + " " + taskSpeed.averageSpeed()
            tvStatus.text = statusWithSpeed
            btnSingleDownload.text = " Start"
            task.tag = null
            if (cause == EndCause.COMPLETED) {
                val realMd5 = fileToMD5(task.file!!.absolutePath)

                if (!realMd5!!.equals("f836a37a5eee5dec0611ce15a76e8fd5", ignoreCase = true)) {
                    Log.e("realMd5", "file is wrong because of md5 is wrong $realMd5")
                }
            }
            realCause?.let {
                Log.e("realCause", "download error", it)
            }

            val speedCalculator = SpeedCalculator()
            CoroutineScope(Dispatchers.Main).launch {
                var lastOffset = 0L
                task?.spChannel()?.consumeEach { dp ->
                    val increase = when (lastOffset) {
                        0L -> 0L
                        else -> dp.currentOffset - lastOffset
                    }
                    lastOffset = dp.currentOffset
                    speedCalculator.downloading(increase)
                    val readableOffset = Util.humanReadableBytes(dp.currentOffset, true)
                    val progressStatus = "$readableOffset/$readableTotalLeng"
                    val speed = speedCalculator.speed()
                    val progressStatusWithSpeed = "$progressStatus($speed)"
                    tvStatus.text = progressStatusWithSpeed
                    DemoUtil.calcProgressToView(progressBar, dp.currentOffset, totalLength)
                }
            }
        }
    }


    companion object {

        private const val TAG = "SingleActivity"

        fun fileToMD5(filePath: String): String? {
            var inputStream: InputStream? = null
            try {
                inputStream = FileInputStream(filePath)
                val buffer = ByteArray(1024)
                val digest = MessageDigest.getInstance("MD5")
                var numRead = 0
                while (numRead != -1) {
                    numRead = inputStream.read(buffer)
                    if (numRead > 0) {
                        digest.update(buffer, 0, numRead)
                    }
                }
                val md5Bytes = digest.digest()
                return convertHashToString(md5Bytes)
            } catch (ignored: Exception) {
                return null
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e: Exception) {
                        Log.e(TAG, "file to md5 failed", e)
                    }
                }
            }
        }

        @SuppressLint("DefaultLocale")
        private fun convertHashToString(md5Bytes: ByteArray): String = StringBuffer().apply {
            md5Bytes.forEach { byte ->
                append(((byte.toInt() and 0xff) + 0x100).toString(16).substring(1))
            }
        }.toString().toUpperCase()
    }

}
