package com.dongfangwei.zwlibs.glide;


import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.dongfangwei.zwlibs.R;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by zhangwei on 2017/11/2.
 * 官方文档 https://muyangmin.github.io/glide-docs-cn/
 */
@GlideModule
public final class OkHttpGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, GlideBuilder builder) {
        /* MemorySizeCalculator 类，这个类根据设备的内存类型，设备 RAM 大小，以及屏幕分辨率来决定缓存的大小*/
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(2)
                .build();
        builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));

        /* 设置磁盘缓存目录与大小（默认磁盘大小为：250 MB ，位置是在应用的“缓存文件夹（内部缓存目录：context.getCacheDir()）”中的一个“特定目录（“image_manager_disk_cache”）”） */
        int diskCacheSizeBytes = 1024 * 1024 * 128;  //缓存大小 128 MB
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));//使用内部缓存目录
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, diskCacheSizeBytes));//使用外部缓存目录，context.getExternalCacheDir()

        builder.setDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_image_loading)//占位符：是当请求正在执行时被展示的 Drawable
                .error(R.drawable.ic_image_error)//错误符：在请求永久性失败时展示的 Drawable
                .fallback(R.drawable.ic_image_null)//后备回调符：在请求的url/model为 null 时展示,默认情况下Glide将 null 作为错误处理，所以可以接受 null 的应用应当显式地设置一个 fallback Drawable 。
                .disallowHardwareConfig()
        );
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, Registry registry) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(16, TimeUnit.SECONDS);
        builder.writeTimeout(16, TimeUnit.SECONDS);
        //以上设置结束，才能build(),不然设置白搭
        OkHttpClient client = builder.build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));//使用OKhttp网络库，使用 replace()方法完全移除和替换 Glide 对某种特定类型的默认处理
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
