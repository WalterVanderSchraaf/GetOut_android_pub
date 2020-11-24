package com.endeavor.walter.getout9;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.endeavor.walter.getout9.WVS_Utils.EditText_disable;
import static com.endeavor.walter.getout9.WVS_Utils.getCameraPhotoOrientation;

public class AndroidCameraApi extends AppCompatActivity {

    private static final String TAG = AndroidCameraApi.class.getSimpleName();
    private Button btnCancel, btnSave;  //takePictureButton
    private Button btnRotate;
    private TextureView textureView;
    private ImageView imgVPicture;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File tmpfile;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private android.os.Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private final Integer frontcamera = 10;
    private final Integer backcamera = 20;
    private Integer camerafacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.i(TAG, " WVS #1 onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_camera_api);
        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        imgVPicture = (ImageView) findViewById(R.id.imgVPicture);

//        takePictureButton = (Button) findViewById(R.id.btn_Capture_Picture);
//        assert takePictureButton != null;

        btnRotate = findViewById(R.id.btnRotate);
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                reverse camera: front to back or back to front
                if (camerafacing == backcamera) {
                    Log.i(TAG,"btnRotate: camerafacing == backcamera");
                    camerafacing = frontcamera;
                } else {
                    Log.i(TAG,"btnRotate: camerafacing != backcamera");
                    camerafacing = backcamera;
                }

                closeCamera();
                openCamera();
            }
        });

        btnCancel =(Button) findViewById(R.id.btn_Cancel_Picture);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave = (Button) findViewById(R.id.btn_Save_Picture);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"btnSave image="+tmpfile);
                Intent returnIntent = getIntent();
//                returnIntent.putExtra("editcontact",tmpContact);
                returnIntent.putExtra("imagefile",tmpfile);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        if (tmpfile != null && tmpfile.exists()) {
            EditText_disable(btnSave, false);
        } else {
            EditText_disable(btnSave, true);
        }

//        takePictureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String filename = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss'_image.jpg'").format(new Date());
//
//                takePicture(filename);
//                Log.i(TAG, " WVS #4 onClick - back from takePicture");
//            }
//        });

        imgVPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss'_image.jpg'").format(new Date());

                takePicture(filename);
//                Log.i(TAG, " WVS #4 onClick - back from takePicture");
            }
        });
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
//            Log.i(TAG, " WVS #4a TextureView.SurfaceTextureListener onSurfaceTextureAvailable... calling openCamera()");
            Log.i(TAG,"width " + width +"," + "height "+ height);
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
//            Log.i(TAG, " WVS #4b TextureView.SurfaceTextureListener onSurfaceTextureSizeChanged() ");
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//            Log.i(TAG, " WVS #4c TextureView.SurfaceTextureListener onSurfaceTextureDestroyed() ");
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            Log.i(TAG, "#4d onSurfaceTextureUpdated() ");
        }
    };
//    wvs hint: A callback objects for receiving updates about the state of a camera device.
//    wvs hint: A callback instance must be provided to the openCamera(String, CameraDevice.StateCallback, Handler) method to open a camera device.
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
//            Log.i(TAG, " WVS #3e CameraDevice.StateCallback onOpened() ");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
//            Log.i(TAG, " WVS #3f CameraDevice.StateCallback onDisconnected() ");
            cameraDevice.close();
        }

