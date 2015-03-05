package com.codejune.sutaekhighschool.util;

import com.orm.SugarRecord;

public class MealCache extends SugarRecord<MealCache> {
    String lunch;
    String lunchkcal;
    String dinner;
    String dinnerkcal;

    public MealCache(){
    }

    public MealCache(String Lunch, String LunchKcal, String Dinner, String DinnerKcal){
        this.lunch = Lunch;
        this.lunchkcal = LunchKcal;
        this.dinner = Dinner;
        this.dinnerkcal = DinnerKcal;
    }
}
