package com.shudh4sure.shopping.utils;


import com.shudh4sure.shopping.adapter.ChildModel;
import com.shudh4sure.shopping.adapter.Section;

public interface ItemClickListener {
    void itemClicked(ChildModel item);
    void itemClicked(Section section);
}
