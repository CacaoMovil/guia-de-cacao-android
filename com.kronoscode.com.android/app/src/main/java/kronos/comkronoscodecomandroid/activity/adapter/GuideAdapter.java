package kronos.comkronoscodecomandroid.activity.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.object.VersionObject;

public class GuideAdapter extends BaseExpandableListAdapter implements Filterable {

	private Map<String, List<VersionObject>> mChildrenList, mChildrenListFilter;
    private final LayoutInflater mInflater;
	private Context context;

    public GuideAdapter(Context context,
                        Map<String, List<VersionObject>> children) {
		this.context = context;
		this.mChildrenList = children;
        this.mChildrenListFilter = children;
        mInflater = LayoutInflater.from(context);
    }

	public boolean areAllItemsEnabled() {
		return true;
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

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

        ChildrenHolder holder;

        if (convertView==null) {
            convertView = mInflater.inflate(R.layout.group_item,null);
            holder = new ChildrenHolder();
            holder.mName = (TextView)convertView.findViewById(R.id.name);
            holder.mDate = (TextView)convertView.findViewById(R.id.date);
            holder.mVersion = (TextView)convertView.findViewById(R.id.version);

            convertView.setTag(holder);
        } else holder = (ChildrenHolder)convertView.getTag();

        VersionObject version = (VersionObject) getChild(groupPosition, childPosition);

        holder.mName.setText((version.getmName()));
        holder.mDate.setText((version.getmDate()));
        holder.mVersion.setText((version.getmNumVersion()));

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
            convertView.setTag(holder);
        } else holder = (GroupHolder)convertView.getTag();

        convertView.setFocusable(false);
        convertView.setSelected(false);

        holder.group.setText(Html.fromHtml(group));

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
        return null;
    }

    /*@Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                try {
                    if (results.count==children2.size()) {
                        children= children2;
                        notifyDataSetChanged();
                    } else if(results.count>0) {
                        children =  (Map<String, List<Enlace>>) results.values;
                        notifyDataSetChanged();
                    } else {
                        children= children2;
                        EnlaceAdapter.this.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    children =  children2;
                    results.count = children2.size();

                } else {
                    Map<String, List<Enlace>> filteredRowItems =  new LinkedHashMap<String, List<Enlace>>();

                    for (String key : children.keySet()){
                        ArrayList<Enlace> newValues = new ArrayList<Enlace>();
                        for (Iterator<Enlace> iterator = children.get(key).iterator(); iterator
                                .hasNext();) {
                            Enlace item = iterator.next();

                             String violationName = item.getTitle();
                            if (violationName.toUpperCase().contains(constraint.toString().toUpperCase())
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
    }*/

    final static class GroupHolder {
        TextView group;
    }

    final static class ChildrenHolder {
        TextView mName;
        TextView mDate;
        TextView mVersion;
    }
}
