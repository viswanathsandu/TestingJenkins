package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.holders.IconTreeItemHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

/**
 * Created by ayush on 05/10/15.
 */
public class OfflineContentActivity extends AbstractBaseActivity {

    private int counter = 0;
    private LinearLayout mainNodeLayout;
    private AndroidTreeView tView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_offline_content, null);
        frameLayout.addView(myView);
        mainNodeLayout = (LinearLayout) findViewById(R.id.main_node);

        TreeNode root = TreeNode.root();

        TreeNode contentRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Content"));

        TreeNode physics = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Physics"));
        TreeNode lesson_one_physics = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Lesson one"));


        TreeNode physics_file1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Content.Html"));
        TreeNode physics_file2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Video.mp4"));
        TreeNode physics_file3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Video2.mp4"));



        TreeNode chemistry = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Chemistry"));
        TreeNode lesson_one_chemistry = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Lesson one"));


        TreeNode chemistry_file1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Content.Html"));
        TreeNode chemistry_file2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Video.mp4"));
        TreeNode chemistry_file3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Video2.mp4"));


        TreeNode maths = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Chemistry"));
        TreeNode lesson_one_maths = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Lesson one"));


        TreeNode maths_file1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Content.Html"));
        TreeNode maths_file2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Video.mp4"));
        TreeNode maths_file3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Video2.mp4"));


        lesson_one_physics.addChildren(physics_file1, physics_file2, physics_file3);
        lesson_one_chemistry.addChildren(chemistry_file1, chemistry_file2, chemistry_file3);
        lesson_one_maths.addChildren(maths_file1, maths_file2, maths_file3);
        physics.addChild(lesson_one_physics);
        chemistry.addChild(lesson_one_chemistry);
        maths.addChild(lesson_one_maths);
        contentRoot.addChildren(physics, chemistry, maths);
        root.addChildren(contentRoot);

        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        mainNodeLayout.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            Toast.makeText(OfflineContentActivity.this, "Long click: " + item.text, Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("tState", tView.getSaveState());
    }
}
