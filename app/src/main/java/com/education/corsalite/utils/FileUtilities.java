package com.education.corsalite.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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

    public String write(String fileName, String data) {
        File root = Environment.getExternalStorageDirectory();
        File outDir = new File(root.getAbsolutePath() + File.separator + "Corsalite");
        if (!outDir.isDirectory()) {
            outDir.mkdir();
        }
        try {
            if (!outDir.isDirectory()) {
                throw new IOException(
                        "Unable to create directory Corsalite. Maybe the SD card is mounted?");
            }
            File outputFile = new File(outDir, fileName);
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

    public Writer getWritergetWriter() {
        return writer;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

}