//        https://www.youtube.com/watch?v=Dl0hzWuCtHw

        @Override
        public void onError(CameraDevice camera, int error) {
//            Log.i(TAG, " WVS #3g CameraDevice.StateCallback onError() ");
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

//            Log.i(TAG, " WVS #4 CameraCaptureSession.CaptureCallback captureCallbackListener onCaptureCompleted()");

            createCameraPreview();
        }
    };

    protected void startBackgroundThread() {
//        Log.i(TAG, " WVS #4 startBackgroundThread() ");
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new android.os.Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
//        Log.i(TAG, " WVS #4 stopBackgroundThread() ");
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void takePicture(String file_name) {
//        Log.i(TAG, " WVS #4A takePicture " + file_name);
        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
//        Log.i(TAG, " WVS #4B create CameraManager");
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
//            int width = 640;
//            int height = 480;
            int width = 1440;//240;
            int height = 1080;// 320;


            if (jpegSizes != null && 0 < jpegSizes.length) {
//              index (0) is default, try smallest (11) 320X240
                for (int i=0; i< jpegSizes.length; i++){
                    if (jpegSizes[i].getWidth() == 1440 ){
                        width = jpegSizes[i].getWidth();
                        height = jpegSizes[i].getHeight();
                        break;
                    }
                }
//                width = jpegSizes[11].getWidth();
//                height = jpegSizes[11].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
//            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

//            wvs hint: other options:
            if (camerafacing == frontcamera) {
                captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, captureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                file_name = file_name.replace("_image", "_front");
                Log.i(TAG, " WVS FRONT CONTROL_AF_MODE_CONTINUOUS_PICTURE, "+file_name);
            } else if (camerafacing == backcamera) {
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                file_name = file_name.replace("_image", "_back");
                Log.i(TAG, " WVS BACK CONTROL_AE_MODE_ON_AUTO_FLASH, "+file_name);
            }
            // If there is a "continuous picture" mode available, use it, otherwise default to AUTO.
//            if ((characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES).toString().contains( String.valueOf( CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)))) {
//                captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//            } else {
//                captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
//            }

            // Orientation
            //wvs hint: https://stackoverflow.com/questions/10380989/how-do-i-get-the-current-orientation-activityinfo-screen-orientation-of-an-a#10383164
//          ROTATION_0 Surface.ROTATION_0 (no rotation),
//          ROTATION_90 Surface.ROTATION_90
//          ROTATION_180 Surface.ROTATION_180
//          ROTATION_270 Surface.ROTATION_270
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            Log.i(TAG," WVS ROTATION = " + rotation);
            Log.i(TAG, " WVS CaptureRequest.JPEG_ORIENTATION="+ CaptureRequest.JPEG_ORIENTATION);
            Log.i(TAG, " WVS ORIENTATIONS.get(rotation)="+ ORIENTATIONS.get(rotation));

            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            //wvs hint: storage
// Environment.getExternalStorageDirectory() refers to whatever the device manufacturer considered to be "external storage".
// On some devices, this is removable media, like an SD card. On some devices, this is a portion of on-device flash.
// Here, "external storage" means "the stuff accessible via USB Mass Storage mode when mounted on a host machine", at least for Android 1.x and 2.x.
//            final File file = new File(Environment.getExternalStorageDirectory() + "/" + file_name);
            tmpfile = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))+ "/" + file_name);

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
//                        Log.i(TAG, " WVS #4C onImageAvailable() about to save ");
                        save(bytes);
