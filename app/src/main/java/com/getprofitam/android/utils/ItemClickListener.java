package com.getprofitam.android.utils;


import com.getprofitam.android.adapter.ChildModel;
import com.getprofitam.android.adapter.Section;

public interface ItemClickListener {
    void itemClicked(ChildModel item);
    void itemClicked(Section section);
}
