package com.h.downloaddemo

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.h.downloaddemo.util.ProgressUtil
import com.liuguangqiang.swipeback.SwipeBackLayout
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener2
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.atomic.AtomicLong

class MainActivity : AppCompatActivity() {
    private val REQUEST_DOWNLOAD_FILE = 2
    private var task: DownloadTask? = null
    private var uri: Uri? = null
    private var url = ""
    private var typeSave = ""
    private var namefile = ""
    private var showLL = false

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = intent
        url =  intent.getStringExtra("image")
            //"https://docs.google.com/spreadsheets/d/1BqFq8MUPHwLC6Vpw1z12peWaSN9yZbfwQO1YqzI3XC8/edit#gid=0"
      //
        namefile = intent.getStringExtra("slug")
        Glide.with(this).load(url).into(ivProduct)
        if (url.contains("mp4")) {
            typeSave = ".mp4"
        } else if (url.contains("jpg")) {
            typeSave = ".jpg"
        } else if (url.contains("png")) {
            typeSave = ".png"
        } else if (!url.contains("mp4")) {
            typeSave = ".jpg"
        } else if (url.contains(".docx")) {
            typeSave = ".docx"
        } else if (!url.contains(".docs")) {
            typeSave = ".xlsx"
        }
        eventOnClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DOWNLOAD_FILE && resultCode == Activity.RESULT_OK && data!!.data != null) {
            uri = data.data
            handler(uri)
        }
    }

    private fun handler(uri: Uri?) {
        val taskDownload =
            DownloadTask.Builder(url, uri!!).setMinIntervalMillisCallbackProcess(400).build()
        this.task = taskDownload
        taskDownload.enqueue(SamplerListener())
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun eventOnClick() {
        btnDownload.setOnClickListener {
            if (btnDownload.tag == null) {
                if (uri == null) {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "image/*"
                    intent.putExtra(Intent.EXTRA_TITLE, "${namefile}" + typeSave)
                    startActivityForResult(intent, REQUEST_DOWNLOAD_FILE)
                } else {
                    handler(uri)
                }

            } else {
                task!!.cancel()
            }

        }

        swipeBackLayout.setDragEdge(SwipeBackLayout.DragEdge.LEFT)
        clContent.setOnClickListener {
            if (showLL) {
                showLL = true
                llDownload.visibility = View.VISIBLE
            } else {
                showLL = false
                llDownload.visibility = View.GONE
                llProgress.visibility = View.GONE
            }
        }

    }

    inner class SamplerListener : DownloadListener2() {
        private val progress = AtomicLong()
        private var speedCalculator: SpeedCalculator? = null
        override fun taskStart(task: DownloadTask) {
            btnDownload.tag = Object()
            speedCalculator = SpeedCalculator()
            llProgress.visibility = View.VISIBLE
        }


        override fun downloadFromBeginning(
            task: DownloadTask,
            info: BreakpointInfo,
            cause: ResumeFailedCause
        ) {
            super.downloadFromBeginning(task, info, cause)
            progress.set(0)
            ProgressUtil.calcProgressToViewAndMark(progress_horizontal, 0, info.totalLength)
        }

        override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {
            super.downloadFromBreakpoint(task, info)
            progress.set(info.totalOffset)
            ProgressUtil.calcProgressToViewAndMark(
                progress_horizontal,
                progress.get(),
                info.totalLength
            )
        }

        override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {
            super.fetchProgress(task, blockIndex, increaseBytes)
            speedCalculator!!.downloading(increaseBytes)
            val offset = progress.addAndGet(increaseBytes)
            ProgressUtil.updateProgressToViewWithMark(progress_horizontal, offset)
        }


        override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
            if (realCause != null) {
                Log.e("ContentUriActivity", "taskEnd with realCause", realCause)
            }
            Toast.makeText(this@MainActivity, "Lưu thành công", Toast.LENGTH_SHORT).show()
            btnDownload.tag = null
        }

    }
}


