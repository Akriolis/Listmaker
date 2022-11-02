package com.akrio.listmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.akrio.listmaker.MainActivity
import com.akrio.listmaker.R
import com.akrio.listmaker.TaskList
import com.akrio.listmaker.ui.detail.ui.detail.ListDetailFragment

class ListDetailActivity : AppCompatActivity() {

    lateinit var list: TaskList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_detail_activity)

        @Suppress("DEPRECATION")
        list = intent.getParcelableExtra(MainActivity.INTENT_LIST_KEY)!!

        title = list.name

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListDetailFragment.newInstance())
                .commitNow()
        }
    }
}