package com.getprofitam.android.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getprofitam.android.R;
import com.getprofitam.android.activity.CategoryActivity;
import com.getprofitam.android.model.SubCategoryModel;

import java.util.HashMap;
import java.util.List;


public class SubCategoryAdapter extends BaseExpandableListAdapter {

	Activity _context;
	public List<SubCategoryModel> _listDataHeader;
	public HashMap<SubCategoryModel, List<SubCategoryModel>> _listDataChild;
	public  static ImageView plusminus;
	SubCategoryModel objChild;
	int lastExpandedGroupPosition = 0;
	public SubCategoryAdapter(Activity context, List<SubCategoryModel> listDataHeader, HashMap<SubCategoryModel, List<SubCategoryModel>> listChildData)
	{
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public View getChildView(int groupPosition, final int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {

		LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = infalInflater.inflate(R.layout.layout_child_view, null);
		final TextView tvfname = (TextView) convertView.findViewById(R.id.tvChildItems);

		objChild = (SubCategoryModel) getChild(groupPosition, childPosition);

		String str = objChild.getChildcatName();
		String cap = str.substring(0, 1).toUpperCase() + str.substring(1);

		tvfname.setText(cap);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {

		LayoutInflater infalInflater = (LayoutInflater) this._context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = infalInflater.inflate(R.layout.layout_parent_view, null);

		TextView tvUser = (TextView) convertView.findViewById(R.id.tvParentName);
		ImageView imgParentIcon = (ImageView) convertView.findViewById(R.id.imgParentIcon);

		plusminus = (ImageView) convertView.findViewById(R.id.plusminus);
		SubCategoryModel subCategoryModelParent=_listDataHeader.get(groupPosition);


		String str = subCategoryModelParent.getName();
		String cap = str.substring(0, 1).toUpperCase() + str.substring(1);

		tvUser.setText(cap);

		Glide.with(_context)
				.load(subCategoryModelParent.getIcon())
				.asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.ic_default_product_one)
				.into(imgParentIcon);

		// If no child item, then Icon will be not shown
		if (getChildrenCount(groupPosition)==0)
		{
			plusminus.setVisibility(View.GONE);
		}
		else
		{
			// If Group item Expandeded than."Minus Icon"
			if(isExpanded)
			{
				plusminus.setVisibility(View.VISIBLE);
				plusminus.setImageResource(R.drawable.ic_cat_minus);
			}
			// If Group item Collepsed than."Plus Icon"
			else {
				plusminus.setVisibility(View.VISIBLE);
				plusminus.setImageResource(R.drawable.ic_cat_plus);
			}

		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	//collapse the old expanded group, if not the same
	//as new group to expand
	@Override
	public void onGroupExpanded(int groupPosition){

		if(groupPosition != lastExpandedGroupPosition){
			CategoryActivity.expListView.collapseGroup(lastExpandedGroupPosition);
		//	expListView
		}

		super.onGroupExpanded(groupPosition);
		lastExpandedGroupPosition = groupPosition;
	}




}
