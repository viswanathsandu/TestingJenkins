package com.education.corsalite.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ContentReadingActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.activities.OfflineActivity;
import com.education.corsalite.activities.VideoActivity;
import com.education.corsalite.enums.OfflineContentStatus;
import com.education.corsalite.holders.IconTreeItemHolder;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.Data;
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.L;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 28/11/15.
 */
public class OfflineContentFragment extends BaseFragment implements OfflineActivity.IOfflineEventListener {

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
        if (getActivity() instanceof OfflineActivity) {
            ((OfflineActivity) getActivity()).setOfflineListener(this);
        }
        return view;
    }

    private void getExercises(final Course course) {
        emptyContentView.setVisibility(View.GONE);
        offlineExercises = dbManager.getOfflineExerciseModels(course.courseId + "");
        getContentIndexResponse(course);
    }

    private void getContentIndexResponse(final Course course) {
        emptyContentView.setVisibility(View.GONE);
        List<OfflineContent> offlineContents = dbManager.getOfflineContents(AbstractBaseActivity.getSelectedCourseId());
        offlineContentList = new ArrayList<>();
        for (OfflineContent offlineContent : offlineContents) {
            if (offlineContent.courseId.equalsIgnoreCase(course.courseId.toString())) {
                offlineContentList.add(offlineContent);
            }
        }
        offlineContentList = getSortedList(offlineContents);
        if (offlineContentList != null && !offlineContentList.isEmpty()) {
            initNodes();
        } else {
            mainNodeLayout.removeAllViews();
            emptyContentView.setVisibility(View.VISIBLE);
        }
    }

    private List<OfflineContent> getSortedList(List<OfflineContent> contents) {
        List<OfflineContent> exercises = new ArrayList<>();
        List<OfflineContent> htmlContents = new ArrayList<>();
        List<OfflineContent> videoContents = new ArrayList<>();
        for (OfflineContent content : contents) {
            if (content.fileName.toLowerCase().endsWith(".mpg")) {
                videoContents.add(content);
            } else if (content.fileName.toLowerCase().endsWith(".html")) {
                htmlContents.add(content);
            } else {
                exercises.add(content);
            }
        }
        Collections.sort(videoContents,
                new Comparator<OfflineContent>() {                                                                 //Class AnalyticsModel
                    public int compare(OfflineContent content1,
                                       OfflineContent content2) {
                        return content1.fileName.compareToIgnoreCase(content2.fileName);

                    }
                });
        contents.clear();
        contents.addAll(htmlContents);
        contents.addAll(videoContents);
        return contents;
    }

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
                subjectRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_subject_white, offlineContent.subjectName, offlineContent.subjectId, "subject", false, 0));
                root.addChild(subjectRoot);
            }

            TreeNode chapterRoot = null;
            for (TreeNode chapterNode : subjectRoot.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem) chapterNode.getValue()).id.equalsIgnoreCase(offlineContent.chapterId)) {
                    chapterRoot = chapterNode;
                    break;
                }
            }
            boolean showProgress = false;
            if (offlineContent.status != null &&
                    (offlineContent.status.equals(OfflineContentStatus.WAITING)
                            || offlineContent.status.equals(OfflineContentStatus.STARTED)
                            || offlineContent.status.equals(OfflineContentStatus.IN_PROGRESS)
                            || offlineContent.status.equals(OfflineContentStatus.FAILED))) {
                showProgress = true;
            }
            if (chapterRoot == null) {
                int icon = getCorrespondingBGColor(offlineContent);
                chapterRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(icon, offlineContent.chapterName, offlineContent.chapterId, "chapter", showProgress, 0));
                subjectRoot.addChild(chapterRoot);
            }

            TreeNode topicRoot = null;
            for (TreeNode topicNode : chapterRoot.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem) topicNode.getValue()).id.equalsIgnoreCase(offlineContent.topicId)) {
                    topicRoot = topicNode;
                    break;
                }
            }
            if (topicRoot == null) {
                int icon = getCorrespondingBGColor(offlineContent);
                topicRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(icon, offlineContent.topicName, offlineContent.topicId, "topic", showProgress, 0));
                chapterRoot.addChild(topicRoot);

                // Add exercise as the first item in topic
                if (offlineExercises != null) {
                    List<ExerciseOfflineModel> addedList = new ArrayList<>();
                    for (ExerciseOfflineModel exercise : offlineExercises) {
                        if (exercise.topicId.equals(offlineContent.topicId)) {
                            IconTreeItemHolder.IconTreeItem item = new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_exercise, "Exercise", exercise.topicId + "-" + exercise.courseId, "Exercise", false, 0);
                            item.setData(exercise);
                            addedList.add(exercise);
                            topicRoot.addChild(new TreeNode(item));
                        }
                    }
                    offlineExercises.removeAll(addedList);
                }
            }

            TreeNode contentRoot = null;
            for (TreeNode contentNode : topicRoot.getChildren()) {
                if (((IconTreeItemHolder.IconTreeItem) contentNode.getValue()).id.equalsIgnoreCase(offlineContent.contentId)) {
                    contentRoot = contentNode;
                    break;
                }
            }
            if (contentRoot == null) {
                if (offlineContent.fileName.endsWith(".mpg")) {
                    contentRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_video, offlineContent.fileName, offlineContent.contentId, "content", showProgress, offlineContent.progress));
                } else if (offlineContent.fileName.endsWith(".html")) {
                    contentRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ico_offline_topic, offlineContent.fileName, offlineContent.contentId, "content", showProgress, 0));
                }
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
            if (item.tag.equalsIgnoreCase("subject")) {
                subjectId = item.id;
                subjectName = item.text;
            } else if (item.tag.equalsIgnoreCase("chapter")) {
                chapterId = item.id;
                chapterName = item.text;
            } else if (item.tag.equalsIgnoreCase("topic")) {
                topicId = item.id;
                topicName = item.text;
            } else if (item.tag.equalsIgnoreCase("content")) {
                if (item.text.endsWith("html")) {
                    startContentActivity(topicId, chapterId, subjectId, item.id, item.text);
                } else {
                    startVideoActivity(item.id);
                }
            } else if (item.data != null && item.data instanceof ExerciseOfflineModel) {
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
        if (tag.equalsIgnoreCase("subject")) {
            List<OfflineContent> subjectContent = dbManager.getOfflineContentWithSubject(id);
            if (subjectContent != null && !subjectContent.isEmpty()) {
                removeList.addAll(subjectContent);
                OfflineContent offlineContent = subjectContent.get(0);
                path = offlineContent.courseName + "/" + offlineContent.subjectName;
            }
        } else if (tag.equalsIgnoreCase("chapter")) {
            List<OfflineContent> chapterContent = dbManager.getOfflineContentWithChapter(id);
            if (chapterContent != null && !chapterContent.isEmpty()) {
                removeList.addAll(chapterContent);
                OfflineContent offlineContent = chapterContent.get(0);
                path = offlineContent.courseName + "/" + offlineContent.subjectName + "/" + offlineContent.chapterName;
            }
        } else if (tag.equalsIgnoreCase("topic")) {
            OfflineContent offlineContent = dbManager.getOfflineContentWithTopic(id);
            if (offlineContent != null) {
                removeList.add(offlineContent);
                path = offlineContent.courseName + "/" + offlineContent.subjectName + "/" + offlineContent.chapterName + "/" + offlineContent.topicName;
            }
            removeList.add(offlineContent);
        } else if (tag.equalsIgnoreCase("content")) {
            OfflineContent offlineContent = dbManager.getOfflineContentWithContent(id);
            if (offlineContent != null) {
                removeList.add(offlineContent);
                String pathPrefix = offlineContent.courseName + "/" + offlineContent.subjectName + "/" + offlineContent.chapterName + "/" + offlineContent.topicName;
                if (offlineContent.fileName.split(Pattern.quote("."))[1].equalsIgnoreCase("video")) {
                    path = pathPrefix + "/" + "Video" + "/" + offlineContent.fileName;
                } else {
                    path = pathPrefix + "/" + "Html" + "/" + offlineContent.fileName;
                }
            }
            removeList.add(offlineContent);
        }
        //Delete file
        FileUtils.get(getActivity()).delete(path);
        //Update database
        dbManager.delete(removeList);
        //Update treeview
        offlineContentList.removeAll(removeList);
        mainNodeLayout.removeAllViews();
        if (offlineContentList.isEmpty()) {
            emptyContentView.setVisibility(View.VISIBLE);
        } else {
            emptyContentView.setVisibility(View.GONE);
            initNodes();
        }
    }

    private int getCorrespondingBGColor(OfflineContent chapter) {
        double totalMarks = Data.getDoubleWithTwoDecimals(chapter.totalTestedMarks);
        double earnedMarks = Data.getDoubleWithTwoDecimals(chapter.earnedMarks);
        double scoreRedPercentage = Data.getInt(chapter.scoreRed) * totalMarks / 100;
        double scoreAmberPercentage = Data.getInt(chapter.scoreAmber) * totalMarks / 100;
        return R.drawable.ico_offline_subject;
        /*
        if (earnedMarks == 0 && totalMarks == 0) {
            return R.drawable.chapter_node_blue;
        } else if (earnedMarks < scoreRedPercentage) {
            return R.drawable.chapter_root_node;
        } else if (earnedMarks < scoreAmberPercentage) {
            return R.drawable.chapter_root_yellow;
        } else {
            return R.drawable.chapter_root_green;
        }
        */
    }

    private void startExerciseTest(ExerciseOfflineModel model) {
        Intent intent = new Intent(getActivity(), ExamEngineActivity.class);
        intent.putExtra(Constants.TEST_TITLE, "Exercises");
        intent.putExtra("topic_id", model.topicId);
        intent.putExtra(Constants.SELECTED_TOPIC_NAME, model.topicName);
        intent.putExtra(Constants.SELECTED_POSITION, 0);
        startActivity(intent);
    }

    private void startContentActivity(String topicId, String chapterId, String subjectId, String contentId, String contentName) {
        Intent intent = new Intent(getActivity(), ContentReadingActivity.class);
        intent.putExtra("courseId", AbstractBaseActivity.getSelectedCourseId());
        intent.putExtra("subjectId", subjectId);
        intent.putExtra("chapterId", chapterId);
        intent.putExtra("topicId", topicId);
        intent.putExtra("contentId", contentId);
        intent.putExtra("contentName", contentName);
        getActivity().startActivity(intent);
    }

    private void startVideoActivity(String contentId) {
        String videoUrl = FileUtils.get(getActivity()).getVideoDownloadPath(contentId);
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra("videopath", videoUrl);
        L.debug("Loading file from : " + videoUrl);
        File file = new File(videoUrl);
        if (file.exists()) {
            getActivity().startActivity(intent);
        } else
            showToast("File does not exist");
    }

}
