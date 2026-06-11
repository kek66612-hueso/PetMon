package com.pozornik.mypetmon;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PetManager {
    private static final String PREF_NAME = "PetMonData";
    private static final String KEY_PETS = "pets_list";

    public static List<Pet> getPets(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String petsJson = prefs.getString(KEY_PETS, "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Pet>>() {}.getType();
        return gson.fromJson(petsJson, type);
    }

    public static void savePets(Context context, List<Pet> pets) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String petsJson = gson.toJson(pets);
        prefs.edit().putString(KEY_PETS, petsJson).apply();
    }

    public static void addPet(Context context, Pet pet) {
        List<Pet> pets = getPets(context);
        pets.add(pet);
        savePets(context, pets);
    }

    public static void updatePet(Context context, Pet updatedPet) {
        List<Pet> pets = getPets(context);
        for (int i = 0; i < pets.size(); i++) {
            if (pets.get(i).getId().equals(updatedPet.getId())) {
                pets.set(i, updatedPet);
                break;
            }
        }
        savePets(context, pets);
    }
}
