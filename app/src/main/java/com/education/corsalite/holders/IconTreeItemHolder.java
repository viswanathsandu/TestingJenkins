package com.education.corsalite.holders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.OfflineActivity;
import com.unnamed.b.atv.model.TreeNode;

public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private ImageView arrowView;
    private ProgressBar progressBar;
    private static IconTreeItem value;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, final IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        RelativeLayout mainLayout = (RelativeLayout)view.findViewById(R.id.rl_container);
        tvValue.setText(value.text);
        tvValue.setTag(value.tag);
        this.value = value;

        final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        iconView.setImageDrawable(context.getResources().getDrawable(value.icon));

        arrowView = (ImageView) view.findViewById(R.id.arrow_icon);


        ImageView update = (ImageView)view.findViewById(R.id.btn_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Update
            }
        });

        ImageView delete =(ImageView)view.findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((OfflineActivity) context).getFragmentManager();
                AlertDialogFragment dialogFragment = new AlertDialogFragment ();
                dialogFragment.show(fragmentManager, "AlertDialog");
            }
        });

        progressBar = (ProgressBar)view.findViewById(R.id.pb_content);
        if(value.showProgress){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value.progress);
        }else {
            progressBar.setVisibility(View.GONE);
        }
        if(value.tag.equalsIgnoreCase("content")){
            arrowView.setVisibility(View.INVISIBLE);
        }else if(value.tag.equalsIgnoreCase("subject")){
            mainLayout.setBackgroundColor(context.getResources().getColor(R.color.red));
            tvValue.setTextColor(context.getResources().getColor(R.color.text_white));
            arrowView.setImageDrawable(context.getResources().getDrawable(R.drawable.ico_offline_arrow_white));
            update.setImageDrawable(context.getResources().getDrawable(R.drawable.ico_offline_update_white));
            delete.setImageDrawable(context.getResources().getDrawable(R.drawable.ico_offline_delete_white));

        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        if(((String)tvValue.getTag()).equalsIgnoreCase("subject")) {
            arrowView.setImageDrawable(context.getResources().getDrawable(active ? R.drawable.ico_offline_arrow_down_white : R.drawable.ico_offline_arrow_white));
        }else{
            arrowView.setImageDrawable(context.getResources().getDrawable(active ? R.drawable.ico_offline_arrow_down_black : R.drawable.ico_offline_arrow_black));

        }

    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public String id;
        public String tag;
        public int progress;
        public boolean showProgress;
        public Object data;

        public IconTreeItem(int icon, String text,String id,String tag,boolean showProgress, int progress) {
            this.icon = icon;
            this.text = text;
            this.id = id;
            this.tag = tag;
            this.progress = progress;
            this.showProgress = showProgress;
        }

        public void setData(Object object) {
            data = object;
        }
    }

    public static class AlertDialogFragment extends DialogFragment{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new AlertDialog.Builder(getActivity())
                    .setTitle("Are you sure you want to delete content?")
                    .setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((OfflineActivity)getActivity()).onDelete(value.id,value.tag);
                                }
                            }
                    )
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }
                    )
                    .create();
        }
    }
}
