package com.FTG2024.hrms.profile.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.FTG2024.hrms.databinding.FragmentUploadProfilePhotoBinding
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.profile.ProfileActivity
import com.FTG2024.hrms.profile.viewmodel.ProfileViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class UploadProfilePhotoFragment : Fragment() {

    private lateinit var binding : FragmentUploadProfilePhotoBinding
    private  var cameraImageUri : Uri? = null
    private  var galleryImageUri : Uri? = null
    private  lateinit var serverImageUri : Uri
    private lateinit var serverImageName : String
    private  val viewModel : ProfileViewModel by activityViewModels()
    private lateinit var progressDialog : ProgressDialog
    private val galleryContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            //galleryImageUri = uri
            serverImageUri = uri
            binding.labelMarkAttendanceTakeSelfie.visibility = View.GONE
        }
        binding.imageViewProfileUploadedImage.setImageURI(uri)
    }

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            binding.labelMarkAttendanceTakeSelfie.visibility = View.GONE
            binding.imageViewProfileUploadedImage.setImageURI(null)
            binding.imageViewProfileUploadedImage.setImageURI(cameraImageUri)
            serverImageUri = cameraImageUri!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraImageUri = null
        galleryImageUri = null
        cameraImageUri = createImageUri()
        progressDialog = ProgressDialog(requireActivity(), "Loading Image")
        progressDialog.show()
        setServerImageUrlName()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadProfilePhotoBinding.inflate(layoutInflater, container, false)
        binding.imageViewProfileUploadedImage.setImageURI(null)
        setListeners()
        setUpObservers()
        setImage()
        return binding.root
    }

    private fun setImage() {
        Glide.with(this)
            .load("https://hrm.brothers.net.in/static/employeeProfile/20240522.jpg")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("####", "onLoadFailed: $e")
                    showToast("Failed to Load Image")
                    progressDialog.dismiss()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.labelMarkAttendanceTakeSelfie.visibility = View.GONE
                    progressDialog.dismiss()
                    return false
                }

            })
            .into(binding.imageViewProfileUploadedImage)
    }


    private fun createImageUri(): Uri {
        val image = File(requireContext().filesDir, "selfie1.png")
        return FileProvider.getUriForFile(requireContext(),
            "com.emsapp.selfie.fileprovider",
            image)
    }


    private fun setUpObservers() {
        viewModel.getImageUploadedLivedata().observe(viewLifecycleOwner, Observer {
            Log.d("###", "setUpObservers: ")
            it.getContentIfNotHandled().let {response ->
                when(response) {
                    is com.FTG2024.hrms.uidata.Response.Success -> {
                        progressDialog.dismiss()
                        showToast("Image Uploaded Successfully")
                        startActivity(Intent(requireContext(), ProfileActivity::class.java))
                    }
                    is com.FTG2024.hrms.uidata.Response.Exception -> {
                        progressDialog.dismiss()
                        showToast(response.message.toString())
                    }
                    else -> return@Observer
                }
            }
        })
    }

    private fun setListeners() {
        binding.imageButtonProfileUploadCamera.setOnClickListener {
            choosePhotoUsingCamera()

        }
        binding.imageButtonProfileUploadGallery.setOnClickListener {
            choosePhotoFromGallery()
        }
        binding.buttonProfileUploadPhoto.setOnClickListener {
            progressDialog = ProgressDialog(requireActivity(),"Uploading Image")
            progressDialog.show()
            uploadImage()
        }
    }

    private fun choosePhotoFromGallery() {
        galleryContract.launch("image/*")
    }

    private fun choosePhotoUsingCamera() {
        contract.launch(cameraImageUri)
    }

    private fun uploadImage() {
        val filesDir = requireContext().filesDir
        val file = File(filesDir, "$serverImageName.jpg")
        val inputStream = requireContext().contentResolver.openInputStream(serverImageUri!!)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        var outputStream: FileOutputStream
        var quality = 100
        var fileSize: Long

        do {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            fileSize = file.length()
            quality -= 5
        } while (fileSize > 400 * 1024 && quality > 5)

        if (fileSize < 200 * 1024) {
            // If the image is too small, increase quality
            do {
                quality += 5
                outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
                outputStream.close()
                fileSize = file.length()
            } while (fileSize < 200 * 1024 && quality < 100)
        }

        val fileNameWithExtension = "$serverImageName.jpg"
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("Image", fileNameWithExtension, requestFile)
        Log.d("####", "uploadImage: $fileNameWithExtension")
        viewModel.setProfileImage(body)
    }

    private fun setServerImageUrlName() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-based, so add 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        serverImageName = String.format("%d%02d%02d", year, month, day)
    }

    private fun showToast(msg : String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UploadProfilePhotoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}