package com.rahil.anagrampoc

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import java.io.File
import com.rahil.anagrampoc.utility.FileType
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),MainActivityContract.MainActivityView {
    private var permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private val REQUEST_CODE_PERMISSIONS = 1001
    lateinit var mainActivityPresenter: MainActivityPresenter
    /*
    * Class for managing notifications
    */
    private lateinit var helper: NotificationHelper
    private val REQUEST_CODE_FIRST_FILE = 1002
    private val REQUEST_CODE_SECOND_FILE = 1003
    lateinit var firstFileLineList: List<String>
    lateinit var secondFileLineList: List<String>
    private lateinit var mFilePath:File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivityPresenter = MainActivityPresenter()
        mainActivityPresenter.takeView(this)
        helper = NotificationHelper(this)
        firstFileChooser.setOnClickListener({ mainActivityPresenter. getFile(FileType.FirstFile)})
        secondFileChooser.setOnClickListener({   mainActivityPresenter. getFile(FileType.SecondFile)})
        resultButton.setOnClickListener({ mainActivityPresenter.findAnagram(firstFileLineList,secondFileLineList)})
    }

    override fun anagramResult(map: HashMap<Int, String>) {
        mainActivityPresenter.emailResultFile(map,this)
    }

    override fun showNotification(counter: Int) {
        helper.notify(
                1001, helper.getNotification("Anagram Count", "Total no of anagram count is :"+counter))
    }

    override fun getFileFromDevice(type: FileType) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "file/*"
        when(type){
            FileType.FirstFile ->startActivityForResult(intent, REQUEST_CODE_FIRST_FILE)
            FileType.SecondFile -> startActivityForResult(intent, REQUEST_CODE_SECOND_FILE)
        }
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun showError(errorMessage: String) {
        Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show()
    }


    override fun checkPermission(): Boolean {
        for (p in permissions) {
            val result = ActivityCompat.checkSelfPermission(this, p)

            if (result == PackageManager.PERMISSION_DENIED) return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted(grantResults)) {
                mainActivityPresenter.permissionDenied()
            }
        }
    }

    private fun allPermissionsGranted(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED)
                return false
        }

        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        mFilePath = File(resultData?.data?.path)
        if (requestCode == REQUEST_CODE_FIRST_FILE && resultCode == Activity.RESULT_OK) {
           firstFileLineList =  mainActivityPresenter.extractLinesFromFilePath(mFilePath,FileType.FirstFile)

        } else if (requestCode == REQUEST_CODE_SECOND_FILE && resultCode == Activity.RESULT_OK) {
            secondFileLineList = mainActivityPresenter.extractLinesFromFilePath(mFilePath,FileType.SecondFile)
        }
    }

    override fun showPermissionDialog() {
        ActivityCompat.requestPermissions(this,
                permissions,
                REQUEST_CODE_PERMISSIONS)
    }

    override fun getEnvFilePath(): File {
        return this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }
}
