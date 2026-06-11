package com.pozornik.mypetmon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> petList;
    private OnPetClickListener listener;
    private String currentTheme;

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    public PetAdapter(List<Pet> petList, String theme, OnPetClickListener listener) {
        this.petList = petList;
        this.currentTheme = theme;
        this.listener = listener;
    }

    public void setPets(List<Pet> pets) {
        this.petList = pets;
        notifyDataSetChanged();
    }
    
    public void setTheme(String theme) {
        this.currentTheme = theme;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.tvItemPetName.setText(pet.getName());
        holder.tvItemPetAvatar.setText(pet.getAvatar());
        holder.tvItemPetStatus.setText(pet.getStatusText());
        
        if (pet.getNextProcedure() != null && !pet.getNextProcedure().isEmpty()) {
            holder.tvItemNextProcedure.setText(pet.getNextProcedure());
            holder.tvItemNextProcedure.setVisibility(View.VISIBLE);
        } else {
            holder.tvItemNextProcedure.setVisibility(View.GONE);
        }

        // Применяем тему программно
        if ("night".equals(currentTheme)) {
            ((androidx.cardview.widget.CardView) holder.itemView).setCardBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"));
            holder.tvItemPetName.setTextColor(android.graphics.Color.WHITE);
            holder.tvItemPetStatus.setTextColor(android.graphics.Color.parseColor("#BDBDBD"));
        } else {
            ((androidx.cardview.widget.CardView) holder.itemView).setCardBackgroundColor(android.graphics.Color.WHITE);
            holder.tvItemPetName.setTextColor(android.graphics.Color.parseColor("#2D3436"));
            holder.tvItemPetStatus.setTextColor(android.graphics.Color.parseColor("#9E9E9E"));
        }

        holder.itemView.setOnClickListener(v -> listener.onPetClick(pet));
    }

    @Override
    public int getItemCount() {
        return petList != null ? petList.size() : 0;
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemPetName, tvItemPetAvatar, tvItemPetStatus, tvItemNextProcedure;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemPetName = itemView.findViewById(R.id.tvItemPetName);
            tvItemPetAvatar = itemView.findViewById(R.id.tvItemPetAvatar);
            tvItemPetStatus = itemView.findViewById(R.id.tvItemPetStatus);
            tvItemNextProcedure = itemView.findViewById(R.id.tvItemNextProcedure);
        }
    }
}
