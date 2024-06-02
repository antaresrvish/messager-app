package com.example.messager.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messager.databinding.ItemContainerConversionsBinding;
import com.example.messager.listeners.ConversionListener;
import com.example.messager.models.ChatMessage;
import com.example.messager.models.User;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>{

    private final List<ChatMessage> chatMessageList;
    private final ConversionListener conversionListener;


    public RecentConversationsAdapter(List<ChatMessage> chatMessageList, ConversionListener conversionListener) {
        this.chatMessageList = chatMessageList;
        this.conversionListener = conversionListener;
    }


    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerConversionsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }


    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessageList.get(position));
    }


    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }


    class ConversionViewHolder extends RecyclerView.ViewHolder {
        ItemContainerConversionsBinding binding;
        ConversionViewHolder(ItemContainerConversionsBinding itemContainerConversionsBinding) {
            super(itemContainerConversionsBinding.getRoot());
            binding = itemContainerConversionsBinding;
        }


        void setData(ChatMessage chatMessage) {
            binding.imageProfile.setImageBitmap(getConversationImage(chatMessage.conversionImage));
            binding.textName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversionListener.onConversionClicked(user);
            });
        }
    }


    private Bitmap getConversationImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
