package com.getprofitam.android.adapter;


public interface SectionStateChangeListener {
    void onSectionStateChanged(Section section, boolean isOpen, int position);
}