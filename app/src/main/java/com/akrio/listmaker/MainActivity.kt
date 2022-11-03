package com.akrio.listmaker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.akrio.listmaker.databinding.ActivityMainBinding
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

    private fun showListDetail(list: TaskList){
        val listDetailIntent = Intent(this, ListDetailActivity::class.java)
        listDetailIntent.putExtra(INTENT_LIST_KEY, list)
        @Suppress("DEPRECATION") // TODO figure out how to improve it
        startActivityForResult(listDetailIntent,LIST_DETAIL_REQUEST_CODE)
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


}

