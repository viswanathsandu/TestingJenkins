package com.corsalite.tabletapp.deserializer;


import com.corsalite.tabletapp.models.responsemodels.AnswerChoiceModel;
import com.corsalite.tabletapp.models.responsemodels.ExamModel;
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
public class ExerciseModelResponseDeserializer implements JsonDeserializer<ExamModel> {

    @Override
    public ExamModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson=new Gson();
        JsonElement value = json.getAsJsonObject();
        JsonObject jsonObject = json.getAsJsonObject();
        ExamModel examModel = null;
        if(jsonObject != null) {
            examModel = gson.fromJson(jsonObject, ExamModel.class);
            Iterable<Map.Entry<String, JsonElement>> entries = value.getAsJsonObject().entrySet();

            for (Map.Entry<String, JsonElement> entry : entries) {
                if(entry.getKey().equalsIgnoreCase("ParagraphContent")) {
                    examModel.paragraphHtml = entry.getValue().getAsString();
                }
                if(entry.getKey().equalsIgnoreCase("QuestionContent")) {
                    examModel.questionHtml = entry.getValue().getAsString();
                }
                if(entry.getKey().equalsIgnoreCase("QuestionTypeComment")) {
                    examModel.comment = entry.getValue().getAsString();
                }
                if(entry.getKey().equalsIgnoreCase("HintContent")) {
                    examModel.hintHtml = entry.getValue().getAsString();
                }
                if(entry.getKey().equalsIgnoreCase("AnswerChoices")) {
                    JsonArray jsonArray = entry.getValue().getAsJsonArray();
                    int size = jsonArray.size();
                    examModel.answerChoice = new ArrayList<>(size);
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
                        examModel.answerChoice.add(answerChoiceModel);
                    }
                }
            }
        }
        return examModel;
    }
}