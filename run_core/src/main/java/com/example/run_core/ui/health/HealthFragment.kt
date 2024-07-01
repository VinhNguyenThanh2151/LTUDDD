package com.example.run_core.ui.health

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.run_core.databinding.FragmentHealthBinding


/**
 * A simple [Fragment] subclass.
 * Use the [HealthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HealthFragment : Fragment() {
    private var _binding: FragmentHealthBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHealthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init() {
        initView()
    }
    private fun initView() {
        binding.apply {
            ltAnimation.playAnimation()
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HealthFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}