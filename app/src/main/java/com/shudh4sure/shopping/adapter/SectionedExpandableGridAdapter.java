package com.shudh4sure.shopping.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.shudh4sure.shopping.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickListener mItemClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.layout_section;
    private static final int VIEW_TYPE_ITEM = R.layout.layout_item;

    public SectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                          final GridLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                          SectionStateChangeListener sectionStateChangeListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isSection(position)?gridLayoutManager.getSpanCount():1;
            }
        });
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof Section;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_ITEM :
                final ChildModel item = (ChildModel) mDataArrayList.get(position);
                holder.itemTextView.setText(item.getName());

                try {
                    Picasso.with(mContext)
                            .load(item.getIcon().trim())
                            .placeholder(R.drawable.ic_default_product_one)
                            .noFade()
                            .into(holder.itemImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(item);
                    }
                });
                break;
            case VIEW_TYPE_SECTION :
                final Section section = (Section) mDataArrayList.get(position);
                holder.sectionTextView.setText(section.getName());

                try {
                    Picasso.with(mContext)
                            .load(section.getIcon().trim())
                            .placeholder(R.drawable.ic_default_product_one)
                            .noFade()
                            .into(holder.sectionIMG);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                holder.secCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("ddldlk","ololol"+section.isExpanded);
                        holder.sectionToggleButton.setChecked(!section.isExpanded);
                       // mSectionStateChangeListener.onSectionStateChanged(section, true);
                    }
                });


                if ( section.getSubcat().equalsIgnoreCase("no")){
                    holder.sectionToggleButton.setVisibility(View.INVISIBLE);

                    holder.secCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(section);
                    }
                });

                }else if (section.getSubcat().equalsIgnoreCase("yes")){

                    holder.sectionToggleButton.setVisibility(View.VISIBLE);

                    holder.sectionToggleButton.setChecked(section.isExpanded);
                    holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            mSectionStateChangeListener.onSectionStateChanged(section, isChecked,holder.getAdapterPosition());
                        }
                    });




                }

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position))
            return VIEW_TYPE_SECTION;
        else return VIEW_TYPE_ITEM;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;

        //for section
        TextView sectionTextView;
        ToggleButton sectionToggleButton;
        ImageView sectionIMG;
        CardView secCard;

        //for item
        TextView itemTextView;
        ImageView itemImage;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = (TextView) view.findViewById(R.id.text_item);
                itemImage = (ImageView) view.findViewById(R.id.itemImage);
            } else {
                sectionTextView = (TextView) view.findViewById(R.id.text_section);
                sectionToggleButton = (ToggleButton) view.findViewById(R.id.toggle_button_section);
                sectionIMG = (ImageView) view.findViewById(R.id.sectionIMG);
                secCard = (CardView) view.findViewById(R.id.secCard);
            }
        }
    }
}
