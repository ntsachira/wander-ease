package com.ironcodesoftware.wanderease.model;

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


    }
    List<String> STATUS_LIST = Arrays.asList(
            State.Available.name(),
            State.Unavailable.name()
    );

    List<String> TOUR_TYPES = Arrays.asList(
            Type.Cultural_and_Heritage_Tours.name(),
            Type.Wildlife_and_Adventure_Tours.name(),
            Type.Food_and_Market_Tours.name(),
            Type.Surfing_and_Beach_Tours.name(),
            Type.City_and_Shopping_Tours.name()
    );
}