//                        Log.i(TAG, " WVS #4C onImageAvailable() finished save ");
                    } catch (FileNotFoundException e) {
//                        Log.i(TAG, " WVS #4C onImageAvailable() file not found ");
                        e.printStackTrace();
                    } catch (IOException e) {
//                        Log.i(TAG, " WVS #4C onImageAvailable() IOException " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                            Log.i(TAG,"#4C image saved:" + tmpfile);
// wvs hint: needed for pixel - captured image not displayed, ok w/o for samsung
                            String deviceName = android.os.Build.MODEL;
                            String deviceMan = android.os.Build.MANUFACTURER;
                            Log.i(TAG, " WVS Brand:"+deviceMan+",Model:"+deviceName);

//                            if (deviceMan.toUpperCase().contains("SAMSUNG")) {
////                                do nothing, works ok
//                            } else if (deviceMan.toUpperCase().contains("GOOGLE")) {
////                              was required for pixel, but not for pixel3a
////                                Log.i(TAG, "skipping closeCamera();");
////                                Log.i(TAG, "skipping openCamera();");
//                            }
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(tmpfile);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Log.i(TAG, " WVS CameraCaptureSession.CaptureCallback captureListener onCaptureCompleted()");
                    createCameraPreview();
                }
            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    protected void createCameraPreview() {
//        Log.i(TAG, " WVS #3/4 B createCameraPreview()");
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            if (cameraDevice == null){
                Log.i(TAG, "why is cameraDevice NULL");
            }
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(AndroidCameraApi.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);

//wvs hint:            https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi#5162096
//                     https://stackoverflow.com/questions/3652560/what-is-the-android-uithread-ui-thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    if (tmpfile != null && tmpfile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(tmpfile.getAbsolutePath());
                        imgVPicture.setImageBitmap(myBitmap);

                        Log.i(TAG,"runOnUiThread CALLING getCameraPhotoOrientation()");
                        int rotateImage = getCameraPhotoOrientation(tmpfile.getAbsolutePath());

                        Log.i(TAG, " WVS runOnUiThread() getCameraPhotoOrientation="+rotateImage);

//                        if ( camerafacing == frontcamera ) {
//                            Log.i(TAG, "runOnUiThread() FRONT FACING now rotating imgVPicture -"+rotateImage);
////                            imgVPicture.setRotation(-90);
//                            imgVPicture.setRotation(-rotateImage);
////                          wvs hint: this corrects the image (pixel image is missing); however, returning back to Edit screen image is 180 upsidedown
//                        } else if (camerafacing == backcamera){
//                            Log.i(TAG, "runOnUiThread() BACK FACING now rotating imgVPicture "+rotateImage);
//                            imgVPicture.setRotation(rotateImage);
//                        }
                        imgVPicture.setRotation(rotateImage);
                        EditText_disable(btnSave, false);
                    } else {
                        Log.i(TAG, " WVS runOnUiThread() tmpfile does not exist ? " + tmpfile);
                    }
                }
            });

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void openCamera() {
//        Log.i(TAG, " WVS #3a openCamera()");
        String[] mCameraIds;
        int facing;
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "#3b is camera open");
        try {

            mCameraIds = manager.getCameraIdList();

            for (int i=0; i < mCameraIds.length; i++ ){
                cameraId = mCameraIds[i];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                //            wvs hint: front facing camera
                if (camerafacing == frontcamera) {
                    facing = CameraCharacteristics.LENS_FACING_FRONT;
                } else {
                    facing = CameraCharacteristics.LENS_FACING_BACK;
                    camerafacing = backcamera;
                }
                if (characteristics.get(CameraCharacteristics.LENS_FACING ) == facing ) {
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    assert map != null;
                    imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
                    // Add permission for camera and let user grant the permission
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AndroidCameraApi.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                        return;
                    }
                    manager.openCamera(cameraId, stateCallback, null);
                    break;
                }
            }
// review... https://www.youtube.com/watch?v=YvS3iGKhQ_g
//            cameraId = manager.getCameraIdList()[0];
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//            assert map != null;
//            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
//            // Add permission for camera and let user grant the permission
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(AndroidCameraApi.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
//                return;
//            }
//            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "#3b openCamera finished");
    }


    protected void updatePreview() {
//        Log.i(TAG, " WVS #3/4 B createCameraPreview --> updatePreview()");

        if(null == cameraDevice) {
            Log.e(TAG, "#3/4 B createCameraPreview --> updatePreview() ERROR, return");
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//        captureRequestBuilder.get(CaptureRequest.JPEG_ORIENTATION);
//        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, CameraMetadata.CONTROL_SCENE_MODE_PORTRAIT);
//        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, CameraMetadata.CONTROL_SCENE_MODE_LANDSCAPE);

        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void closeCamera() {
//        Log.i(TAG, " WVS #1d closeCamera()");
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.i(TAG, " WVS #1d onRequestPermissionsResult()");
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(AndroidCameraApi.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        Log.e(TAG, " WVS #3 onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
//            Log.e(TAG, " WVS #3 onResume calling openCamera()");
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }


    @Override
    protected void onPause() {
//        Log.i(TAG, " WVS #5 onPause()");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }


}
