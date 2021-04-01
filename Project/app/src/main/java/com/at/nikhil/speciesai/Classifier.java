package com.at.nikhil.speciesai;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.label.TensorLabel;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Classifier {
    private static final int MAX_RESULTS = 3;
    private MappedByteBuffer tfliteModel;

    private final int imageSizeX;
    private final int imageSizeY;

    private GpuDelegate gpuDelegate = null;
    protected Interpreter tflite;
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    private List<String> labels;
    private TensorImage inputImageBuffer;
    private final TensorBuffer outputProbabilityBuffer;
    private final TensorProcessor probabilityProcessor;

    public static class Recognition{

        private final String id;
        private final String title;
        private final Float confidence;

        public Recognition(final String id, final String title, final Float confidence) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }

            if (title != null) {
                resultString += title + " ";
            }

            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }

            return resultString.trim();
        }

    }

    public static Classifier create(Activity activity,
                                    Model.Device device,int type) throws IOException{

        //Type: 0 for plants,1 for animals, 2 for birds
        return new Classifier(activity, device, type);
    }

    protected Classifier(Activity activity,
                         Model.Device device,int type) throws IOException{

        tfliteModel = FileUtil.loadMappedFile(activity, getModelName(type));

        switch (device){

            case GPU:
                gpuDelegate = new GpuDelegate();
                tfliteOptions.addDelegate(gpuDelegate);
                break;

            case CPU:
                break;
        }
        tflite = new Interpreter(tfliteModel,tfliteOptions);

        labels = FileUtil.loadLabels(activity,getLabels(type));

        int imageTensorIndex = 0;
        int[] inputShape = tflite.getInputTensor(imageTensorIndex).shape();
        //{1,height,width,3}

        imageSizeY = inputShape[1];
        imageSizeX = inputShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

        int probabilityTensorIndex = 0;
        int[] probabilityShape = tflite.getOutputTensor(probabilityTensorIndex).shape();
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        inputImageBuffer = new TensorImage(imageDataType);
        outputProbabilityBuffer = TensorBuffer.createFixedSize(
                probabilityShape,probabilityDataType);

        probabilityProcessor =
                new TensorProcessor.Builder()
                .add(new NormalizeOp(0.0f,1.0f))
                .build();
    }

    private TensorImage loadImage(Bitmap bitmap){

        Matrix matrix = new Matrix();
        float scaleHeight = imageSizeY/((float) bitmap.getHeight());
        float scaleWidth = imageSizeX/((float) bitmap.getWidth());

        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap res_bm = Bitmap.createBitmap(
                bitmap,0,0,
                bitmap.getWidth(),bitmap.getHeight(),
                matrix,true);

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                .add(new NormalizeOp(0.0f,255.0f))
                .build();

        inputImageBuffer.load(res_bm);
        return  imageProcessor.process(inputImageBuffer);
    }

    public void close(){
        if(tflite != null){
            tflite.close();
            tflite = null;
        }
        if(gpuDelegate != null){
            gpuDelegate.close();
            gpuDelegate = null;
        }
        tfliteModel = null;
    }

    public List<Recognition> recognizeImage(Bitmap bitmap){

        inputImageBuffer = loadImage(bitmap);
        tflite.run(inputImageBuffer.getBuffer(),
                outputProbabilityBuffer.getBuffer().rewind());

        Map<String,Float> labeledProbability =
                new TensorLabel(
                        labels,
                        probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();

        return getTopKProbability(labeledProbability);

    }

    private static List<Recognition> getTopKProbability(Map<String,Float> labeledProbability){
        PriorityQueue<Recognition> p_queue =
                new PriorityQueue<>(
                        MAX_RESULTS,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(Recognition lhs, Recognition rhs) {

                                // Reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {

            p_queue.add(new Recognition("" + entry.getKey(),
                    entry.getKey(),
                    entry.getValue()));
        }

        final ArrayList<Recognition> recognized = new ArrayList<>();
        int recognizedSize = Math.min(p_queue.size(), MAX_RESULTS);

        for (int i = 0; i < recognizedSize; ++i) {
            recognized.add(p_queue.poll());
        }

        return recognized;
    }

    private String getModelName(int type){
        if(type == 0){
            return "plant_model.tflite";
        }
        else if(type == 1){
            return "animal_model.tflite";
        }
        else{
            return "bird_model.tflite";
        }

    }

    private String getLabels(int type) {
        if(type == 0){
            return "labels.txt";
        }
        else if(type == 1){
            return "animal_labels.txt";
        }
        else{
            return "bird_labels.txt";
        }
    }

    private int getImageSizeX(){
        return imageSizeX;
    }

    private int getImageSizeY(){
        return imageSizeY;
    }
}
