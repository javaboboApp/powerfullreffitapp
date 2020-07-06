package com.javabobo.reddit.ui.posts

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.javabobo.reddit.R
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.utils.Constants
import com.javabobo.reddit.utils.DateUtils.getDate
import kotlinx.android.synthetic.main.fragment_post_info.*
import kotlinx.android.synthetic.main.item_adapter_post.view.*

class PostInfoFragment : BaseFragment() {


    private val TAG: String = "PostInfoFragment"
    private lateinit var post: Post

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_info, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPost()
        setHasOptionsMenu(firebaseAuth.currentUser?.uid == post.userId)

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                navigateToUpdate()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToUpdate() {
        val action =
            PostInfoFragmentDirections.actionPostInfoFragmentToUpdatePostInfoFragment(
                post
            )
        try {
            findNavController().navigate(action)
        } catch (exeption: IllegalArgumentException) {
            Log.i(TAG, "illegal argument exeption")
        }
    }

    private fun setUpPost() {
         post = PostInfoFragmentArgs.fromBundle(requireArguments()).post
        Glide.with(requireContext())
            .load(Constants.PATH_IMAGE + post.postId + "?alt=media")
            .apply( RequestOptions().override(600, 600))
            .into(image_imageview)
        title_textview.text = post.title
        desc_textview.text = post.desc
        date_textview.text = getDate(post.timestamp)
    }
}