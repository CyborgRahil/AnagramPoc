package com.rahil.anagrampoc

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.rahil.anagrampoc.utility.FileType

import java.io.*
import java.util.*
import kotlin.collections.HashMap


class MainActivityPresenter : MainActivityContract.Presenter {
    override fun emailResultFile(map: HashMap<Int, String>, context: Context) {
        val rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/MyFolder/"
        val fileName = "resultAnagram.txt"
        val root = File(rootPath)
        if (!root.exists()) {
            root.mkdirs()
        }
        val file = File(rootPath + fileName)
        if (file.exists()) {
            file.delete()
        }
        val isNewFileCreated = file.createNewFile()

        if (isNewFileCreated) {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            try {
                map.forEach { (key, value) ->
                    val result = "line no is :" + key + " line is : " + value
                    fos!!.write(result.toByteArray())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mView.hideProgress()
            val emailIntent = Intent(
                    Intent.ACTION_SEND);

            //Explicitly only use Gmail to send
            emailIntent.setType("vnd.android.cursor.dir/email");

            emailIntent.setType("plain/text");

            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Anagram Result");

            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "PFA for Anagram Result.");

            //Add the attachment by specifying a reference to our custom ContentProvider
            //and the specific file of interest
            emailIntent.putExtra(
                    Intent.EXTRA_STREAM, Uri.parse("file://" + rootPath + fileName))

            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))

        } else {
            mView.showError("Please try again.")

        }

    }

    lateinit var mView: MainActivityContract.MainActivityView
    override fun permissionDenied() {
        mView.showError("Please allow the permission.")
    }

    override fun getFile(type: FileType) {
        when (mView.checkPermission()) {
            true -> mView.getFileFromDevice(type)
            false -> mView.showPermissionDialog()
        }
    }

    override fun extractLinesFromFilePath(filePath: File, type: FileType): List<String> {
        return filePath.useLines { it.toList() }
    }

    override fun findAnagram(firstFileList: List<String>, secondFileList: List<String>) {
        if (firstFileList.size==0 ||secondFileList.size==0){
            mView.showError("PLease select valid file.")
        }
        mView.showProgress()
        var count = 0
        var notificationCounter = 0
        val resultMap = HashMap<Int, String>()
        while (count < secondFileList.size) {
            val secondListString = secondFileList[count]
            val charArray = secondListString.toCharArray()
            Arrays.sort(charArray)
            val secondListSortedString = String(charArray)
            for (i in firstFileList.indices) {
                val firstListString = firstFileList[i]
                if (firstListString.length == secondListString.length) {
                    val firstListCharArray = secondListString.toCharArray()
                    Arrays.sort(firstListCharArray)
                    val firstListSortedString = String(firstListCharArray)
                    if (firstListSortedString.equals(secondListSortedString)) {
                        notificationCounter++
                        resultMap.put(i, secondListString)
                        mView.showNotification(notificationCounter)
                        break
                    }
                }
            }
            count++
        }
        mView.anagramResult(resultMap)

    }

    override fun takeView(view: MainActivityContract.MainActivityView) {
        mView = view
    }

    override fun dropView() {

    }
}