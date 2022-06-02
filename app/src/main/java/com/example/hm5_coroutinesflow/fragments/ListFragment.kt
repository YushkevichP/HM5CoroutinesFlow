package com.example.hm5_coroutinesflow.fragments

import retrofit2.*
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
import com.example.hm5_coroutinesflow.model.wrapperForListFromApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class ListFragment : Fragment() {

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

    private val personRepository by lazy {
        ServiceLocator.provideRepository()
    }

    private var pageCounter = 1
    private var isLoading = false
    private var listForSubmit: List<ItemType<CartoonPerson>> = emptyList()

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


        val layoutManager = LinearLayoutManager(requireContext())
        initRecyclerView(layoutManager)
        loadNewPage(pageCounter)
        swipeRefresh()

    }

    private fun initRecyclerView(layoutManager: LinearLayoutManager) {
        with(binding) {
            recyclerView.apply {
                addSpaceDecoration(resources.getDimensionPixelSize(R.dimen.bottom_space))
                adapter = personAdapter
                recyclerView.layoutManager = layoutManager
                recyclerView.addPaginationScrollListener(layoutManager, 2) {
                    if (!isLoading) {
                        isLoading = true
                        pageCounter++
                        loadNewPage(pageCounter)
                     //   println("PAGE COUNTER = $pageCounter")
                    }
                }
            }
            toolbar.setOnClickListener {
                refreshListToStart()
            }
        }
    }

    private fun loadNewPage(pageForRequest: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val tempList = personRepository.getUser(pageForRequest)
                val listPersons = tempList.results
                val content = listPersons.map {
                    ItemType.Content(it)
                }
                val resultList = content.plus(ItemType.Loading)
                val currentList = personAdapter.currentList.dropLast(1)
                listForSubmit = (currentList + resultList)
                personAdapter.submitList(listForSubmit)

                isLoading = false
                binding.swipeLayout.isRefreshing = false
            } catch (e: Throwable) {
                error(e)
            }
        }
    }

    private fun refreshListToStart() {
        pageCounter = 1
        listForSubmit = emptyList()
        personAdapter.submitList(listForSubmit)
        loadNewPage(pageCounter)

    }

    private fun swipeRefresh() {
        binding.swipeLayout.setOnRefreshListener {
            refreshListToStart()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






//----tried do with flow
//    private val _paginationFlow = MutableSharedFlow<Unit>()
//    private val paginationFlow = _paginationFlow.asSharedFlow()
//    private fun addScrollListener(layoutManager: LinearLayoutManager) {
//
//        with(binding) {
//            recyclerView
//                .paginationScrollFlow(layoutManager, 4) {
//                    if (!isLoading) {
//                        isLoading = true
//                        loadNewPage(pageCounter)
//                        pageCounter++
//                        println("PAGE COUNTER = $pageCounter")
//                    }
//                }
//                .filter { !isLoading }
//                .onEach { isLoading = true }
//                .map {
//                    personRepository.getUser(pageCounter).results
//                }
//                .map {
//                    it.map {
//                        ItemType.Content(it)
//                    }
//                }
//                .map {
//                    it.plus(ItemType.Loading)
//                }
//                .onEach {
//                    personAdapter.submitList(it)
//                }
//                .onEach {
//                    isLoading = false
//              //      println("PAGE COUNTER = $pageCounter")
//                }
//                .launchIn(viewLifecycleOwner.lifecycleScope)


//            recyclerView.paginationScrollFlow(layoutManager, 1) {
//                _paginationFlow.tryEmit(Unit)
//                if (!isLoading) {
//                    isLoading = true
//                    pageCounter++
//                    loadNewPage(pageCounter)
//                }
//            }
//        }
//    }