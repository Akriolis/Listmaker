package com.akrio.listmaker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.akrio.listmaker.databinding.ActivityMainBinding
import com.akrio.listmaker.ui.detail.ui.detail.ListDetailFragment
import com.akrio.listmaker.ui.main.MainFragment
import com.akrio.listmaker.ui.main.MainViewModel
import com.akrio.listmaker.ui.main.MainViewModelFactory

class MainActivity : AppCompatActivity(), MainFragment.MainFragmentInteractionListener {

    override fun listItemTapped(list: TaskList) {
        showListDetail(list)
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this,
            MainViewModelFactory(PreferenceManager.getDefaultSharedPreferences(this))
        )[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState == null) {
            //TODO something wrong with this snippet
            val mainFragment = MainFragment.newInstance()
            mainFragment.clickListener = this

            val fragmentContainerViewId: Int =
                if (binding.mainFragmentContainer == null){
                    R.id.detail_container
                } else{
                    R.id.main_fragment_container
                }

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(fragmentContainerViewId,mainFragment)
            }
        }

        binding.fabButton.setOnClickListener {
            showCreateListDialog()
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    private fun showCreateListDialog(){
        val dialogTitle = getString(R.string.name_of_list)
        val positiveButtonTitle = getString(R.string.create_list)
        val negativeButtonTitle = getString(R.string.cancel)

        val builder = AlertDialog.Builder(this)
        val listTitleEditText = EditText(this)

        listTitleEditText.inputType = InputType.TYPE_CLASS_TEXT

        builder.apply {
            setTitle(dialogTitle)
            setView(listTitleEditText)

            setPositiveButton(positiveButtonTitle) { dialog, _ ->
                dialog.dismiss()

                val taskList = TaskList(name = listTitleEditText.text.toString())
                if (taskList.name.isEmpty()) {
                    dialog.dismiss()
                } else {
                    viewModel.saveList(taskList)
                    showListDetail(taskList)
                }
            }
            setNegativeButton(negativeButtonTitle) { dialog, _ ->
                dialog.dismiss()
            }

            create().show()
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

    private fun showListDetail(list: TaskList) {
        if (binding.mainFragmentContainer == null) {
            val listDetailIntent = Intent(this, ListDetailActivity::class.java)
            listDetailIntent.putExtra(INTENT_LIST_KEY, list)
            @Suppress("DEPRECATION") // TODO figure out how to improve it
            startActivityForResult(listDetailIntent, LIST_DETAIL_REQUEST_CODE)
        }else{
            val bundle = bundleOf(INTENT_LIST_KEY to list)
            supportFragmentManager.commit{
                setReorderingAllowed(true)
                replace(R.id.list_detail_fragment_container,ListDetailFragment::class.java, bundle, null)
            }

            binding.fabButton.setOnClickListener {
                showCreateTaskDialog()
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LIST_DETAIL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                @Suppress("DEPRECATION")
                viewModel.updateList(data.getParcelableExtra(INTENT_LIST_KEY)!!)
                viewModel.refreshLists()
            }
        }
    }
    companion object{
        const val INTENT_LIST_KEY = "list"
        const val LIST_DETAIL_REQUEST_CODE = 123
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val listDetailFragment =
                supportFragmentManager.findFragmentById(R.id.list_detail_fragment_container)

            if (listDetailFragment == null) {
                finish()
            } else {
                title = resources.getString(R.string.app_name)

                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    remove(listDetailFragment)
                }

                binding.fabButton.setOnClickListener {
                    showCreateListDialog()
                }
            }
        }
    }
}

