package com.javabobo.reddit.ui.posts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.javabobo.reddit.R
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.utils.CustomItemTouchHelper
import com.javabobo.reddit.utils.DateUtils.getDate
import com.javabobo.reddit.utils.Resource.Status.*
import kotlinx.android.synthetic.main.fragment_post_info.*
import kotlinx.android.synthetic.main.fragment_post_list.*
import org.koin.android.viewmodel.ext.android.viewModel


class PostListFragment : BaseFragment() {


    private val TAG: String = "NewReporterListFragment"
    private lateinit var mAdapter: PostItemAdapter
    private val postListViewModel: PostListViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initRecyclerView()
        subscribeObserver()
        initListeners()


    }

    private fun initListeners() {
        add_news_reporters.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_newReporterListFragment_to_addNewsReporterFragment)

            } catch (exeption: IllegalArgumentException) {
                Log.i(TAG, "illegal argument exeption")
            }

        }
    }

    private fun initAdapter() {
        mAdapter = PostItemAdapter(object : PostItemAdapter.Listener {
            //on remove item called
            override fun remove(post: Post, position: Int) {
                if (firebaseAuth.currentUser?.uid != post.userId) {
                    //reset list
                    mAdapter.notifyItemChanged(position)
                    showErrorDelintingNotAllowed()
                    return
                }
                postListViewModel.removePost(post).observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        SUCCESS -> {
                            mAdapter.removeAt(position)
                            //do nothing
                            comunicatorInterface.hideProgressBar()
                        }
                        ERROR -> {
                            //reset list
                            mAdapter.notifyItemChanged(position)
                            showErrorDeleting()
                            //do nothing
                            comunicatorInterface.hideProgressBar()
                        }
                        LOADING -> {
                            comunicatorInterface.showProgressBar()
                        }
                    }
                })
            }

            override fun onClickItem(post: Post) {
                val action =
                    PostListFragmentDirections.actionPostListFragmentToPostInfoFragment(
                        post
                    )
                try {
                    findNavController().navigate(action)
                } catch (exeption: IllegalArgumentException) {
                    Log.i(TAG, "illegal argument exeption")
                }

            }

            override fun onClickShareButtom(item: Post) {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    val textToShare = """${item.title}
                        |${item.desc}
                        |${getDate(item.timestamp) }
                    """.trimMargin()
                    putExtra(Intent.EXTRA_TEXT, textToShare)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

        })
    }

    private fun showErrorDelintingNotAllowed() {
        Toast.makeText(context, getString(R.string.error_not_allowed_delete), Toast.LENGTH_LONG)
            .show()

    }

    private fun showErrorDeleting() {
        Toast.makeText(context, getString(R.string.msg_error_deleting), Toast.LENGTH_LONG).show()
    }

    private fun initRecyclerView() {
        news_reporter_recyclerview.apply {
            adapter = mAdapter
        }
        val itemTouchHelper = ItemTouchHelper(CustomItemTouchHelper(mAdapter))
        itemTouchHelper.attachToRecyclerView(news_reporter_recyclerview)

    }

    private fun subscribeObserver() {
        postListViewModel.getPosts()
            .observe(viewLifecycleOwner, Observer { response ->
                when (response.status) {
                    SUCCESS -> {
                        response.data?.peekContent()?.let { responseList ->
                            if (responseList.isEmpty()) {
                                showNotDataText()
                            } else {
                                hideNotDataText()
                            }
                            mAdapter.list = responseList
                        }
                        comunicatorInterface.hideProgressBar()
                    }
                    LOADING -> {
                        comunicatorInterface.showProgressBar()
                    }
                    ERROR -> {
                        response.data?.getContentIfNotHandled().let {
                            //avoid to show error msg twice when the screen is rotated
                            showErrorMsg()
                        }

                        comunicatorInterface.hideProgressBar()

                    }
                }

            })
    }

    private fun hideNotDataText() {
        no_data_textview.visibility = GONE
    }

    private fun showNotDataText() {
        no_data_textview.visibility = VISIBLE
    }

    private fun showErrorMsg() {
        Toast.makeText(context, getString(R.string.msg_error_retriving), Toast.LENGTH_LONG).show()
    }


}