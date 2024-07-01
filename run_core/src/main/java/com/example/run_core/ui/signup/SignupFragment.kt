package com.example.run_core.ui.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.run_core.R
import com.example.run_core.databinding.FragmentSignupBinding

import com.example.run_core.utils.LoadingDialog
import com.permissionx.guolindev.PermissionX
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private var email: String? = null
    private var username: String? = null
    private var password: String? = null
    private val viewModel: SignUpViewModel by viewModels()
    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(requireActivity())
    }

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
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        openImage()
        onSingup()
    }

    private fun onSingup() {
        binding.btSignup.setOnClickListener {
            if (areFieldReady()) {
                if (imageUri != null) {
                    signUp()
                } else {
                    Toast.makeText(requireContext(), "Image is required", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }
        binding.txtLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun signUp() {
        loadingDialog.startLoading()
        viewModel.signUp(username!!, email!!, password!!, imageUri!!)
        viewModel.signUpResult.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                Toast.makeText(requireContext(), "Create account successfully", Toast.LENGTH_SHORT)
                    .show()
                loadingDialog.stopLoading()
            } else {
                Toast.makeText(requireContext(), "Email already exist", Toast.LENGTH_SHORT).show()
                loadingDialog.stopLoading()
            }
        }
    }

    private fun areFieldReady(): Boolean {
        username = binding.edtUserName.text.toString().trim()
        email = binding.edtEmail.text.toString().trim()
        password = binding.edtPassword.text.toString().trim()

        var flag = false
        var requestView: View? = null

        if (username!!.isEmpty()) {
            binding.edtUserName.error = "Field is required"
            flag = true
            requestView = binding.edtUserName
        } else if (email!!.isEmpty()) {
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

    private fun openImage() {
        binding.imgPick.setOnClickListener { view ->
            PermissionX.init(this).permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        pickImage()
                    } else {

                    }
                }
        }
    }

    private fun pickImage() {
        CropImage.activity()
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(requireActivity())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.uri
                Glide.with(this).load(imageUri).into(binding.imgPick)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val exception = result.error
                Log.d("TAG", "onActivityResult: $exception")
            }
        }
    }


    companion object {
        fun newInstance(): SignupFragment {
            val fragment = SignupFragment()
            return fragment
        }
    }
}