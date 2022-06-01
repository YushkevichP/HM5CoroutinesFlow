package com.example.hm5_coroutinesflow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.example.hm5_coroutinesflow.ServiceLocator
import com.example.hm5_coroutinesflow.databinding.FragmentPersonDetailsBinding

import kotlinx.coroutines.launch


class PersonDetailsFragment : Fragment() {

    private var _binding: FragmentPersonDetailsBinding? = null
    private val binding: FragmentPersonDetailsBinding
        get() = requireNotNull(_binding) {
            "VIEW WAS DESTROYED"
        }

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
        loadPersonDetails(counter)
    }


    private fun loadPersonDetails(counter: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val details = ServiceLocator.rickMortyApi.getUserDetails(id = counter)
                with(binding) {
                    imageUserFragment.load(details.avatarApiDetails)
                    personGender.text = details.gender
                    personName.text = details.name
                    personStatus.text = details.status
                }
            } catch (e: Throwable) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}