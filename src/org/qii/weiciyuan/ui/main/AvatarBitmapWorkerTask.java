package org.qii.weiciyuan.ui.main;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;
import org.qii.weiciyuan.support.imagetool.ImageTool;
import org.qii.weiciyuan.support.lib.MyAsyncTask;

import java.util.Map;

/**
 * User: Jiang Qi
 * Date: 12-8-3
 */
public class AvatarBitmapWorkerTask extends MyAsyncTask<String, Void, Bitmap> {


    private LruCache<String, Bitmap> lruCache;
    private String data = "";
    private ImageView view;

    private Map<String, AvatarBitmapWorkerTask> taskMap;
    private int position;


    public AvatarBitmapWorkerTask(LruCache<String, Bitmap> lruCache,
                                  Map<String, AvatarBitmapWorkerTask> taskMap,
                                  ImageView view, String url, int position) {

        this.lruCache = lruCache;
        this.taskMap = taskMap;
        this.view = view;
        this.data = url;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        view.setTag(data);
    }

    @Override
    protected Bitmap doInBackground(String... url) {

        if (!isCancelled()) {
            return ImageTool.getAvatarBitmap(data);
        }
        return null;
    }

    @Override
    protected void onCancelled(Bitmap bitmap) {
        if (bitmap != null) {

            lruCache.put(data, bitmap);

        }
        if (taskMap != null && taskMap.get(data) != null) {
            taskMap.remove(data);
        }
        view.setTag("");
        super.onCancelled(bitmap);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {

            lruCache.put(data, bitmap);

            if (view != null && view.getTag().equals(data)) {
                view.setImageBitmap(bitmap);
                view.setTag("");
            }

        }

        if (taskMap != null && taskMap.get(getMemCacheKey(data, position)) != null) {
            taskMap.remove(getMemCacheKey(data, position));
        }
    }

    protected String getMemCacheKey(String urlKey, int position) {
        return urlKey + position;
    }


}
