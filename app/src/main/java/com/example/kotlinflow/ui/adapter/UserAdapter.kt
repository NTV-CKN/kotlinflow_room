package com.example.kotlinflow.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinflow.R
import com.example.kotlinflow.data.local.model.User
import com.example.kotlinflow.databinding.ItemUserBinding
import com.example.kotlinflow.ui.OnMenuUserClick


class UserAdapter(private val onMenuUserClick: OnMenuUserClick) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private val users = mutableListOf<User>()

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(user: User) {
            binding.tvEmailUser.text = user.email
            binding.tvFullNameUser.text = user.fullName
            binding.imgMore.setOnClickListener {
                val popupMenu = PopupMenu(binding.root.context, binding.imgMore)
                popupMenu.inflate(R.menu.menu_more)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener {
                    onMenuUserClick.onMenuUserClick(it, user)
                }
            }

            Glide.with(binding.root)
                .load(user.img)
                .error(ContextCompat.getDrawable(binding.root.context, R.drawable.ic_img_not_sp))
                .into(binding.imgAvatarUser)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsers(users: List<User>) {
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(users[position])
    }
}