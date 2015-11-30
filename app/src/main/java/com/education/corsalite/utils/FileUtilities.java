package com.education.corsalite.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by Girish on 30/09/15.
 */
public class FileUtilities {
    private Writer writer;
    private String absolutePath;
    private final Context context;

    public FileUtilities(Context context) {
        super();
        this.context = context;
    }

    public String write(String fileName, String data, String folderStructure) {
        File root = Environment.getExternalStorageDirectory();
        File outDir = new File(root.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER + File.separator +folderStructure);
        if (!outDir.isDirectory()) {
            outDir.mkdirs();
        }

        File savingDirectory ;
        if(fileName.endsWith(Constants.VIDEO_FILE)) {
            savingDirectory = new File(outDir.getAbsolutePath() + File.separator + Constants.VIDEO_FOLDER);
        } else {
            savingDirectory = new File(outDir.getAbsolutePath() + File.separator + Constants.HTML_FOLDER);
        }
        if (!savingDirectory.isDirectory()) {
            savingDirectory.mkdirs();
        }
        try {
            if (!savingDirectory.isDirectory()) {
                throw new IOException(
                        "Unable to create directory Corsalite. Maybe the SD card is mounted?");
            }
            File outputFile = new File(savingDirectory, fileName);
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(data);
            writer.close();
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            Log.w("Corsalite", e.getMessage(), e);
            Toast.makeText(context, e.getMessage() + " Unable to write to external storage.",
                    Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }
    public void delete(String selectedPath){
        File root = Environment.getExternalStorageDirectory();
        File fileorDir = new File(root.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER + File.separator +selectedPath);

        if(fileorDir.isDirectory()){
            deleteDirectory(fileorDir);
        }else if(fileorDir.isFile()){
            fileorDir.delete();
        }
    }

    public Writer getWritergetWriter() {
        return writer;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

}

