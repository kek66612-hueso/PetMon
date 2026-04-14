package com.pozornik.mypetmon;

import android.graphics.Color;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarFragment extends Fragment {

    private TextView tvMonthYear, tvSelectedDateEvents;
    private RecyclerView rvCalendarGrid;
    private LocalDate selectedDate;
    private CalendarAdapter calendarAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        tvSelectedDateEvents = view.findViewById(R.id.tvSelectedDateEvents);
        rvCalendarGrid = view.findViewById(R.id.rvCalendarGrid);
        Button btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        Button btnNextMonth = view.findViewById(R.id.btnNextMonth);

        selectedDate = LocalDate.now();
        setMonthView();

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

    private void setMonthView() {
        tvMonthYear.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        rvCalendarGrid.setLayoutManager(layoutManager);

        calendarAdapter = new CalendarAdapter(daysInMonth, selectedDate, this::onItemClick);
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

    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            String message = "Выбрано: " + dayText + " " + monthYearFromDate(selectedDate);
            tvSelectedDateEvents.setText("Задачи на " + dayText + " число:");

            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

            calendarAdapter.setSelectedDay(dayText);
            calendarAdapter.notifyDataSetChanged();
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

        public CalendarAdapter(ArrayList<String> daysOfMonth, LocalDate date, OnItemListener onItemListener) {
            this.daysOfMonth = daysOfMonth;
            this.onItemListener = onItemListener;

            if (date.getMonth() == LocalDate.now().getMonth() && date.getYear() == LocalDate.now().getYear()) {
                this.selectedDay = String.valueOf(LocalDate.now().getDayOfMonth());
            } else {
                this.selectedDay = "";
            }
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

            if (day.equals(selectedDay) && !day.isEmpty()) {
                holder.tvDayCell.setBackgroundResource(R.drawable.bg_circle);
                holder.tvDayCell.setTextColor(Color.WHITE);
            } else {
                holder.tvDayCell.setBackgroundColor(Color.TRANSPARENT);
                holder.tvDayCell.setTextColor(Color.parseColor("#2D3436"));
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