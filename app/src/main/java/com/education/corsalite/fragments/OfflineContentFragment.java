package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.OfflineContentActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.holders.IconTreeItemHolder;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.utils.FileUtilities;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 28/11/15.
 */
public class OfflineContentFragment extends BaseFragment  implements OfflineContentActivity.IOfflineEventListener{

    @Bind(R.id.main_node_content) RelativeLayout mainNodeLayout;
    @Bind(R.id.headerProgress)ProgressBar mProgressBar;
    private AndroidTreeView tView;
    List<OfflineContent> offlineContentList ;
    String selectedCourse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_content, container, false);
        ButterKnife.bind(this, view);
        if(getActivity() instanceof OfflineContentActivity) {
            ((OfflineContentActivity) getActivity()).setOfflineListener(this);
        }
        return view;
    }

    private void getContentIndexResponse(final Course course) {
        DbManager.getInstance(getActivity()).getOfflineContentList(new ApiCallback<List<OfflineContent>>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
            }

            @Override
            public void success(List<OfflineContent> offlineContents, Response response) {
                offlineContentList = new ArrayList<>();
                for(OfflineContent offlineContent: offlineContents) {
                    if(offlineContent.courseId.equalsIgnoreCase(course.courseId.toString())) {
                        offlineContentList.add(offlineContent);
                    }
                }
                if(offlineContentList != null && offlineContentList.size() >0){
                    initNodes();
                }else{
                    mProgressBar.setVisibility(View.GONE);
                    mainNodeLayout.removeAllViews();
                    Toast.makeText(getActivity(), "No offline content available " + course.name, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*private void updateContentIndexResponses(String courseId) {
        Type contentIndexType = new TypeToken<List<ContentIndex>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonObject = gson.toJson(contentIndexList, contentIndexType);
        DbManager.getInstance(getActivity()).saveContentIndexList(jsonObject, courseId, LoginUserCache.getInstance().loginResponse.studentId);
    }*/

    @Override
    public void onCourseIdSelected(Course course) {
        getContentIndexResponse(course);
        this.selectedCourse = course.courseId.toString();
    }

    @Override
    public void onUpdateOfflineData(String courseId) {
        //updateContentIndexResponses(courseId);
    }

    private void initNodes() {
        TreeNode root = TreeNode.root();
        for (OfflineContent offlineContent: offlineContentList) {
            TreeNode subjectRoot =null;
            for(TreeNode subjectNode:root.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem) subjectNode.getValue()).id.equalsIgnoreCase(offlineContent.subjectId)) {
                    subjectRoot = subjectNode ;
                    break;
                }

            }
            if(subjectRoot == null){
                subjectRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_subject_white, offlineContent.subjectName,offlineContent.subjectId,"subject"));
                root.addChild(subjectRoot);
            }

            TreeNode chapterRoot =null;
            for(TreeNode chapterNode:subjectRoot.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem)chapterNode.getValue()).id.equalsIgnoreCase(offlineContent.chapterId)) {
                    chapterRoot = chapterNode ;
                    break;
                }
            }
            if(chapterRoot == null){
                chapterRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_chapter,  offlineContent.chapterName,offlineContent.chapterId,"chapter"));
                subjectRoot.addChild(chapterRoot);
            }

            TreeNode topicRoot =null;
            for(TreeNode topicNode:chapterRoot.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem)topicNode.getValue()).id.equalsIgnoreCase(offlineContent.topicId)) {
                    topicRoot = topicNode ;
                    break;
                }
            }
            if(topicRoot == null){
                topicRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_chapter, offlineContent.topicName,offlineContent.topicId,"topic"));
                chapterRoot.addChild(topicRoot);
            }

            TreeNode contentRoot =null;
            for(TreeNode contentNode:topicRoot.getChildren()) {
                if(((IconTreeItemHolder.IconTreeItem)contentNode.getValue()).id.equalsIgnoreCase(offlineContent.contentId)) {
                    contentRoot = contentNode ;
                    break;
                }
            }
            if(contentRoot == null){
                contentRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_topics,offlineContent.fileName, offlineContent.contentId,"content"));
                topicRoot.addChild(contentRoot);
            }
        }

        tView = new AndroidTreeView(getActivity(), root);
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
            return true;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDeleteOfflineData(String id,String tag){
        String path = null;
        ArrayList<OfflineContent> removeList = new ArrayList<>();
        for(OfflineContent offlineContent : offlineContentList){
            if(offlineContent.courseId.equalsIgnoreCase(selectedCourse)) {
                switch (tag) {
                    case "subject":
                        if (id.equalsIgnoreCase(offlineContent.subjectId)) {
                            path = offlineContent.courseName+"/"+offlineContent.subjectName;
                            removeList.add(offlineContent);
                        }
                        break;
                    case "chapter":
                        if (id.equalsIgnoreCase(offlineContent.chapterId)) {
                            path = offlineContent.courseName+"/"+offlineContent.subjectName+"/"+offlineContent.chapterName;
                            removeList.add(offlineContent);
                        }
                        break;
                    case "topic":
                        if (id.equalsIgnoreCase(offlineContent.topicId)) {
                            path = offlineContent.courseName+"/"+offlineContent.subjectName+"/"+offlineContent.chapterName+"/"+offlineContent.topicName;
                            removeList.add(offlineContent);

                        }
                        break;
                    case "content":
                        if (id.equalsIgnoreCase(offlineContent.contentId)) {
                            String pathPrefix = offlineContent.courseName + "/" + offlineContent.subjectName + "/" + offlineContent.chapterName + "/" + offlineContent.topicName ;
                            if(offlineContent.fileName.split(Pattern.quote("."))[1].equalsIgnoreCase("video")) {
                                path = pathPrefix + "/"+ "Video" + "/"+ offlineContent.fileName;
                            }else{
                                path = pathPrefix + "/" + "Html" + "/"+ offlineContent.fileName;
                            }
                            removeList.add(offlineContent);
                        }
                        break;
                }
            }
        }
        //Delete file
        new FileUtilities(getActivity()).delete(path);

        //Update database
        DbManager.getInstance(getActivity()).deleteOfflineContent(removeList);

    }







}
