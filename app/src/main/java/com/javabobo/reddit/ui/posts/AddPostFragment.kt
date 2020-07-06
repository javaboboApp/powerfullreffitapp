package com.javabobo.reddit.ui.posts

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.javabobo.reddit.R
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.utils.Resource.Status.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_add_post.*
import org.koin.android.viewmodel.ext.android.viewModel


class AddPostFragment : BaseFragment() {
    private var imageUri: Uri? =null
    private val GALLERY_REQUEST_CODE: Int = 123
    private val addPostViewModel: AddPostViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initListeners()
    }

    private fun initListeners() {
        image_imageview.setOnClickListener {
            if (comunicatorInterface.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        activity?.let {
                            launchImageCrop(uri)
                        }
                    }
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    //get the result...
                     imageUri = result.uri
                    Glide.with(this)
                        .load(imageUri)
                        .into(image_imageview)


                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {


                }
            }
        }
    }

    private fun launchImageCrop(uri: Uri) {
        context?.let {
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    private fun insert() {
        comunicatorInterface.hideKeyBoard()
        focusable_view.requestFocus()
        val newReporter = validate()
        if (newReporter == null) {
            showValidationError()
            return
        }

        addAndObserver(newReporter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.insert_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.insert -> {
                insert()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun addAndObserver(validate: Post) {
        setMenuVisibility(false)
        addPostViewModel.addPost(validate)
            .observe(viewLifecycleOwner, Observer { result ->

                setMenuVisibility(true)
                when (result.status) {
                    SUCCESS -> {
                        findNavController().popBackStack()
                        comunicatorInterface.hideProgressBar()
                        hideImageProgressBar()

                    }
                    ERROR
                    -> {
                        showInsertionError()
                        comunicatorInterface.hideProgressBar()
                        hideImageProgressBar()
                    }
                    LOADING
                    -> {
                        comunicatorInterface.showProgressBar()
                    }
                    LOADING_IMG -> {
                        showLoadingImageProgressBar()
                    }
                }
            })
    }

    private fun hideImageProgressBar() {
        loading_image_progressbar.visibility = View.INVISIBLE

    }

    private fun showLoadingImageProgressBar() {
        loading_image_progressbar.visibility = View.VISIBLE
    }

    private fun showValidationError() {
        Toast.makeText(context, getString(R.string.not_empty_field_error), Toast.LENGTH_LONG).show()
    }


    private fun showInsertionError() {
        Toast.makeText(context, getString(R.string.error_inserting_msg), Toast.LENGTH_LONG).show()
    }

    private fun validate(): Post? {
        val title = title_edittext.text.toString()
        val desc = desc_editText.text.toString()

        if (title.trim().isEmpty() || desc.trim().isEmpty()) {
            return null
        }
        return createPost(title, desc)
    }

    private fun createPost(title: String, desc: String): Post {
        val post = Post()

        post.title = title
        post.desc = desc
        post.timestamp = System.currentTimeMillis()
        post.imageUri = imageUri
        return post
    }
}