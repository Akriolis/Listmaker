package com.akrio.listmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.akrio.listmaker.databinding.ActivityMainBinding
import com.akrio.listmaker.ui.main.MainFragment
import com.akrio.listmaker.ui.main.MainViewModel
import com.akrio.listmaker.ui.main.MainViewModelFactory

class MainActivity : AppCompatActivity(), MainFragment.MainFragmentInteractionListener {

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
            val mainFragment = MainFragment.newInstance(this)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment)
                .commitNow()
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

        with(builder) {
            setTitle(dialogTitle)
            setView(listTitleEditText)

            setPositiveButton(positiveButtonTitle) { dialog, _ ->
                dialog.dismiss()

                val taskList = TaskList(listTitleEditText.text.toString())
                viewModel.saveList(taskList)
                showListDetail(taskList)
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
        startActivity(listDetailIntent)
    }

    companion object{
        const val INTENT_LIST_KEY = "list"
    }

    override fun listItemTapped(list: TaskList) {
        showListDetail(list)
    }

}

