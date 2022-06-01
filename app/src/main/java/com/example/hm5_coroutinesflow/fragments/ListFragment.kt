package com.example.hm5_coroutinesflow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hm5_coroutinesflow.ItemAdapter
import com.example.hm5_coroutinesflow.R
import com.example.hm5_coroutinesflow.ServiceLocator
import com.example.hm5_coroutinesflow.databinding.FragmentListBinding
import com.example.hm5_coroutinesflow.model.CartoonPerson
import com.example.hm5_coroutinesflow.model.ItemType
import kotlinx.coroutines.launch


class ListFragment : Fragment() {

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private var _binding: FragmentListBinding? = null
    private val binding: FragmentListBinding
        get() = requireNotNull(_binding) {
            "View was destroyed"
        }

    private val personAdapter by lazy {
        ItemAdapter(requireContext()) { item ->
            val personItem = item as? ItemType.Content ?: return@ItemAdapter
            findNavController().navigate(
                ListFragmentDirections.toDetails(personItem.data.idApi)
            )
        }
    }

    private var pageCounter = 1
    private var isLoading = false
    private var finalListForSubmit: List<ItemType<CartoonPerson>> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentListBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "onViewCreated", Toast.LENGTH_SHORT).show()
        val layoutManager = LinearLayoutManager(requireContext())

        initRecyclerView(layoutManager)



        loadNewPage(pageCounter)
        swipeToRefreshListener()

        with(binding) {
            recyclerView.addPaginationScrollListener(layoutManager, 1) {
                if (!isLoading) {
                    isLoading = true
                    pageCounter++
                    loadNewPage(pageCounter)
                }
            }
        }


    }

    private fun initRecyclerView(layoutManager: LinearLayoutManager) {
        with(binding) {
            recyclerView.apply {
                addSpaceDecoration(resources.getDimensionPixelSize(R.dimen.bottom_space))
                adapter = personAdapter
                recyclerView.layoutManager = layoutManager
            }
            toolbar.setOnClickListener {
                refreshListToStart()  // для себя, чтоб список обнулять
            }
        }
    }


//    private fun addScrollListener(manager : LinearLayoutManager) {
//        with(binding){
//            recyclerView.apply {
//                addSpaceDecoration(42)
//
//            }
//        }
//    }


    private fun loadNewPage(counter: Int) {
        Toast.makeText(requireContext(),
            "loadNewPage / номер страницы $pageCounter",
            Toast.LENGTH_SHORT).show()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val personsFromApi = ServiceLocator.rickMortyApi.getPersons(counter)
                val listPersons = personsFromApi.results
                val content = listPersons.map {
                    ItemType.Content(it)
                }
                val resultList = content.plus(ItemType.Loading)
                val currentList = personAdapter.currentList.dropLast(1)
                finalListForSubmit = (currentList + resultList)
                personAdapter.submitList(finalListForSubmit)

                isLoading = false
                binding.swipeLayout.isRefreshing = false

            } catch (e: Throwable) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun refreshListToStart() {
        pageCounter = 1
        finalListForSubmit = emptyList()
        personAdapter.submitList(finalListForSubmit)
        loadNewPage(pageCounter)
    }

    private fun swipeToRefreshListener() {
        binding.swipeLayout.setOnRefreshListener {
            // при свайпе обновляем и сетим пустой лист и сразу делаем запрос на первую страничку
            pageCounter = 1
            finalListForSubmit = emptyList()
            personAdapter.submitList(finalListForSubmit)
            loadNewPage(pageCounter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}