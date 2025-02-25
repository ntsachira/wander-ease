package com.ironcodesoftware.wanderease.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Vehicle {
    enum Type{
        TukTuk,Car,Bike
    }
    enum GearMode{
        Auto,Manual
    }
    enum State{
        Available,Unavailable
    }
    List<String> VEHICLE_TYPES = Arrays.asList(
      Type.TukTuk.name(),
      Type.Car.name(),
      Type.Bike.name()
    );

    List<String> GEAR_MODES = Arrays.asList(
            GearMode.Auto.name(),
            GearMode.Manual.name()
    );

    List<String> STATUS_LIST = Arrays.asList(
            State.Available.name(),
            State.Unavailable.name()
    );

    String F_START_DATE = "start_date";
    String F_DAYS = "DAYS";
    String F_TOTAL_PRICE = "total_price";
    String F_RENTAL_STATUS = "rental_status";
    String F_CREATED_DATE = "created_date";
    String F_REVIEW_STATUS = "review_status";
    String F_VEHICLE = "vehicle";
    String F_USER = "renter";

}
