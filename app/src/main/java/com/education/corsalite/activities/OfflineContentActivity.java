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
import com.education.corsalite.models.responsemodels.Course;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ayush on 05/10/15.
 */
public class OfflineContentActivity extends AbstractBaseActivity {

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
        setToolbarForOfflineContent();
    }

    private boolean getContentIndexResponse(String courseId) {
        ContentIndexResponse contentIndexResponse = DbManager.getInstance(OfflineContentActivity.this).getContentIndexList(courseId,
                LoginUserCache.getInstance().loginResponse.studentId);
        if(contentIndexResponse == null) {
            return false;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if(contentIndexList != null) {
            contentIndexList.clear();
        }
        contentIndexList = gson.fromJson(contentIndexResponse.contentIndexesJson, new TypeToken<List<ContentIndex>>(){}.getType());
        return true;
    }

    private void updateContentIndexResponses() {
        Type contentIndexType = new TypeToken<List<ContentIndex>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonObject = gson.toJson(contentIndexList, contentIndexType);
        DbManager.getInstance(OfflineContentActivity.this).saveContentIndexList(jsonObject, selectedCourse.courseId.toString(), LoginUserCache.getInstance().loginResponse.studentId);
    }

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        if(getContentIndexResponse(course.courseId.toString())){
            initNodes();
        }else{
            Toast.makeText(OfflineContentActivity.this, "No offline content available for "+course.name , Toast.LENGTH_SHORT).show();
        }
    }


    private void initNodes() {
        TreeNode root = TreeNode.root();
        for (ContentIndex contentResponse: contentIndexList) {
            //TreeNode contentResponseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_subject, contentResponse.courseName,contentResponse.idCourse));
            //root.addChild(contentResponseRoot);
            for (SubjectModel subject : contentResponse.subjectModelList) {
                TreeNode subjectRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_subject, subject.subjectName));
                root.addChild(subjectRoot);
                for (ChapterModel chapter : subject.chapters) {
                    TreeNode chapterRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_chapter, chapter.chapterName));
                    subjectRoot.addChild(chapterRoot);
                    for (TopicModel topic : chapter.topicMap) {
                        TreeNode topicRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_chapter, topic.topicName));
                        chapterRoot.addChild(topicRoot);
                        for (ContentModel content : topic.contentMap) {
                            TreeNode contentRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_topics,content.contentName +"."+ content.type));
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
        mainNodeLayout.removeAllViews();
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
    }

    public void onDelete(String id){
        for(ContentIndex contentIndex:contentIndexList){
            if(contentIndex.idCourse.equalsIgnoreCase(id)){
                contentIndexList.remove(contentIndex);
                return;
            }
            for(SubjectModel subject : contentIndex.subjectModelList){
                if(subject.idSubject.equalsIgnoreCase(id)){
                    contentIndex.subjectModelList.remove(subject);
                    return;
                }
                for (ChapterModel chapter : subject.chapters) {
                    if(chapter.idChapter.equalsIgnoreCase(id)){
                        subject.chapters.remove(chapter);
                        return;
                    }
                    for(TopicModel topic : chapter.topicMap){
                        if(topic.idTopic.equalsIgnoreCase(id)){
                            chapter.topicMap.remove(topic);
                            return;
                        }
                        for(ContentModel content : topic.contentMap){
                            if(content.idContent.equalsIgnoreCase(id)){
                                topic.contentMap.remove(content);
                                return;
                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        updateContentIndexResponses();
        super.onBackPressed();
    }
}
