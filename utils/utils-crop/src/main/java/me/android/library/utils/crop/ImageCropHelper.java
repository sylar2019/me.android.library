package me.android.library.utils.crop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.common.eventbus.Subscribe;

import java.io.File;

import me.android.library.common.event.ActivityResultEvent;
import me.android.library.common.utils.ToastUtils;
import me.android.library.utils.graphic.BitmapUtils;
import me.java.library.utils.base.guava.AsyncEventUtils;

public class ImageCropHelper {

    private final static int RequestCode_Camera = 1;
    private final static int RequestCode_Gallery = 2;
    private final static int RequestCode_Crop = 3;

    Activity act;
    CropCallback callback;

    int cropMode;
    boolean isManual = false;
    int width = 200;
    int height = 200;
    Uri uriCrop, uriSnap;
    String[] srcItems = {"相机", "相册"};

    public ImageCropHelper(Activity act, CropCallback callback) {
        this.act = act;
        this.callback = callback;
        this.cropMode = CropMode.Extend;
    }


    public ImageCropHelper setText(String cameraText, String galleryText) {
        srcItems = new String[]{cameraText, galleryText};
        return this;
    }

    public ImageCropHelper setPickSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ImageCropHelper setCropIsManual(boolean isManual) {
        this.isManual = isManual;
        return this;
    }

    public ImageCropHelper setCropMode(int cropMode) {
        this.cropMode = cropMode;
        return this;
    }

    public void showPickDialog(String title) {

        DialogInterface.OnClickListener dlgListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        startCamera();
                        break;
                    case 1:
                        startGallery();
                        break;
                    default:
                        break;
                }
            }
        };

        AlertDialog dlg = new AlertDialog.Builder(act)
                .setTitle(title)
                .setItems(srcItems, dlgListener)
                .create();

        dlg.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                AsyncEventUtils.regist(ImageCropHelper.this);
            }
        });

        dlg.show();
    }


    @Subscribe
    public void onEvent(ActivityResultEvent event) {

        int requestCode = event.getContent().getRequestCode();
        if (requestCode != Activity.RESULT_OK) {
            AsyncEventUtils.unregist(ImageCropHelper.this);
            return;
        }

        Intent data = event.getContent().getIntent();

        switch (requestCode) {
            // 如果是直接从相册获取
            case RequestCode_Gallery:
                if (data != null) {
                    startCrop(data.getData());
                }
                break;
            // 如果是调用相机拍照时
            case RequestCode_Camera:
                if (uriSnap != null) {
                    startCrop(uriSnap);
                }
                break;
            // 取得裁剪后的图片
            case RequestCode_Crop:
                if (data != null) {
                    AsyncEventUtils.unregist(ImageCropHelper.this);
                    saveCropImage(data);
                }
                break;
            default:
                break;

        }
    }

    /**
     * 启动相机
     */
    private void startCamera() {
        try {
            File snapFile = createTempFile("snap", ".jpg");
            uriSnap = Uri.fromFile(snapFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSnap);
            act.startActivityForResult(intent, RequestCode_Camera);
        } catch (Exception e) {
            ToastUtils.showException(e);
        }
    }

    /**
     * 启动相册
     */
    private void startGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            act.startActivityForResult(intent, RequestCode_Gallery);
        } catch (Exception e) {
            ToastUtils.showException(e);
        }
    }


    /**
     * 启动图片裁剪
     */
    protected void startCrop(Uri uri) {

        try {
            File cropFile = createTempFile("tmp_crop", ".jpg");
            uriCrop = Uri.fromFile(cropFile);

            switch (cropMode) {
                case CropMode.Primary:
                    cropByPrimary(uri);
                    break;
                case CropMode.Extend:
                    cropByExtend(uri);
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            ToastUtils.showException(e);
        }

    }

    void cropByPrimary(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");

        if (!isManual) {
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);

            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
        }

        // 是否返回数据
        intent.putExtra("return-data", false);
        // 直接输出文件时
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCrop);

        // 是否保留比例
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);

        // JPEG 格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 关闭人脸检测
        intent.putExtra("noFaceDetection", true);

        act.startActivityForResult(intent, RequestCode_Crop);
    }

    void cropByExtend(Uri uri) {
        Intent intent = new Intent(act, ImageCropActivity.class);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCrop);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        act.startActivityForResult(intent, RequestCode_Crop);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected void saveCropImage(Intent picdata) {
        Bitmap bmp = BitmapUtils.fromUri(act, uriCrop);

        if (bmp != null) {
            if (isManual) {
                width = bmp.getWidth();
                height = bmp.getHeight();
            }

            bmp = BitmapUtils.zoomBySize(bmp, width, height);
            callback.onSelected(bmp);
        }
    }

    private File createTempFile(String prefix, String suffix) throws Exception {
        String state = Environment.getExternalStorageState();
        File path;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            path = Environment.getExternalStorageDirectory();
        } else {
            path = act.getFilesDir();
        }

        return File.createTempFile(prefix, suffix, path);
    }

}
