package com.javabobo.reddit.ui.posts

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.javabobo.reddit.R
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.utils.Resource
import kotlinx.android.synthetic.main.fragment_update_post_info.*
import kotlinx.android.synthetic.main.fragment_update_post_info.focusable_view
import org.koin.android.ext.android.inject

import org.koin.android.viewmodel.ext.android.viewModel


class UpdatePostInfoFragment : BaseFragment() {
    private lateinit var post: Post

    private val updateNewsReporterViewModel: UpdatePostViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_post_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPost(view)
        setHasOptionsMenu(firebaseAuth.currentUser?.uid == post.userId)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    private fun validate(): Post? {
        val title = title_edittext.text.toString()
        val desc = desc_editText.text.toString()

        if (title.trim().isEmpty() || desc.trim().isEmpty()) {
            return null
        }
         updatePost(title, desc)
        return post
    }

    private fun updatePost(title: String, desc: String) {
        post.title = title
        post.desc = desc
        post.timestamp = System.currentTimeMillis()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.update -> {
                update()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun update() {
        comunicatorInterface.hideKeyBoard()
        focusable_view.requestFocus()

        val post = validate()
        if (post != null) {

            setMenuVisibility(false)
            updateNewsReporterViewModel.updatePost(post)
                .observe(viewLifecycleOwner, Observer { result ->
                    setMenuVisibility(true)
                    when (result.status) {
                        Resource.Status.SUCCESS -> {
                            findNavController().popBackStack(R.id.PostListFragment, false)
                            comunicatorInterface.hideProgressBar()

                        }
                        Resource.Status.ERROR
                        -> {
                            showUpdatingError()
                            comunicatorInterface.hideProgressBar()
                        }
                        Resource.Status.LOADING
                        -> {
                            comunicatorInterface.showProgressBar()
                        }
                    }
                })
        }
    }

    private fun showUpdatingError() {
        Toast.makeText(context, getString(R.string.error_updating_msg), Toast.LENGTH_LONG).show()

    }

    private fun setUpPost(view: View) {
         post = UpdatePostInfoFragmentArgs.fromBundle(requireArguments()).post
        title_edittext.setText(post.title ?: "")
        desc_editText.setText(post.desc ?: "")

    }


}