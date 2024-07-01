package com.example.run_core.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.run_core.R
import com.example.run_core.databinding.FragmentLoginBinding
import com.example.run_core.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var email: String? = null
    private var password:String? = null
    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(requireActivity())
    }
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init() {
        onViewClick()
    }
    private fun onViewClick() {
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
        binding.btnLogin.setOnClickListener { view ->
            try {
                if (areFieldReady()) {
                    login()
                }
            } catch (e :Exception) {

            }

        }
    }
    private fun login() {
         loadingDialog.startLoading()
        viewModel.login(email!!, password!!)
        viewModel.loginResult.observe(viewLifecycleOwner) {
            try {
                if (it.isSuccess) {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    loadingDialog.stopLoading()
                } else {
                    Toast.makeText(requireContext(), "Email or Password is incorrect", Toast.LENGTH_SHORT).show()
                    loadingDialog.stopLoading()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Account not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun areFieldReady(): Boolean {
        email = binding.edtEmail.text.toString().trim()
        password = binding.edtPassword.text.toString().trim()

        var flag = false
        var requestView: View? = null

        if (email!!.isEmpty()) {
            binding.edtEmail.error = "Field is required"
            flag = true
            requestView = binding.edtEmail
        } else if (password!!.isEmpty()) {
            binding.edtPassword.error = "Field is required"
            flag = true
            requestView = binding.edtPassword
        } else if (password!!.length < 8) {
            binding.edtPassword.error = "Minimum 8 characters"
            flag = true
            requestView = binding.edtPassword
        }

        if (flag) {
            requestView!!.requestFocus()
            return false
        } else {
            return true
        }
    }
    companion object {
        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()
            return fragment
        }
    }
}