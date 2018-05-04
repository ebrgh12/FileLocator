package com.filelocater.model;

import android.support.annotation.NonNull;

/**
 * Created by ADMIN on 25-04-2018.
 */

public class FrequentModel implements Comparable<FrequentModel>{
    String name;
    Integer count;

    public FrequentModel(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public int compareTo(@NonNull FrequentModel frequentModel) {
        return frequentModel.getCount().compareTo(this.count);
    }
}
