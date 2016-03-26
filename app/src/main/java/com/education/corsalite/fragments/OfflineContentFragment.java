package com.education.corsalite.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ContentReadingActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.activities.OfflineContentActivity;
import com.education.corsalite.activities.VideoActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.holders.IconTreeItemHolder;
import com.education.corsalite.models.ExerciseOfflineModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.FileUtilities;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 28/11/15.
 */
public class OfflineContentFragment extends BaseFragment implements OfflineContentActivity.IOfflineEventListener {

    @Bind(R.id.main_node_content)
    RelativeLayout mainNodeLayout;
    @Bind(R.id.no_content_txt)
    View emptyContentView;

    private AndroidTreeView tView;
    private List<OfflineContent> offlineContentList;
    private List<ExerciseOfflineModel> offlineExercises;
    private String selectedCourse;
    private String subjectId = "";
    private String chapterId = "";
    private String topicId = "";
    private ArrayList<String> contentIds;
    private Course course;
    private String subjectName;
    private String chapterName;
    private String topicName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_content, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() instanceof OfflineContentActivity) {
            ((OfflineContentActivity) getActivity()).setOfflineListener(this);
        }
        return view;
    }

    private void getExercises(final Course course) {
        showProgress();
        emptyContentView.setVisibility(View.GONE);
        DbManager.getInstance(getActivity().getApplicationContext()).getOfflineExerciseModels(course.courseId + "",
                new ApiCallback<List<ExerciseOfflineModel>>(getActivity()) {
                    @Override
                    public void success(List<ExerciseOfflineModel> exerciseOfflineModels, Response response) {
                        super.success(exerciseOfflineModels, response);
                        offlineExercises = exerciseOfflineModels;
                        closeProgress();
                        getContentIndexResponse(course);
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        offlineExercises = null;
                        getContentIndexResponse(course);
                    }
                });
    }

    private void getContentIndexResponse(final Course course) {
        showProgress();
        emptyContentView.setVisibility(View.GONE);
        DbManager.getInstance(getActivity().getApplicationContext()).getOfflineContentList(new ApiCallback<List<OfflineContent>>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
            }

            @Override
            public void success(List<OfflineContent> offlineContentsList, Response response) {
                closeProgress();
                ArrayList<OfflineContent> offlineContents = new ArrayList<>(offlineContentsList);
                String savedData = AppPref.getInstance(getActivity()).getValue("DATA_IN_PROGRESS");
                ArrayList<OfflineContent> contents = new Gson().fromJson(savedData, new TypeToken<ArrayList<OfflineContent>>() {
                }.getType());
                if (contents != null) {
                    offlineContents.addAll(contents);
                    getTopicIds(contents);
                }
                offlineContentList = new ArrayList<>();
                for (OfflineContent offlineContent : offlineContents) {
                    if (offlineContent.courseId.equalsIgnoreCase(course.courseId.toString())) {
                        offlineContentList.add(offlineContent);
                    }
                }
                if (offlineContentList != null && !offlineContentList.isEmpty()) {
                    initNodes();
                } else {
                    mainNodeLayout.removeAllViews();
                    emptyContentView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getTopicIds(ArrayList<OfflineContent> contents) {
        contentIds = new ArrayList<>();
        for (OfflineContent offlineContent : contents) {
            contentIds.add(offlineContent.contentId);
        }
    }

    /*private void updateContentIndexResponses(String courseId) {
        Type contentIndexType = new TypeToken<List<ContentIndex>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonObject = gson.toJson(contentIndexList, contentIndexType);
        DbManager.getInstance(getActivity()).saveContentIndexList(jsonObject, courseId, LoginUserCache.getInstance().loginResponse.studentId);
    }*/

    private void updateContentIndexResponses(String contentId) {
        if (contentIds != null) {
            contentIds.remove(contentId);
            initNodes();
        }
    }

    @Override
    public void onCourseIdSelected(Course course) {
        getExercises(course);
        this.selectedCourse = course.courseId.toString();
        this.course = course;
    }

    @Override
    public void onUpdateOfflineData(String contentId) {
        updateContentIndexResponses(contentId);
    }

    private void initNodes() {
        TreeNode root = TreeNode.root();
        for (OfflineContent offlineContent : offlineContentList) {
            TreeNode subjectRoot = null;
            for (TreeNode subjectNode : root.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem) subjectNode.getValue()).id.equalsIgnoreCase(offlineContent.subjectId)) {
                    subjectRoot = subjectNode;
                    break;
                }

            }
            if (subjectRoot == null) {
                subjectRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_subject_white, offlineContent.subjectName, offlineContent.subjectId, "subject", false));
                root.addChild(subjectRoot);
            }

            TreeNode chapterRoot = null;
            for (TreeNode chapterNode : subjectRoot.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem) chapterNode.getValue()).id.equalsIgnoreCase(offlineContent.chapterId)) {
                    chapterRoot = chapterNode;
                    break;
                }
            }
            if (chapterRoot == null) {
                boolean showProgress = contentIds != null && contentIds.contains(offlineContent.contentId);
                chapterRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_chapter, offlineContent.chapterName, offlineContent.chapterId, "chapter", showProgress));
                subjectRoot.addChild(chapterRoot);
            }

            TreeNode topicRoot = null;
            boolean showProgress;
            for (TreeNode topicNode : chapterRoot.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem) topicNode.getValue()).id.equalsIgnoreCase(offlineContent.topicId)) {
                    topicRoot = topicNode;
                    break;
                }
            }
            if (topicRoot == null) {
                showProgress = contentIds != null && contentIds.contains(offlineContent.contentId);
                topicRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_chapter, offlineContent.topicName, offlineContent.topicId, "topic", showProgress));
                chapterRoot.addChild(topicRoot);

                // Add exercise as the first item in topic
                List<ExerciseOfflineModel> addedList = new ArrayList<>();
                for (ExerciseOfflineModel exercise : offlineExercises) {
                    if (exercise.topicId.equals(offlineContent.topicId)) {
                        IconTreeItemHolder.IconTreeItem item = new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_topics, "Exercise - " + exercise.topicId, exercise.topicId + "-" + exercise.courseId, "Exercise", false);
                        item.setData(exercise);
                        addedList.add(exercise);
                        topicRoot.addChild(new TreeNode(item));
                    }
                }
                offlineExercises.removeAll(addedList);
            }

            TreeNode contentRoot = null;
            for (TreeNode contentNode : topicRoot.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem) contentNode.getValue()).id.equalsIgnoreCase(offlineContent.contentId)) {
                    contentRoot = contentNode;
                    break;
                }
            }
            if (contentRoot == null) {
                showProgress = contentIds != null && contentIds.contains(offlineContent.contentId);
                contentRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_topics, offlineContent.fileName, offlineContent.contentId, "content", showProgress));
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

            if(item.tag.equalsIgnoreCase("subject")){
                subjectId = item.id;
                subjectName = item.text;
            } else if(item.tag.equalsIgnoreCase("chapter")){
                chapterId = item.id;
                chapterName = item.text;
            } else if(item.tag.equalsIgnoreCase("topic")){
                topicId = item.id;
                topicName = item.text;
            } else if (item.tag.equalsIgnoreCase("content")) {
                if (item.text.endsWith("html")) {
                    startContentActivity(topicId, chapterId, subjectId, item.id, item.text);
                } else {
                    startVideoActivity(course.name, subjectName, chapterName, topicName, item.text);
                }
            } else if(item.data != null && item.data instanceof ExerciseOfflineModel){
                startExerciseTest((ExerciseOfflineModel) item.data);
            }
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            return true;
        }
    };

    @Override
    public void onDeleteOfflineData(String id, String tag) {
        String path = null;
        ArrayList<OfflineContent> removeList = new ArrayList<>();
        for (OfflineContent offlineContent : offlineContentList) {
            if (offlineContent.courseId.equalsIgnoreCase(selectedCourse)) {
                switch (tag) {
                    case "subject":
                        if (id.equalsIgnoreCase(offlineContent.subjectId)) {
                            path = offlineContent.courseName + "/" + offlineContent.subjectName;
                            removeList.add(offlineContent);
                        }
                        break;
                    case "chapter":
                        if (id.equalsIgnoreCase(offlineContent.chapterId)) {
                            path = offlineContent.courseName + "/" + offlineContent.subjectName + "/" + offlineContent.chapterName;
                            removeList.add(offlineContent);
                        }
                        break;
                    case "topic":
                        if (id.equalsIgnoreCase(offlineContent.topicId)) {
                            path = offlineContent.courseName + "/" + offlineContent.subjectName + "/" + offlineContent.chapterName + "/" + offlineContent.topicName;
                            removeList.add(offlineContent);
                        }
                        break;
                    case "content":
                        if (id.equalsIgnoreCase(offlineContent.contentId)) {
                            String pathPrefix = offlineContent.courseName + "/" + offlineContent.subjectName + "/" + offlineContent.chapterName + "/" + offlineContent.topicName;
                            if (offlineContent.fileName.split(Pattern.quote("."))[1].equalsIgnoreCase("video")) {
                                path = pathPrefix + "/" + "Video" + "/" + offlineContent.fileName;
                            } else {
                                path = pathPrefix + "/" + "Html" + "/" + offlineContent.fileName;
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
        DbManager.getInstance(getActivity().getApplicationContext()).deleteOfflineContent(removeList);

        //Update treeview
        for (OfflineContent offlineContent : removeList) {
            offlineContentList.remove(offlineContent);
        }
        mainNodeLayout.removeAllViews();
        if (offlineContentList.isEmpty()) {
            Toast.makeText(getActivity(), "No offline content available ", Toast.LENGTH_SHORT).show();
        } else {
            initNodes();
        }
    }


    private void startExerciseTest(ExerciseOfflineModel model) {
        AbstractBaseActivity.setSharedExamModels(model.questions);
        Intent intent = new Intent(getActivity(), ExamEngineActivity.class);
        intent.putExtra(Constants.TEST_TITLE, "Exercises");
        intent.putExtra(Constants.SELECTED_POSITION, 0);
        startActivity(intent);
    }

    private void startContentActivity(String topicId, String chapterId, String subjectId, String contentId, String contentName) {
        Intent intent = new Intent(getActivity(), ContentReadingActivity.class);
        intent.putExtra("courseId", AbstractBaseActivity.selectedCourse.courseId.toString());
        intent.putExtra("subjectId", subjectId);
        intent.putExtra("chapterId", chapterId);
        intent.putExtra("topicId", topicId);
        intent.putExtra("contentId", contentId);
        intent.putExtra("contentName", contentName);
        getActivity().startActivity(intent);
    }

    private void startVideoActivity(String name, String mSubjectName, String mChapterName, String topicName, String contentName) {
        String folderStructure = name + File.separator + mSubjectName + File.separator + mChapterName + File.separator + topicName;
        File SDCardRoot = Environment.getExternalStorageDirectory();
        File outDir = new File(SDCardRoot.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER + File.separator + folderStructure);
        File file = new File(outDir.getAbsolutePath() + File.separator + Constants.VIDEO_FOLDER);
        File newFile = new File(file, contentName);
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra("videopath", newFile.getAbsolutePath());
        L.debug("Loading file from : "+newFile.getAbsolutePath());
        if (newFile.exists()) {
            getActivity().startActivity(intent);
        } else
            showToast("File does not exist");
    }

}
