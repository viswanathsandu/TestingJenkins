package com.education.corsalite.models.responsemodels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aastha on 30/09/15.
        */
public class CourseAnalysisResponse {

   public List<CourseAnalysis> courseAnalysisList;

   public CourseAnalysisResponse(){
      courseAnalysisList = new ArrayList<>();
   }
}
