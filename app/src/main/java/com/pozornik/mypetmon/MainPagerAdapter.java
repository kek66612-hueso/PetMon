package com.pozornik.mypetmon;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Порядок экранов: 0 - Настройки, 1 - Главная, 2 - Профиль
        switch (position) {
            case 0: return new SettingsFragment();
            case 2: return new ProfileFragment();
            default: return new HomeFragment(); // По умолчанию центр (1)
        }
    }

    @Override
    public int getItemCount() {
        return 3; // У нас 3 экрана
    }
}