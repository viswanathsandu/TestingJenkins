package com.education.corsalite.holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.corsalite.R;
import com.unnamed.b.atv.model.TreeNode;

public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private ImageView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        iconView.setImageDrawable(context.getResources().getDrawable(value.icon));

        arrowView = (ImageView) view.findViewById(R.id.arrow_icon);

        view.findViewById(R.id.btn_addFolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TreeNode newFolder = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.completed_topics, "New Folder"));
                //getTreeView().addNode(node, newFolder);
            }
        });

        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTreeView().removeNode(node);
                //TODO Update Database
            }
        });

        //Subject
        if (node.getLevel() == 1) {
            view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
            view.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setImageDrawable(context.getResources().getDrawable(active ? R.drawable.ico_offline_arrow_down_black : R.drawable.ico_offline_arrow_black));

    }

    public static class IconTreeItem {
        public int icon;
        public String text;

        public IconTreeItem(int icon, String text) {
            this.icon = icon;
            this.text = text;
        }
    }
}
