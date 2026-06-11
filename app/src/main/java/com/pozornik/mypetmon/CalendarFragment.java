package com.pozornik.mypetmon;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private TextView tvMonthYear, tvSelectedDateEvents;
    private RecyclerView rvCalendarGrid;
    private LocalDate selectedDate;
    private CalendarAdapter calendarAdapter;
    private Map<String, String> eventsMap;
    private static final String PREFS_CALENDAR = "CalendarEvents";
    private View rootLayout;
    private TextView tvCalendarTitle;
    private androidx.cardview.widget.CardView calendarCardView;
    private Button btnPrevMonth, btnNextMonth;
    private TextView tvDaySat, tvDaySun;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        rootLayout = view.findViewById(R.id.calendarRootLayout);
        if (rootLayout == null) {
            rootLayout = view; // Fallback
        }

        tvCalendarTitle = view.findViewById(R.id.tvCalendarTitle);
        calendarCardView = view.findViewById(R.id.calendarCardView);
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        tvSelectedDateEvents = view.findViewById(R.id.tvSelectedDateEvents);
        rvCalendarGrid = view.findViewById(R.id.rvCalendarGrid);
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        tvDaySat = view.findViewById(R.id.tvDaySat);
        tvDaySun = view.findViewById(R.id.tvDaySun);

        loadEvents();
        selectedDate = LocalDate.now();
        setMonthView();
        
        updateUI();

        btnPrevMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.minusMonths(1);
            setMonthView();
        });

        btnNextMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.plusMonths(1);
            setMonthView();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvents();
        setMonthView();
        updateUI();
    }
    
    private void updateUI() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme_mode", "day");

        if (theme.equals("night")) {
            if (rootLayout != null) rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            if (calendarCardView != null) calendarCardView.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            if (tvCalendarTitle != null) tvCalendarTitle.setTextColor(Color.parseColor("#E0E0E0"));
            tvMonthYear.setTextColor(Color.parseColor("#E0E0E0"));
            tvSelectedDateEvents.setTextColor(Color.WHITE);
            
            if (btnPrevMonth != null) btnPrevMonth.setTextColor(Color.parseColor("#E0E0E0"));
            if (btnNextMonth != null) btnNextMonth.setTextColor(Color.parseColor("#E0E0E0"));
            if (tvDaySat != null) tvDaySat.setTextColor(Color.parseColor("#E0E0E0"));
            if (tvDaySun != null) tvDaySun.setTextColor(Color.parseColor("#E0E0E0"));
        } else {
            if (rootLayout != null) rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            if (calendarCardView != null) calendarCardView.setCardBackgroundColor(Color.WHITE);
            if (tvCalendarTitle != null) tvCalendarTitle.setTextColor(Color.parseColor("#2D3436"));
            tvMonthYear.setTextColor(Color.parseColor("#2D3436"));
            tvSelectedDateEvents.setTextColor(Color.parseColor("#2D3436"));
            
            if (btnPrevMonth != null) btnPrevMonth.setTextColor(Color.parseColor("#FF8A65"));
            if (btnNextMonth != null) btnNextMonth.setTextColor(Color.parseColor("#FF8A65"));
            if (tvDaySat != null) tvDaySat.setTextColor(Color.parseColor("#FF8A65"));
            if (tvDaySun != null) tvDaySun.setTextColor(Color.parseColor("#FF8A65"));
        }
        
        if (calendarAdapter != null) {
            calendarAdapter.setTheme(theme);
        }
    }

    private void loadEvents() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_CALENDAR, Context.MODE_PRIVATE);
        String json = prefs.getString("events_map", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        eventsMap = gson.fromJson(json, type);
        if (eventsMap == null) eventsMap = new HashMap<>();
    }

    private void saveEvents() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_CALENDAR, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(eventsMap);
        prefs.edit().putString("events_map", json).apply();
    }

    private void setMonthView() {
        tvMonthYear.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
        
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme_mode", "day");

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        rvCalendarGrid.setLayoutManager(layoutManager);

        calendarAdapter = new CalendarAdapter(daysInMonth, selectedDate, this::onItemClick, eventsMap, theme);
        rvCalendarGrid.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i < dayOfWeek || i >= daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek + 1));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        String formatted = date.format(formatter);
        return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
    }

    private String getFullDateKey(String dayText) {
        int day = Integer.parseInt(dayText);
        LocalDate date = selectedDate.withDayOfMonth(day);
        return date.toString(); // YYYY-MM-DD
    }

    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            String dateKey = getFullDateKey(dayText);
            
            calendarAdapter.setSelectedDay(dayText);
            calendarAdapter.notifyDataSetChanged();

            if (eventsMap.containsKey(dateKey)) {
                tvSelectedDateEvents.setText("Задачи на " + dayText + " число:\n" + eventsMap.get(dateKey));
            } else {
                tvSelectedDateEvents.setText("На " + dayText + " число задач нет.");
            }

            showAddEventDialog(dateKey, dayText);
        }
    }

    private void showAddEventDialog(String dateKey, String dayText) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_calendar_event);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = dialog.findViewById(R.id.tvDialogEventTitle);
        tvTitle.setText("Событие на " + dayText + " число");

        TextInputEditText etEventDescription = dialog.findViewById(R.id.etEventDescription);
        Button btnSaveEvent = dialog.findViewById(R.id.btnSaveEvent);
        Button btnCancelEvent = dialog.findViewById(R.id.btnCancelEvent);
        
        // Применяем тему к диалогу программно
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme_mode", "day");
        
        View dialogRoot = dialog.findViewById(R.id.dialogRootLayout);
        if (dialogRoot == null) {
            dialogRoot = ((ViewGroup)dialog.findViewById(android.R.id.content)).getChildAt(0);
        }
        
        if (theme.equals("night")) {
            if (dialogRoot != null) dialogRoot.setBackgroundColor(Color.parseColor("#1E1E1E"));
            tvTitle.setTextColor(Color.WHITE);
            etEventDescription.setTextColor(Color.WHITE);
            etEventDescription.setHintTextColor(Color.parseColor("#9E9E9E"));
            btnCancelEvent.setTextColor(Color.parseColor("#E0E0E0"));
            btnSaveEvent.setTextColor(Color.WHITE);
        } else {
            if (dialogRoot != null) dialogRoot.setBackgroundColor(Color.WHITE);
            tvTitle.setTextColor(Color.parseColor("#2D3436"));
            etEventDescription.setTextColor(Color.parseColor("#2D3436"));
            etEventDescription.setHintTextColor(Color.parseColor("#9E9E9E"));
            btnCancelEvent.setTextColor(Color.parseColor("#2D3436"));
            btnSaveEvent.setTextColor(Color.parseColor("#2D3436"));
        }

        if (eventsMap.containsKey(dateKey)) {
            etEventDescription.setText(eventsMap.get(dateKey));
        }

        btnCancelEvent.setOnClickListener(v -> dialog.dismiss());

        btnSaveEvent.setOnClickListener(v -> {
            String desc = etEventDescription.getText().toString().trim();
            if (!desc.isEmpty()) {
                eventsMap.put(dateKey, desc);
                saveEvents();
                scheduleNotification(dateKey, desc);
                tvSelectedDateEvents.setText("Задачи на " + dayText + " число:\n" + desc);
                calendarAdapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Событие сохранено", Toast.LENGTH_SHORT).show();
            } else {
                eventsMap.remove(dateKey);
                saveEvents();
                tvSelectedDateEvents.setText("На " + dayText + " число задач нет.");
                calendarAdapter.notifyDataSetChanged();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void scheduleNotification(String dateKey, String desc) {
        try {
            LocalDate eventDate = LocalDate.parse(dateKey);
            // Установим напоминание на 9 утра в день события (или сразу, если время прошло)
            long triggerAtMillis = eventDate.atTime(9, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            
            // Если событие сегодня или в прошлом, для теста покажем уведомление через 5 секунд
            if (triggerAtMillis < System.currentTimeMillis()) {
                triggerAtMillis = System.currentTimeMillis() + 5000;
            }

            Intent intent = new Intent(requireContext(), EventNotificationReceiver.class);
            intent.putExtra("event_desc", desc);
            int requestCode = dateKey.hashCode();
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ИНТЕРФЕЙС
    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }

    // АДАПТЕР
    public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
        private final ArrayList<String> daysOfMonth;
        private final OnItemListener onItemListener;
        private String selectedDay;
        private Map<String, String> events;
        private LocalDate currentMonthDate;
        private String currentTheme;

        public CalendarAdapter(ArrayList<String> daysOfMonth, LocalDate date, OnItemListener onItemListener, Map<String, String> events, String theme) {
            this.daysOfMonth = daysOfMonth;
            this.onItemListener = onItemListener;
            this.events = events;
            this.currentMonthDate = date;
            this.currentTheme = theme;

            if (date.getMonth() == LocalDate.now().getMonth() && date.getYear() == LocalDate.now().getYear()) {
                this.selectedDay = String.valueOf(LocalDate.now().getDayOfMonth());
            } else {
                this.selectedDay = "";
            }
        }
        
        public void setTheme(String theme) {
            this.currentTheme = theme;
            notifyDataSetChanged();
        }

        public void setSelectedDay(String day) {
            this.selectedDay = day;
        }

        @NonNull
        @Override
        public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
            return new CalendarViewHolder(view, onItemListener, daysOfMonth);
        }

        @Override
        public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
            String day = daysOfMonth.get(position);
            holder.tvDayCell.setText(day);

            if (!day.isEmpty()) {
                String dateKey = currentMonthDate.withDayOfMonth(Integer.parseInt(day)).toString();
                if (events.containsKey(dateKey)) {
                    // Визуально выделяем дни с событиями
                    if ("night".equals(currentTheme)) {
                        holder.tvDayCell.setTextColor(Color.WHITE);
                    } else {
                        holder.tvDayCell.setTextColor(Color.parseColor("#FF8A65"));
                    }
                } else {
                    if ("night".equals(currentTheme)) {
                        holder.tvDayCell.setTextColor(Color.parseColor("#E0E0E0"));
                    } else {
                        holder.tvDayCell.setTextColor(Color.parseColor("#2D3436"));
                    }
                }
            }

            if (day.equals(selectedDay) && !day.isEmpty()) {
                holder.tvDayCell.setBackgroundResource(R.drawable.bg_circle);
                holder.tvDayCell.setTextColor(Color.WHITE);
            } else {
                holder.tvDayCell.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        @Override
        public int getItemCount() {
            return daysOfMonth.size();
        }

        public class CalendarViewHolder extends RecyclerView.ViewHolder {
            public final TextView tvDayCell;

            public CalendarViewHolder(@NonNull View itemView, OnItemListener listener, ArrayList<String> days) {
                super(itemView);
                tvDayCell = itemView.findViewById(R.id.tvDayCell);
                itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition())));
            }
        }
    }
}