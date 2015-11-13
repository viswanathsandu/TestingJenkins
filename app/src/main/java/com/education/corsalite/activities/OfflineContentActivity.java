package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.holders.IconTreeItemHolder;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.TopicModel;
import com.education.corsalite.models.db.ContentIndexResponse;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

/**
 * Created by ayush on 05/10/15.
 */
public class OfflineContentActivity extends AbstractBaseActivity {

    private int counter = 0;
    private LinearLayout mainNodeLayout;
    private AndroidTreeView tView;
    List<ContentIndex> contentIndexList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_offline_content, null);
        frameLayout.addView(myView);
        mainNodeLayout = (LinearLayout) findViewById(R.id.main_node);
        setToolbarTitle("Offline Content");

        if(getContentIndexResponse()) {
            initNodes();
        }else{
            Toast.makeText(OfflineContentActivity.this, "No offline content available" , Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getContentIndexResponse() {
        ContentIndexResponse contentIndexResponse = DbManager.getInstance(OfflineContentActivity.this).getContentIndexList(selectedCourse.courseId.toString(),
                LoginUserCache.getInstance().loginResponse.studentId);
        if(contentIndexResponse == null) {
            return false;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        contentIndexList = gson.fromJson(contentIndexResponse.contentIndexesJson, new TypeToken<List<ContentIndex>>(){}.getType());
        return true;
    }

    private void initNodes() {
        TreeNode root = TreeNode.root();
        for (ContentIndex contentResponse: contentIndexList) {
            TreeNode contentResponseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, contentResponse.courseName));
            root.addChild(contentResponseRoot);
            for (SubjectModel subject : contentResponse.subjectModelList) {
                TreeNode subjectRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, subject.subjectName));
                contentResponseRoot.addChild(subjectRoot);
                for (ChapterModel chapter : subject.chapters) {
                    TreeNode chapterRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, chapter.chapterName));
                    subjectRoot.addChild(chapterRoot);
                    for (TopicModel topic : chapter.topicMap) {
                        TreeNode topicRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, topic.topicName));
                        chapterRoot.addChild(topicRoot);
                        for (ContentModel content : topic.contentMap) {
                            TreeNode contentRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file,content.contentName +"."+ content.type));
                            topicRoot.addChild(contentRoot);
                        }
                    }
                }
            }
        }

        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        mainNodeLayout.addView(tView.getView());
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
