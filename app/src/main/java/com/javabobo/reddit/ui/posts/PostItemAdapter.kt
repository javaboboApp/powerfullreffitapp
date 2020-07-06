package com.javabobo.reddit.ui.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.javabobo.reddit.R
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.utils.Constants.PATH_IMAGE
import com.javabobo.reddit.utils.DateUtils.getDate
import kotlinx.android.synthetic.main.item_adapter_post.view.*

class PostItemAdapter(val listener: Listener) :
    RecyclerView.Adapter<PostItemAdapter.ItemViewHolder>() {
    var list: List<Post> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_adapter_post, parent, false)
        return ItemViewHolder(
            view
        )
    }

    fun remove(position: Int) {
        listener.remove(list[position],position)
    }

    fun removeAt(position: Int){
        list.toMutableList().removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface Listener {
        fun remove(post: Post, position: Int)
        fun onClickItem(item: Post)
        fun onClickShareButtom(item: Post)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(post = list[position], listener = listener)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Post, listener: Listener) = with(itemView) {
            title_textview.text =post.title
            create_date_textview.text = getDate(post.timestamp)
            setOnClickListener {
                listener.onClickItem(post)
            }
            share_imageview.setOnClickListener {
                listener.onClickShareButtom(post)
            }
            Glide.with(context)
                .load(PATH_IMAGE + post.postId + "?alt=media")
                .apply( RequestOptions().override(600, 600))
                .into(image_imageview)

        }
    }

}