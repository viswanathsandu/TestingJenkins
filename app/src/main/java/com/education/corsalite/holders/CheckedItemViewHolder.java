package com.education.corsalite.holders;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
public class CheckedItemViewHolder extends TreeNode.BaseNodeViewHolder<String> {
    private TextView tvValue;
    private ImageView checkboxImage;
    private CheckBox nodeSelector;
    private boolean isFile;
    private RelativeLayout nodeLayout;

    public CheckedItemViewHolder(Context context, boolean isFile) {
        super(context);
        this.isFile = isFile;
    }

    @Override
    public View createNodeView(final TreeNode node, String value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.profile_holder, null, false);

        tvValue = (TextView) view.findViewById(R.id.node_value);
        checkboxImage = (ImageView) view.findViewById(R.id.node_selectors);
        nodeLayout = (RelativeLayout) view.findViewById(R.id.node_selectorLayout);
        tvValue.setText(value);

        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
                for (TreeNode n : node.getChildren()) {
                    getTreeView().selectNode(n, isChecked);
                }
            }
        });
        nodeSelector.setChecked(node.isSelected());
        if(!isFile){
            checkboxImage.setImageDrawable(view.getResources().getDrawable(R.drawable.ico_offline_chapter));
            nodeLayout.setBackgroundColor(view.getResources().getColor(R.color.gray));
        }
        if (node.isLastChild()) {
            view.findViewById(R.id.bot_line).setVisibility(View.INVISIBLE);
        }

        return view;
    }


    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }
}
