package com.akrio.listmaker.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.akrio.listmaker.ListSelectionRecyclerViewAdapter
import com.akrio.listmaker.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    val TAG = MainFragment::class.java.simpleName

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.listsRecyclerview.layoutManager =
            LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(),
            MainViewModelFactory(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        )
            .get(MainViewModel::class.java)
        Log.d(TAG,"Calling for ViewModelProvider")

        val recyclerViewAdapter =
            ListSelectionRecyclerViewAdapter(viewModel.lists)
        Log.d(TAG,"Creating RecyclerViewAdapter")

        binding.listsRecyclerview.adapter = recyclerViewAdapter

        Log.d(TAG,"Binging adapter to RecycledView")

        viewModel.onListAdded = {
            recyclerViewAdapter.listsUpdated()
        }
    }

}