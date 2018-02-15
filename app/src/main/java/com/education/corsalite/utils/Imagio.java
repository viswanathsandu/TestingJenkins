package com.education.corsalite.utils;

/**
 * Created by praveen on 15/2/18.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.education.corsalite.BuildConfig;
import com.education.corsalite.services.ApiClientService;
import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;

public class Imagio {

    public static void load(Context context, String imageUrl, ImageView imageView) {
        load(context, imageUrl, imageView, null, null);
    }

    public static void load(Context context, String imageUrl, ImageView imageView, Drawable defaultDrawable) {
        load(context, imageUrl, imageView, defaultDrawable, null);
    }

    public static void load(Context context, String imageUrl, ImageView imageView, Drawable defaultDrawable, Drawable errorDrawable) {
        FileUtils fileUtils = FileUtils.get(context);
        if(defaultDrawable != null) {
            imageView.setImageDrawable(defaultDrawable);
        }
        imageUrl = getUrl(imageUrl);
        String localFilePath = fileUtils.getLocalFilePath(imageUrl);
        if(imageUrl.endsWith("/")) {
            if(errorDrawable != null) {
                imageView.setImageDrawable(errorDrawable);
            } else if(defaultDrawable != null) {
                imageView.setImageDrawable(defaultDrawable);
            }
            return;
        }
        if(fileUtils.fileExists(localFilePath)) {
            L.info("Loading from local memory : "+localFilePath);
            Picasso.with(context).load(new File(localFilePath)).into(imageView);
        } else {
            DownloadRequest downloadRequest = null;
                downloadRequest = new DownloadRequest(Uri.parse(imageUrl))
                        .addCustomHeader("cookie", ApiClientService.getSetCookie())
                        .setRetryPolicy(new DefaultRetryPolicy())
                        .setPriority(DownloadRequest.Priority.HIGH)
                        .setDestinationURI(Uri.parse(imageUrl)).setPriority(DownloadRequest.Priority.HIGH)
                        .setDownloadContext(context) //Optional
                        .setStatusListener(new DownloadStatusListenerV1() {
                            @Override
                            public void onDownloadComplete(DownloadRequest downloadRequest) {

                            }
                            @Override
                            public void onDownloadFailed(DownloadRequest downloadRequest, int i, String s) {

                            }

                            @Override
                            public void onProgress(DownloadRequest downloadRequest, long l, long l1, int i) {
                                // Do nothing
                            }
                        });

            new ThinDownloadManager().add(downloadRequest);
/*
            AndroidNetworking.get(imageUrl)
                    .setTag("imageRequestTag")
                    .setPriority(Priority.MEDIUM)
                    .setBitmapConfig(Bitmap.Config.ARGB_8888)
                    .setOkHttpClient(new ApiClientService(GCFApplication.get(context)).getOkHttpClient())
                    .build()
                    .getAsBitmap(new BitmapRequestListener() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            if(imageView.getId() == R.id.subject_list_icon) {
                                L.info(bitmap.toString());
                            }
                            imageView.setImageBitmap(bitmap);
                            fileUtils.saveImage(bitmap, localFilePath);
                        }

                        @Override
                        public void onError(ANError error) {
                            // L.error(error);
                            if(imageView.getId() == R.id.subject_list_icon) {
                                L.info(error.getMessage());
                            }
                            if(errorDrawable != null) {
                                imageView.setImageDrawable(errorDrawable);
                            } else if(defaultDrawable != null) {
                                imageView.setImageDrawable(defaultDrawable);
                            }
                        }
                    });*/
        }
    }

    private static String getUrl(String url) {
        if(!url.startsWith("http")) {
            url = url.replace("./", "");
            String format = "";
            if(url.startsWith("/")) {
                format = "%s%s";
            } else {
                format = "%s/%s";
            }
            url = String.format(format, BuildConfig.APP_IMAGE_URL, url);
        }
        url = url.replaceFirst("//", "!");
        url = url.replaceAll("//", "/");
        url = url.replaceFirst("!", "//");
        return url;
    }
}
