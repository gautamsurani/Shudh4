package com.shudh4sure.shopping.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class SectionedExpandableLayoutHelper implements SectionStateChangeListener {

    //data list
    private LinkedHashMap<Section, ArrayList<ChildModel>> mSectionDataMap =
            new LinkedHashMap<Section, ArrayList<ChildModel>>();
    private ArrayList<Object> mDataArrayList = new ArrayList<Object>();

    //section map
    //TODO : look for a way to avoid this
    private HashMap<Section, Section> mSectionMap = new HashMap<Section, Section>();

    //adapter
    private SectionedExpandableGridAdapter mSectionedExpandableGridAdapter;

    //recycler view
    RecyclerView mRecyclerView;

    public SectionedExpandableLayoutHelper(Context context, RecyclerView recyclerView, ItemClickListener itemClickListener,
                                           int gridSpanCount) {

        //setting the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new SectionedExpandableGridAdapter(context, mDataArrayList,
                gridLayoutManager, itemClickListener, this);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);

        mRecyclerView = recyclerView;
    }

    public void notifyDataSetChanged(Section fget,int pos) {
        generateDataList(fget);
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(mDataArrayList.lastIndexOf(fget));
    }

    public void notifyDataSetChanged() {
        generateDataList();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    public void addSection(Section section, ArrayList<ChildModel> items) {
        Section newSection;
        mSectionMap.put(section, (newSection = new Section(section.getCatID(),section.getName(),section.getIcon(),section
        .getSubcat())));
        mSectionDataMap.put(newSection, items);
    }

    /*public void addItem(String section, ChildModel item) {
        mSectionDataMap.get(mSectionMap.get(section)).add(item);
    }*/

    /*public void removeItem(String section, ChildModel item) {
        mSectionDataMap.get(mSectionMap.get(section)).remove(item);
    }
*/
    /*public void removeSection(String section) {
        mSectionDataMap.remove(mSectionMap.get(section));
        mSectionMap.remove(section);
    }*/

    private void generateDataList (Section oldF) {
        mDataArrayList.clear();



        for (Map.Entry<Section, ArrayList<ChildModel>> entry : mSectionDataMap.entrySet()) {
            Section key;
            mDataArrayList.add((key = entry.getKey()));


            if (key.isExpanded && oldF.getCatID().equals(key.getCatID())) {
                mDataArrayList.addAll(entry.getValue());
            }else {
                key.isExpanded=false;
            }
        }

    }

    private void generateDataList () {
        mDataArrayList.clear();


        for (Map.Entry<Section, ArrayList<ChildModel>> entry : mSectionDataMap.entrySet()) {
            Section key;
            mDataArrayList.add((key = entry.getKey()));

            if (key.isExpanded) {
                mDataArrayList.addAll(entry.getValue());
            }

        }


    }


    @Override
    public void onSectionStateChanged(Section section, boolean isOpen,int pos) {
        if (!mRecyclerView.isComputingLayout()) {
            section.isExpanded = isOpen;
            notifyDataSetChanged(section,pos);
        }
    }
}
