package com.project.jhunting1.Neural_Network;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CNN_Model {

    private static final String MODEL_FILE  = "frozen_model.pb";
    private static final String INPUT_NODE  = "input/X";
    private static final String OUTPUT_NODE = "FullyConnected/Softmax";
    private static final String[] OUTPUT_NODES = {"FullyConnected/Softmax"};
    private float[] result = new float[5];

    private static Map<String,ArrayList<int[]>> vocabMap = null;
    private static int maxLength = 0;
    private final int batchSize = 1;
    private static AssetManager assetManager;


    public static CNN_Model initialize(AssetManager asset_Manager){
        CNN_Model helper = new CNN_Model();

        assetManager = asset_Manager;
        String vocabJson = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open("job_desc_sequential.json")));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            vocabJson = sb.toString();
            br.close();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type type = new TypeToken<Map<String, ArrayList<int[]>>>(){}.getType();
            vocabMap = gson.fromJson(vocabJson, type);
            Log.e("Initializing", "Finished initialize");
        }
        catch(Exception e){
            Log.e("Helper Error",e.toString());
        }

        return helper;
    }

    public float[] getInput(String text){
        Log.e("text", text);

        ArrayList<int[]> sequences = buildInput(TextPreprocess.preprocess(text));
        Log.e("Sequence size", Integer.toString(sequences.size()));
        float[] realInput = new float[100];
        int i = 0;
        for (int[] intArray : sequences) {
            for (int value : intArray) {
                realInput[i] = (float) value;
                i++;
            }
        }

        for (float f : realInput){
            Log.e("Real Input", Float.toString(f));
        }

        Log.e("input Length", Integer.toString(realInput.length));
        return realInput;
    }

    private ArrayList<int[]> buildInput(List<String> words){
        maxLength = 100;

        ArrayList<int[]> input = new ArrayList<int[]>(batchSize * maxLength);
        if (vocabMap == null) {
            Log.e("Error", "Empty Vocab");
        } else {
            int i = 0;
            for (String word : words) {
                if (vocabMap.get(word) != null) {
                    ArrayList<int[]> index = vocabMap.get(word);
                    input.addAll(index);
                    i++;
                    if (i > words.size()){
                        break;
                    }
                }
            }
        }

        for (int[] intArray : input){
            for (int ii : intArray){
                Log.e("input", Integer.toString(ii));
            }
        }
        return input;
    }

    public String[] classify(String textToClassify){
        TensorFlowInferenceInterface tfInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);

        String filtered_textToClassify = textToClassify.toLowerCase().replace(".", "");
        float[] input = getInput(filtered_textToClassify);

        tfInterface.feed(INPUT_NODE, input, 1, input.length);
        tfInterface.run(OUTPUT_NODES);
        tfInterface.fetch(OUTPUT_NODE, result);

        for (float f: result){
            Log.e("Results", Float.toString(f));
        }

        String[] labels = get_labels(result);
        return labels;
    }

    private String[] get_labels(float[] result){
        String[] labels = new String[2];

        float highest = Integer.MIN_VALUE;
        float second_highest = Integer.MIN_VALUE;
        int highest_label = Integer.MIN_VALUE;
        int second_highest_label = Integer.MIN_VALUE;
        for (int i = 0; i < result.length; i++) {
            if (result[i] > highest) {
                second_highest = highest;
                highest = result[i];

                second_highest_label = highest_label;
                highest_label = i;
            } else if (result[i] > second_highest) {
                second_highest = result[i];
                second_highest_label = i;
            }
        }

        Map<Integer, String> map = getIntToLabelMap();
        labels[0] = map.get(highest_label);
        labels[1] = map.get(second_highest_label);

        Log.e("Highest : ", labels[0] + " : " + Float.toString(highest));
        Log.e("Second Highest : ", labels[1] + " : " + Float.toString(second_highest));
        return labels;
    }

    private Map<Integer, String> getIntToLabelMap(){
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "Programmer");
        map.put(1, "Construction");
        map.put(2, "Accounting");
        map.put(3, "Community Service");
        map.put(4, "Legal");

        return map;
    }
}
