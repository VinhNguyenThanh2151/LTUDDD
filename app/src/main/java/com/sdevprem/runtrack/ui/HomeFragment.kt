package com.sdevprem.runtrack.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.run_core.R
import com.example.run_core.databinding.FragmentHomeBinding
import com.example.run_core.ui.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.run_core.ui.map.MapActivity
import com.example.run_core.utils.LoadingDialog
import com.example.run_core.utils.ShareUtils
import com.sdevprem.runtrack.AppCoreActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!
    private val dialog: LoadingDialog by  lazy {
        LoadingDialog(requireActivity())
    }
    private var name:String = ""
    private var url:String = ""
    private var listener: OnFragmentInteractionListener? = null
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init() {
       initView()
        onClickView()
    }
    private fun onClickView() {
        binding.apply {
            btnSetting.setOnClickListener {
                val bundle = bundleOf(
                    "name" to name,
                    "url" to url
                )
                findNavController().navigate(R.id.action_homeFragment_to_settingFragment, bundle)
            }
            btnLogout.setOnClickListener {
                viewModel.logout()
                dialog.startLoading()
                viewModel.logoutSuccess.observe(viewLifecycleOwner) {
                    if (it) {
                        dialog.stopLoading()
                        findNavController().popBackStack(R.id.homeFragment, false)
                        findNavController().navigate(R.id.loginFragment)
                    }
                }
            }
            btnMap.setOnClickListener {
                startActivity(Intent(requireActivity(), MapActivity::class.java))
            }
            btnHealth.setOnClickListener {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.putExtra("UserName",name)
                startActivity(intent)
            }
        }
    }
    private fun initView() {
        binding.apply {
            viewModel.fetchUserData(firebaseAuth.currentUser!!.uid)
            viewModel.user.observe(viewLifecycleOwner) {
                try {
                    if (it.isSuccess) {
                        val user = it.getOrNull()
                        if (user != null) {
                            tvName.text = user.username
                            tvEmail.text = user.email
                            name = user.username
                            url = user.image
                            ShareUtils.setString("USER_NAME", user.username)
                            Glide.with(requireActivity()).load(user.image).into(imgAvt)
                        }
                    } else {
                        Toast.makeText(requireActivity(), "User not found", Toast.LENGTH_SHORT)
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }

            }

        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentClick()
    }
    companion object {
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            return fragment
        }
    }
}