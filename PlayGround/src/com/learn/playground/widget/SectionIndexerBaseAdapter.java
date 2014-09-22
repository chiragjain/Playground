package com.learn.playground.widget;

import java.util.ArrayList;

import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.learn.playground.widget.PinnedHeaderListView.PinnedSectionedHeaderAdapter;

public abstract class SectionIndexerBaseAdapter extends BaseAdapter implements
		PinnedSectionedHeaderAdapter, SectionIndexer {

	private static int HEADER_VIEW_TYPE = 0;
	private static int ITEM_VIEW_TYPE = 0;

	private ArrayList<Integer> mHeaderPosition;
	private SparseIntArray mHeaderEmptyPosition;

	private SectionIndexer mIndexer;

	public SectionIndexerBaseAdapter() {
		mHeaderPosition = new ArrayList<Integer>();
		mHeaderEmptyPosition = new SparseIntArray();
	}

	@Override
	public void notifyDataSetChanged() {
		fillHeaderPosition();
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		fillHeaderPosition();
		super.notifyDataSetInvalidated();
	}

	public void setIndexer(SectionIndexer indexer) {
		mIndexer = indexer;
		fillHeaderPosition();
	}

	@Override
	public final Object getItem(int position) {
		if (isSectionHeader(position))
			return getSections()[getSectionHeaderForPosition(position)];
		else
			return getDataItem(getVirtualPositionOfDataItem(position));
	}

	@Override
	public final long getItemId(int position) {
		if (isSectionHeader(position))
			return getSectionHeaderForPosition(position);

		return getDataItemId(getVirtualPositionOfDataItem(position));
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		if (isSectionHeader(position))
			return getSectionHeaderView(getSectionHeaderForPosition(position),
					convertView, parent);
		return getItemView(getSectionHeaderForPosition(position),
				getVirtualPositionOfDataItem(position), convertView, parent);
	}

	@Override
	public final int getItemViewType(int position) {
		if (isSectionHeader(position)) {
			return getItemViewTypeCount()
					+ getSectionHeaderViewType(getSectionHeaderForPosition(position));
		}
		return getItemViewType(getVirtualPositionOfDataItem(position));
	}

	@Override
	public final int getViewTypeCount() {
		return getItemViewTypeCount() + getSectionHeaderViewTypeCount();
	}

	@Override
	public final boolean isSectionHeader(int position) {
		return mHeaderPosition.contains(position);
	}

	@Override
	public final int getSectionHeaderForPosition(int position) {
		return getNumberofHeaderAbovePosition(position) - 1;
	}

	@Override
	public int getCount() {
		return getSections().length + getDataCount();
	}

	@Override
	public final int getPositionForSection(int section) {
		return mHeaderPosition.get(section);
	}

	@Override
	public Object[] getSections() {
		Object[] tempArray = new Object[mHeaderPosition.size()];
		int nonEmptyItem = 0;
		for (int i = 0; i < mIndexer.getSections().length; i++) {
			if (mHeaderEmptyPosition.indexOfKey(i) < 0) {
				tempArray[nonEmptyItem] = mIndexer.getSections()[i];
				nonEmptyItem++;
			}
		}
		return tempArray;
	}

	@Override
	public final int getSectionForPosition(int position) {
		return getNumberofHeaderAbovePosition(position) - 1;
	}

	public int getItemViewType(int section, int position) {
		return ITEM_VIEW_TYPE;
	}

	public int getItemViewTypeCount() {
		return 1;
	}

	public int getSectionHeaderViewType(int section) {
		return HEADER_VIEW_TYPE;
	}

	public int getSectionHeaderViewTypeCount() {
		return 1;
	}

	public abstract Object getDataItem(int position);

	public abstract long getDataItemId(int position);

	public abstract int getDataCount();

	public abstract View getItemView(int section, int position,
			View convertView, ViewGroup parent);

	public abstract View getSectionHeaderView(int section, View convertView,
			ViewGroup parent);

	public final void fillHeaderPosition() {

		mHeaderPosition.clear();
		mHeaderEmptyPosition.clear();

		for (int sectionPosition = 0; sectionPosition < mIndexer.getSections().length; sectionPosition++) {
			int headerPosition = mIndexer
					.getPositionForSection(sectionPosition);

			if (mHeaderPosition.size() > 0
					&& (mHeaderPosition.get(mHeaderPosition.size() - 1) == (headerPosition
							+ mHeaderPosition.size() - 1))) {
				mHeaderEmptyPosition.put(sectionPosition - 1, (headerPosition
						+ mHeaderPosition.size() - 1));
				mHeaderPosition.remove(mHeaderPosition.size() - 1);
			}

			if (headerPosition <= (getDataCount() - 1))
				mHeaderPosition.add(headerPosition + mHeaderPosition.size());
			else
				mHeaderEmptyPosition.put(sectionPosition, headerPosition
						+ mHeaderPosition.size());
		}

	}

	public final int getVirtualPositionOfDataItem(int position) {

		if (!isSectionHeader(position))
			return (position - getNumberofHeaderAbovePosition(position));

		return -1;
	}

	public final int getNumberofHeaderAbovePosition(int position) {
		int noOfHeaders = 0;

		while (noOfHeaders < mHeaderPosition.size()
				&& mHeaderPosition.get(noOfHeaders) <= position) {
			noOfHeaders++;
		}

		return noOfHeaders;
	}

}
