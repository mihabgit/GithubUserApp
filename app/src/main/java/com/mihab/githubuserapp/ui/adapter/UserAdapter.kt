package com.mihab.githubuserapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mihab.githubuserapp.data.model.User
import com.mihab.githubuserapp.databinding.ItemUserBinding
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var users: List<User>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.binding.apply {
            val user = users[position]

            Picasso.get().load(user.avatar_url).into(civProPic)
            tvName.text = user.login
            tvProfileUrl.text = user.url

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(user) }
            }
        }
    }

    override fun getItemCount() = users.size

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }
}