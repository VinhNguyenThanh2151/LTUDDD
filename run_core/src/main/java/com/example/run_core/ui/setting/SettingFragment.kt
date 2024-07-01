package com.example.run_core.ui.setting

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
import com.example.run_core.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.run_core.utils.ChangeEmailDialog
import com.example.run_core.utils.ChangePasswordDialog
import com.permissionx.guolindev.PermissionX
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingViewModel by viewModels()
    private var user: String = ""
    private var url:String = ""
    private var imageUri: Uri? = null
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val changeEmailDialog: ChangeEmailDialog by lazy {
        ChangeEmailDialog(requireActivity())
    }
    private val changePasswordDialog: ChangePasswordDialog by lazy {
        ChangePasswordDialog(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getString("name").toString()
        url = arguments?.getString("url").toString()
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
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
    private fun initView() {
        binding.apply {
           txtUsername.text = user
            Glide.with(requireContext()).load(url).into(imgProfile)
        }
    }

    private fun onClickView() {
        binding.apply {
            cardEmail.setOnClickListener {
                changeEmailDialog.show {
                    viewModel.updateEmail(it)
                    viewModel.updateEmailSuccess.observe(viewLifecycleOwner) {
                        if (it) {
                            changeEmailDialog.dismiss()
                            Toast.makeText(requireContext(), "Update email success", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Update email failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            cardPassword.setOnClickListener {
                changePasswordDialog.show {
                    viewModel.changePassword(it)
                    viewModel.changePasswordSuccess.observe(viewLifecycleOwner) {
                        if (it) {
                            changePasswordDialog.dismiss()
                            Toast.makeText(requireContext(), "Update password success", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Update password failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
           imgProfile.setOnClickListener { view ->
                PermissionX.init(requireActivity()).permissions(
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
                Glide.with(this).load(imageUri).into(binding.imgProfile)
                viewModel.updateProfileImage(firebaseAuth.uid!!,imageUri!!)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val exception = result.error
                Log.d("TAG", "onActivityResult: $exception")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SettingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}