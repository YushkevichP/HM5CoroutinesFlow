package com.example.hm5_coroutinesflow.fragments

import retrofit2.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hm5_coroutinesflow.fragments.addPaginationScrollListener
import com.example.hm5_coroutinesflow.fragments.addSpaceDecoration
import com.example.hm5_coroutinesflow.ItemAdapter
import com.example.hm5_coroutinesflow.R
import com.example.hm5_coroutinesflow.databinding.FragmentListBinding
import com.example.hm5_coroutinesflow.model.CartoonPerson
import com.example.hm5_coroutinesflow.model.ItemType
import com.example.hm5_coroutinesflow.model.PersonsListApi
import com.example.hm5_coroutinesflow.retrofit.RickMortyService


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding: FragmentListBinding
        get() = requireNotNull(_binding) {
            "View was destroyed"
        }

    private val adapter by lazy {
        ItemAdapter(requireContext()) { item ->
            val personItem = item as? ItemType.Content ?: return@ItemAdapter
            findNavController().navigate(
                ListFragmentDirections.toDetails(personItem.data.idApi)
            )
        }
    }

    private var requesrCall: Call<PersonsListApi>? = null
    private var pageCounter = 1
    private var isLoading = false
    private var finalFResultlist: List<ItemType<CartoonPerson>> = emptyList()

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

        val layoutManager = LinearLayoutManager(view.context)

        with(binding) {
            recyclerView.addSpaceDecoration(resources.getDimensionPixelSize(R.dimen.bottom_space))
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
            toolbar.setOnClickListener {
                refreshListToStart()
            }
        }

        swipeRefresh()
        makeRequest(pageCounter)

        with(binding) {
            recyclerView.addPaginationScrollListener(layoutManager, 1) {
                if (!isLoading) {
                    isLoading = true
                    pageCounter++
                    makeRequest(pageCounter)
                }
            }
        }
    }

    private fun refreshListToStart() {
        pageCounter = 1
        finalFResultlist = emptyList()
        adapter.submitList(finalFResultlist)
        makeRequest(pageCounter)

    }

    private fun swipeRefresh() {
        binding.swipeLayout.setOnRefreshListener {
            pageCounter = 1
            finalFResultlist = emptyList()
            adapter.submitList(finalFResultlist)
            makeRequest(pageCounter)
        }
    }

    private fun makeRequest(pageForRequest: Int) {

        requesrCall = RickMortyService.personApi.getUsers(pageForRequest)
        requesrCall?.enqueue(object : Callback<PersonsListApi> {
            override fun onResponse(
                call: Call<PersonsListApi>,
                response: Response<PersonsListApi>,
            ) {
                if (response.isSuccessful) {

                    val persons = response.body()?.results // получаем список персон
                    println(persons)
                    val content = persons?.map { // переводим его в наш айтем тайп
                        ItemType.Content(it)
                    }
                    val resultList = content?.plus(ItemType.Loading) ?: return
                    val currentList = adapter.currentList.dropLast(1)
                    finalFResultlist = (currentList + resultList)
                    adapter.submitList(finalFResultlist)

                    isLoading = false

                  //  pageCounter++
                    binding.swipeLayout.isRefreshing = false
                    Toast.makeText(requireContext(),
                        "После всего Пейдж counter =  $pageCounter",
                        Toast.LENGTH_SHORT).show()
                } else {
                    HttpException(response).message()
                }
                requesrCall = null
            }

            override fun onFailure(call: Call<PersonsListApi>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT)
                    .show()
                requesrCall = null
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requesrCall?.cancel()
    }
}