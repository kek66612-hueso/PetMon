package com.pozornik.mypetmon;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import androidx.appcompat.app.AlertDialog;

public class HomeFragment extends Fragment {

    private TextView tvHomeTitleMain, tvHomeTitleEvents;
    private FloatingActionButton fabAddRecord;
    private View rootLayout;
    
    private RecyclerView rvPets;
    private PetAdapter petAdapter;
    
    private String tempEmoji = "🐶";
    private final String[] emojis = {"🐶", "🐱", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rootLayout = view;

        rvPets = view.findViewById(R.id.rvPets);
        rvPets.setLayoutManager(new LinearLayoutManager(getContext()));

        tvHomeTitleMain = view.findViewById(R.id.tvHomeTitleMain);
        tvHomeTitleEvents = view.findViewById(R.id.tvHomeTitleEvents);
        fabAddRecord = view.findViewById(R.id.fabAddRecord);

        updateUI();

        fabAddRecord.setOnClickListener(v -> showPetDialog(null));

        return view;
    }

    private void loadPets(String theme) {
        List<Pet> pets = PetManager.getPets(requireContext());
        if (petAdapter == null) {
            petAdapter = new PetAdapter(pets, theme, pet -> {
                showPetDialog(pet); // Открываем редактирование
            });
            rvPets.setAdapter(petAdapter);
        } else {
            petAdapter.setTheme(theme);
            petAdapter.setPets(pets);
        }
    }

    private void showPetDialog(Pet existingPet) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_pet_details);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Программная покраска диалога под тему
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme_mode", "day");
        
        View dialogRoot = dialog.findViewById(R.id.dialogRootLayout); // Нужно добавить ID в XML, или найти первый ребенок
        if (dialogRoot == null) {
            dialogRoot = ((ViewGroup)dialog.findViewById(android.R.id.content)).getChildAt(0);
        }
        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        com.google.android.material.textfield.TextInputLayout tilPetName = dialog.findViewById(R.id.tilPetName);
        com.google.android.material.textfield.TextInputLayout tilPetStatus = dialog.findViewById(R.id.tilPetStatus);
        TextInputEditText etPetName = dialog.findViewById(R.id.etPetName);
        TextInputEditText etPetStatus = dialog.findViewById(R.id.etPetStatus);
        TextView tvDialogAvatar = dialog.findViewById(R.id.tvDialogAvatar);
        View avatarContainer = dialog.findViewById(R.id.avatarContainer);
        Button btnSavePet = dialog.findViewById(R.id.btnSavePet);
        Button btnCancelPet = dialog.findViewById(R.id.btnCancelPet);

        if (theme.equals("night")) {
            if (dialogRoot != null) dialogRoot.setBackgroundColor(Color.parseColor("#1E1E1E"));
            tvDialogTitle.setTextColor(Color.WHITE);
            etPetName.setTextColor(Color.WHITE);
            etPetStatus.setTextColor(Color.WHITE);
            btnCancelPet.setTextColor(Color.parseColor("#E0E0E0"));
            btnSavePet.setTextColor(Color.WHITE);
            
            if (tilPetName != null) {
                tilPetName.setDefaultHintTextColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                tilPetName.setHintTextColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
            }
            if (tilPetStatus != null) {
                tilPetStatus.setDefaultHintTextColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                tilPetStatus.setHintTextColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
            }
        } else {
            if (dialogRoot != null) dialogRoot.setBackgroundColor(Color.WHITE);
            tvDialogTitle.setTextColor(Color.parseColor("#2D3436"));
            etPetName.setTextColor(Color.parseColor("#2D3436"));
            etPetStatus.setTextColor(Color.parseColor("#2D3436"));
            btnCancelPet.setTextColor(Color.parseColor("#2D3436"));
            btnSavePet.setTextColor(Color.parseColor("#2D3436"));
            
            if (tilPetName != null) {
                tilPetName.setDefaultHintTextColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                tilPetName.setHintTextColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#2D3436")));
            }
            if (tilPetStatus != null) {
                tilPetStatus.setDefaultHintTextColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                tilPetStatus.setHintTextColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#2D3436")));
            }
        }

        if (existingPet != null) {
            tvDialogTitle.setText("Редактировать питомца");
            etPetName.setText(existingPet.getName());
            etPetStatus.setText(existingPet.getStatusText());
            tempEmoji = existingPet.getAvatar();
        } else {
            tvDialogTitle.setText("Добавить питомца");
            tempEmoji = "🐶";
        }
        
        tvDialogAvatar.setText(tempEmoji);

        avatarContainer.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Выберите аватара")
                    .setItems(emojis, (d, which) -> {
                        tempEmoji = emojis[which];
                        tvDialogAvatar.setText(tempEmoji);
                    }).show();
        });

        btnCancelPet.setOnClickListener(v -> dialog.dismiss());

        btnSavePet.setOnClickListener(v -> {
            String name = etPetName.getText().toString().trim();
            String status = etPetStatus.getText().toString().trim();

            if (name.isEmpty()) {
                etPetName.setError("Введите имя");
                return;
            }

            if (status.isEmpty()) status = "Без статуса";

            if (existingPet != null) {
                existingPet.setName(name);
                existingPet.setAvatar(tempEmoji);
                existingPet.setStatusText(status);
                PetManager.updatePet(requireContext(), existingPet);
                Toast.makeText(requireContext(), "Питомец обновлен!", Toast.LENGTH_SHORT).show();
            } else {
                Pet newPet = new Pet(name, tempEmoji, status, "");
                PetManager.addPet(requireContext(), newPet);
                Toast.makeText(requireContext(), "Питомец добавлен!", Toast.LENGTH_SHORT).show();
            }
            
            loadPets(theme);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateUI() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme_mode", "day");

        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            tvHomeTitleMain.setTextColor(Color.parseColor("#E0E0E0"));
            tvHomeTitleEvents.setTextColor(Color.parseColor("#E0E0E0"));
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            tvHomeTitleMain.setTextColor(Color.parseColor("#2D3436"));
            tvHomeTitleEvents.setTextColor(Color.parseColor("#2D3436"));
        }
        
        loadPets(theme);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}