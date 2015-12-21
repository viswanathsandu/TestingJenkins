package com.education.corsalite.deserializer;


import android.util.Log;

import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.models.responsemodels.ExerciseModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Girish on 19/12/15.
 */
public class ExerciseModelResponseDeserializer implements JsonDeserializer<ExerciseModel> {

    @Override
    public ExerciseModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson=new Gson();
        JsonElement value = json.getAsJsonObject();
        JsonObject jsonObject = json.getAsJsonObject();
        ExerciseModel exerciseModel = null;
        if(jsonObject != null) {
            exerciseModel = gson.fromJson(jsonObject, ExerciseModel.class);
            Iterable<Map.Entry<String, JsonElement>> entries = value.getAsJsonObject().entrySet();

            for (Map.Entry<String, JsonElement> entry : entries) {
                if(entry.getKey().equalsIgnoreCase("ParagraphContent")) {
                    exerciseModel.paragraphHtml = entry.getValue().getAsString();
                }
                if(entry.getKey().equalsIgnoreCase("QuestionContent")) {
                    exerciseModel.questionHtml = entry.getValue().getAsString();
                }
                if(entry.getKey().equalsIgnoreCase("QuestionTypeComment")) {
                    exerciseModel.comment = entry.getValue().getAsString();
                }
                if(entry.getKey().equalsIgnoreCase("HintContent")) {
                    exerciseModel.hintHtml = entry.getValue().getAsString();
                }
                if(entry.getKey().equalsIgnoreCase("AnswerChoices")) {
                    JsonArray jsonArray = entry.getValue().getAsJsonArray();
                    int size = jsonArray.size();
                    exerciseModel.answerChoice = new ArrayList<>(size);
                    for(int i = 0; i < size ; i++) {
                        JsonObject jObject = jsonArray.get(i).getAsJsonObject();
                        AnswerChoiceModel answerChoiceModel = new AnswerChoiceModel();
                        if(!jObject.get("idAnswerKey").isJsonNull()) {
                            answerChoiceModel.idAnswerKey = jObject.get("idAnswerKey").getAsString();
                        }
                        if(!jObject.get("IsCorrectAnswer").isJsonNull()) {
                            answerChoiceModel.isCorrectAnswer = jObject.get("IsCorrectAnswer").getAsString();
                        }
                        if(!jObject.get("AnswerChoiceTextContent").isJsonNull()) {
                            answerChoiceModel.answerChoiceTextHtml = jObject.get("AnswerChoiceTextContent").getAsString();
                        }
                        if(!jObject.get("AnswerChoiceExplanationContent").isJsonNull()) {
                            answerChoiceModel.answerChoiceExplanationHtml = jObject.get("AnswerChoiceExplanationContent").getAsString();
                        }
                        if(!jObject.get("AnswerText").isJsonNull()) {
                            answerChoiceModel.answerKeyText = jObject.get("AnswerText").getAsString();
                        }
                        exerciseModel.answerChoice.add(answerChoiceModel);
                    }
                }
            }
        }
        return exerciseModel;
    }
}