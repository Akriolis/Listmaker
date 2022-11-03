package com.akrio.listmaker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.akrio.listmaker.databinding.ListDetailActivityBinding
import com.akrio.listmaker.ui.detail.ui.detail.ListDetailFragment
import com.akrio.listmaker.ui.detail.ui.detail.ListDetailViewModel

class ListDetailActivity : AppCompatActivity() {

    lateinit var binding: ListDetailActivityBinding

    lateinit var viewModel: ListDetailViewModel

    lateinit var fragment: ListDetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ListDetailViewModel::class.java]

        @Suppress("DEPRECATION")
        viewModel.list = intent.getParcelableExtra(MainActivity.INTENT_LIST_KEY)!!

        binding = ListDetailActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.addTaskButton.setOnClickListener {
            showCreateTaskDialog()
        }

        title = viewModel.list.name

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        if (savedInstanceState == null) {
            //TODO what is wrong with unused fragment variable?
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListDetailFragment.newInstance())
                .commitNow()
        }
    }

    private fun showCreateTaskDialog() {
        val taskEditText = EditText(this)
        taskEditText.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this).apply {
            setTitle(R.string.task_to_add)
            setView(taskEditText)
            setPositiveButton(R.string.add_task) { dialog, _ ->

                val task = taskEditText.text.toString()
                if (task.trim().isEmpty()){
                    dialog.dismiss()
                } else {
                    viewModel.addTask(task)

                    dialog.dismiss()
                }
            }
            setNegativeButton(R.string.cancel){ dialog,_->
                dialog.dismiss()
            }
            create().show()
        }
    }

//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        val bundle = Bundle()
//        bundle.putParcelable(MainActivity.INTENT_LIST_KEY, viewModel.list)
//
//        val intent = Intent()
//        intent.putExtras(bundle)
//        setResult(Activity.RESULT_OK, intent)
//        @Suppress("DEPRECATION")
//        super.onBackPressed()
//    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val bundle = Bundle()
            bundle.putParcelable(MainActivity.INTENT_LIST_KEY, viewModel.list)

            val intent = Intent()
            intent.putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }
}