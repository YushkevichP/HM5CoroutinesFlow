package com.example.hm5_coroutinesflow.fragments

import retrofit2.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        swipeToRefreshListener()
        addScrollListener(layoutManager)


    }

    private fun addScrollListener(layoutManager: LinearLayoutManager) {
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

    private fun loadNewPage(pageForRequest: Int) {
        viewLifecycleOwner.lifecycleScope.launch {

            try {
                val tempList = ServiceLocator.rickMortyApi.getUsers(pageForRequest)
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
            pageCounter = 1
            listForSubmit = emptyList()
            personAdapter.submitList(listForSubmit)
            loadNewPage(pageCounter)
        }
    }

    private fun swipeToRefreshListener() {
        binding.swipeLayout.setOnRefreshListener {
            pageCounter = 1
            listForSubmit = emptyList()
            personAdapter.submitList(listForSubmit)
            loadNewPage(pageCounter)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}