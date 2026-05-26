package com.smk.growsave.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smk.growsave.databinding.FragmentHomeBinding

/**
 * UserHomeFragment digunakan khusus untuk dashboard role Warga/User.
 * Menggunakan layout fragment_home.xml (mockup layout dashboard warga) tanpa logic tambahan.
 */
class UserHomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah kebocoran memori (memory leaks)
    }
}
