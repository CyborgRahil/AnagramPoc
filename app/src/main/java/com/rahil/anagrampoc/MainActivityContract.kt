package com.rahil.anagrampoc

import android.content.Context
import com.rahil.anagrampoc.base.BasePresenter
import com.rahil.anagrampoc.base.BaseView
import com.rahil.anagrampoc.utility.FileType
import java.io.File

interface MainActivityContract {

    interface  MainActivityView : BaseView<Presenter> {
        fun showProgress()
        fun hideProgress()
        fun checkPermission(): Boolean
        fun showPermissionDialog()
        fun getEnvFilePath(): File
        fun getFileFromDevice(type:FileType)
        fun showNotification(counter:Int)
        fun anagramResult(map:HashMap<Int,String>)

    }

    interface  Presenter : BasePresenter<MainActivityView>{
        fun extractLinesFromFilePath(filePath: File, type:FileType): List<String>
        fun findAnagram(firstFileList:List<String>,secondFileList:List<String>)
        fun permissionDenied()
        fun getFile(type: FileType)
        fun emailResultFile(map:HashMap<Int,String>, context:Context)
    }
}