package kronos.comkronoscodecomandroid.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.kronoscode.cacao.android.app.model.GuideVersion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.utils.GuideUtils;

public class GuideAdapter extends BaseExpandableListAdapter implements Filterable {

	private Map<String, List<GuideVersion>> mChildrenList, mChildrenListFilter;
    private final LayoutInflater mInflater;
    private ListView mGuideList;
    private TextView mEmpty;

    public GuideAdapter(Context context, Map<String, List<GuideVersion>> children, ListView guidelist, TextView empty) {
		this.mChildrenList = children;
        this.mChildrenListFilter = children;
        this.mGuideList = guidelist;
        this.mEmpty = empty;
        mInflater = LayoutInflater.from(context);
    }

	public boolean areAllItemsEnabled() {
		return false;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return mChildrenList.get(getGroup(groupPosition)).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		return mChildrenList.get(getGroup(groupPosition)).size();
	}

	public String getGroup(int groupPosition) {
		Iterator<String> keySet = mChildrenList.keySet().iterator();
		int index = 0;
		String group;
		do {
			group = keySet.next();
		} while (keySet.hasNext() && groupPosition != index++);
		return group;
	}

	public int getGroupCount() {
		return mChildrenList.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return convertView;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

        String group = getGroup(groupPosition);

        GroupHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.expandable_list_group, parent, false);
            holder = new GroupHolder();
            holder.group = (TextView)convertView.findViewById(R.id.name);
            holder.guideNumber = (TextView)convertView.findViewById(R.id.guide_number);
            convertView.setTag(holder);
        } else holder = (GroupHolder)convertView.getTag();

        convertView.setFocusable(false);
        convertView.setSelected(false);

        holder.group.setText(group);
        String numberDisplay;
        if (groupPosition >= 9){
            numberDisplay = String.valueOf(groupPosition+1);
        } else{
            numberDisplay = "0" + String.valueOf(groupPosition+1);
        }
        holder.guideNumber.setText(numberDisplay);
        convertView.setPadding(0, 20, 0, 20);
        return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                try {

                    mGuideList.setVisibility(View.VISIBLE);
                    mEmpty.setVisibility(View.GONE);

                    if (results.count == mChildrenListFilter.size()) {
                        mChildrenList = mChildrenListFilter;
                        notifyDataSetChanged();
                    } else if(results.count > 0) {
                        mChildrenList = (Map<String, List<GuideVersion>>) results.values;
                        notifyDataSetChanged();
                    } else {
                        mGuideList.setVisibility(View.GONE);
                        mEmpty.setVisibility(View.VISIBLE);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    mChildrenList =  mChildrenListFilter;
                    results.count = mChildrenListFilter.size();

                } else {
                    Map<String, List<GuideVersion>> filteredRowItems =  new LinkedHashMap<String, List<GuideVersion>>();

                    for (String key : mChildrenList.keySet()) {
                        ArrayList<GuideVersion> newValues = new ArrayList<>();
                        for (Iterator<GuideVersion> iterator = mChildrenList.get(key).iterator(); iterator
                                .hasNext();) {
                            GuideVersion item = iterator.next();

                            String violationName = item.getName();
                            if (violationName.toUpperCase().contains(constraint.toString().toUpperCase())
                                    && !newValues.contains(item)) {

                                newValues.add(item);
                            }

                            String tags = item.getTags();
                            if (tags.toUpperCase().contains(constraint.toString().toUpperCase())
                                    && !newValues.contains(item)) {

                                newValues.add(item);
                            }
                        }
                        if (!newValues.isEmpty() && !filteredRowItems.containsKey(key)) {
                            filteredRowItems.put(key, newValues);
                        }
                    }
                    results.values = filteredRowItems;
                    results.count = filteredRowItems.size();
                }
                return results;
            }
        };
    }

    final static class GroupHolder {
        TextView group;
        TextView guideNumber;
    }
}
