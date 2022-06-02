package com.example.hm5_coroutinesflow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val personRepository by lazy {
        ServiceLocator.provideRepository()
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

        viewLifecycleOwner.lifecycleScope.launch {
            val id = args.keyId
            val details = personRepository.getPersonDetails(id)

            with(binding) {
                imageUserFragment.load(details.avatarApiDetails)
                personGender.text = details.gender
                personName.text = details.name
                personStatus.text = details.status
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}