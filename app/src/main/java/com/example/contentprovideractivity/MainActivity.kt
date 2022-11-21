package com.example.contentprovideractivity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.EmptyStackException
//import java.util.jar.Manifest
import android.Manifest
import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.provider.CallLog
import android.provider.ContactsContract
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.util.StringJoiner

class MainActivity : AppCompatActivity() {
    private val contactsList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactsList)
        contactsView.adapter = adapter
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CALL_LOG), 1)
        } else {
            readCallLog()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readCallLog()
                } else {
                    Toast.makeText(this, "You denied the permission",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun transToString(time:Int):String {
        return SimpleDateFormat("yyy-MM-dd-hh").format(time)
    }
    @SuppressLint("Range")
    private fun readCallLog() {
        // 查询联系人数据
        contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, null)?.apply {
            while (moveToNext()) {
                // 获取联系人姓名
                val displayName = getString(getColumnIndex(
                    CallLog.Calls.CACHED_NAME))
                // 获取联系人手机号
                val number = getString(getColumnIndex(
                    CallLog.Calls.NUMBER))
                val date = transToString(getColumnIndex(CallLog.Calls.DATE))

                contactsList.add("$displayName\n$number  $date")
            }
            adapter.notifyDataSetChanged()
            close()
        }
    }

}
