package com.ironcodesoftware.wanderease.model;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public interface Guide {
    enum State{
        Available,Unavailable
    }

    enum Type{
        Cultural_and_Heritage_Tours,
        Wildlife_and_Adventure_Tours,
        Food_and_Market_Tours,
        Surfing_and_Beach_Tours,
        City_and_Shopping_Tours;

        @NonNull
        @Override
        public String toString() {
            return this.name().replace("_", " ");
        }
    }
    List<String> STATUS_LIST = Arrays.asList(
            State.Available.name(),
            State.Unavailable.name()
    );

    List<String> TOUR_TYPES = Arrays.asList(
            Type.Cultural_and_Heritage_Tours.toString(),
            Type.Wildlife_and_Adventure_Tours.toString(),
            Type.Food_and_Market_Tours.toString(),
            Type.Surfing_and_Beach_Tours.toString(),
            Type.City_and_Shopping_Tours.toString()
    );

}
