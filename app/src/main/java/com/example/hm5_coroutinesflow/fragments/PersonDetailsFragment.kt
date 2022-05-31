package com.example.hm5_coroutinesflow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.example.hm5_coroutinesflow.databinding.FragmentPersonDetailsBinding
import com.example.hm5_coroutinesflow.model.PersonDetails
import com.example.hm5_coroutinesflow.retrofit.RickMortyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class PersonDetailsFragment : Fragment() {

    private var _binding: FragmentPersonDetailsBinding? = null
    private val binding: FragmentPersonDetailsBinding
        get() = requireNotNull(_binding) {
            "VIEW WAS DESTROYED"
        }

    private var currentRequest: Call<PersonDetails>? = null
    private val args by navArgs<PersonDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentPersonDetailsBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNavController(findNavController()) // back_arrow

        val counter = args.keyId
        currentRequest = RickMortyService.personApi.getUserDetails(counter)
        currentRequest?.enqueue(object : Callback<PersonDetails> {
            override fun onResponse(call: Call<PersonDetails>, response: Response<PersonDetails>) {

                if (response.isSuccessful) {
                    val tempPerson = response.body() ?: return
                    with(binding) {
                        imageUserFragment.load(tempPerson.avatarApiDetails)
                        personGender.text = "Пол персонажа: ${tempPerson.gender}"
                        personName.text = "Имя персонажа: ${tempPerson.name}"
                        personStatus.text = "Жив или нет: ${tempPerson.status}"

                    }
                } else {
                    HttpException(response).message()
                }
                currentRequest = null
            }

            override fun onFailure(call: Call<PersonDetails>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                currentRequest = null
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentRequest?.cancel()
    }
}