package com.example.hospital_management_app.UI.Fragment

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.hospital_management_app.Data.User
import com.example.hospital_management_app.R
import com.example.hospital_management_app.Utilities.Resource
import com.example.hospital_management_app.ViewModel.ProfileViewModel
import com.example.hospital_management_app.databinding.FragmentProfileBinding
import com.example.hospital_management_app.databinding.ProfileDialogLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>

    private val profileViewModel by viewModels<ProfileViewModel>()

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageUri = it.data?.data
                Glide.with(this).load(imageUri).error(R.drawable.profile).into(binding.profileImage)
                profileViewModel.saveImage(imageUri)
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.editProfileBtn.setOnClickListener {
            showEditProfileDialog()
        }
        setupInitialDetails()

        binding.backBtnFavDocLyt.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cameraIcon.setOnClickListener {
            binding.cameraIcon.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                imageActivityResultLauncher.launch(intent)
            }
        }


    }

    private fun setupInitialDetails() {
        profileViewModel.profile.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val user = it.data

                    user?.let {
                        if (!user.name.isNullOrEmpty()) {
                            binding.NameTxt.text = user.name
                        }
                        if (!user.phoneNumber.isNullOrEmpty()) {
                            binding.ContactNumberTxt.text = user.phoneNumber
                        }
                        if (!user.email.isNullOrEmpty()) {
                            binding.EmailTxt.text = user.email
                        }
                        if (!user.dob.isNullOrEmpty()) {
                            binding.DOBTxt.text = user.dob
                        }
                        if (!user.address.isNullOrEmpty()) {
                            binding.AddressTxt.text = user.address
                        }
                        Glide.with(this).load(user.profileImage).error(R.drawable.profile)
                            .into(binding.profileImage)
                    }

                }

                is Resource.Error -> {
                    // Handle error
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {

                }

                is Resource.Unspecified -> Unit
            }
        }

        profileViewModel.updateInfo.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val user = it.data

                    user?.let {
                        if (!user.name.isNullOrEmpty()) {
                            binding.NameTxt.text = user.name
                        }
                        if (!user.phoneNumber.isNullOrEmpty()) {
                            binding.ContactNumberTxt.text = user.phoneNumber
                        }
                        if (!user.email.isNullOrEmpty()) {
                            binding.EmailTxt.text = user.email
                        }
                        if (!user.dob.isNullOrEmpty()) {
                            binding.DOBTxt.text = user.dob
                        }
                        if (!user.address.isNullOrEmpty()) {
                            binding.AddressTxt.text = user.address
                        }
                    }

                }

                is Resource.Error -> {
                    // Handle error
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {

                }

                is Resource.Unspecified -> Unit
            }
        }

        profileViewModel.profileImageUri.observe(viewLifecycleOwner) {
            Glide.with(this).load(it).error(R.drawable.profile).into(binding.profileImage)
        }
    }

    private fun showEditProfileDialog() {
        val dialogBinding = ProfileDialogLayoutBinding.inflate(layoutInflater)

        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(dialogBinding.root)

        val dialog = dialogBuilder.create()

        // Handle Date Picker
        dialogBinding.userDOBEditText.setOnClickListener {
            showDatePicker(dialogBinding)
        }

        dialogBinding.SaveBtn.setOnClickListener {

            val name = dialogBinding.usernameEditText.text.toString()
            val dob = dialogBinding.userDOBEditText.text.toString()
            val address = dialogBinding.userAddressEditText.text.toString()

            if (name.isEmpty() || dob.isEmpty() || address.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val user = User(name = name, dob = dob, address = address)
                profileViewModel.updateProfile(user)
                Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun showDatePicker(dialogBinding: ProfileDialogLayoutBinding) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), R.style.CustomDatePickerDialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dialogBinding.userDOBEditText.setText(selectedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}