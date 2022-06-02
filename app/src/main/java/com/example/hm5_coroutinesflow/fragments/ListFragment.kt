package com.example.hm5_coroutinesflow.fragments

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hm5_coroutinesflow.ItemAdapter
import com.example.hm5_coroutinesflow.R
import com.example.hm5_coroutinesflow.ServiceLocator
import com.example.hm5_coroutinesflow.database.appDataBase
import com.example.hm5_coroutinesflow.databinding.FragmentListBinding
import com.example.hm5_coroutinesflow.model.CartoonPerson
import com.example.hm5_coroutinesflow.model.ItemType
import com.example.hm5_coroutinesflow.utilities.networkState
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding: FragmentListBinding
        get() = requireNotNull(_binding) {
            "View was destroyed"
        }

    private val personAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ItemAdapter(requireContext()) { item ->
            val personItem = item as? ItemType.Content ?: return@ItemAdapter
            findNavController().navigate(
                ListFragmentDirections.toDetails(personItem.data.idApi)
            )
        }
    }

    private val personRepository by lazy(LazyThreadSafetyMode.NONE) {
        ServiceLocator.provideRepository()
    }

    private val personDao by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().appDataBase.personDao()
    }

    private var pageCounter = 1
    private var isLoading = false
    private var listForSubmitRetrofit: List<ItemType<CartoonPerson>> = emptyList()

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

        checkNetWorkState()
        initRecyclerView(layoutManager)
        loadNewPage(pageCounter)
        swipeRefresh()

    }

    private fun checkNetWorkState() {
        requireContext().networkState
            .filter { isWorking ->
                !isWorking
            }
            .onEach {
                Toast.makeText(requireContext(), " Lost connection", Toast.LENGTH_SHORT).show()
                uploadCashPerson()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun uploadCashPerson() {
        viewLifecycleOwner.lifecycleScope.launch {
            // fetch person from db
            // val listDao = personDao.getFirstTwenty()
            val listDao = personDao.getAllPersons()
            println("LIST DAO ==== $listDao")
            val listForSubmitDao = listDao.map {
                ItemType.Content(it)
            }
            personAdapter.submitList(listForSubmitDao)
        }
    }

    private fun loadNewPage(pageForRequest: Int) {

        viewLifecycleOwner.lifecycleScope.launch {
            try {
               // uploadCashPerson()
                val tempList = personRepository.getUser(pageForRequest)
                val listPersons = tempList.results
                personDao.insertPersons(listPersons)
                val content = listPersons.map {
                    ItemType.Content(it)
                }
                val resultList = content.plus(ItemType.Loading)
                val currentList = personAdapter.currentList.dropLast(1)
                listForSubmitRetrofit = (currentList + resultList)
                personAdapter.submitList(listForSubmitRetrofit)

                isLoading = false
                binding.swipeLayout.isRefreshing = false
            } catch (e: Throwable) {
                error(e)
            }
        }
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
                    }
                }
            }
            toolbar.setOnClickListener {
                refreshListToStart()
            }
        }
    }

    private fun refreshListToStart() {
        pageCounter = 1
        listForSubmitRetrofit = emptyList()
        personAdapter.submitList(listForSubmitRetrofit)
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