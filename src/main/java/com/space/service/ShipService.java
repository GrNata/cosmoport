package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.io.Serializable;
import java.util.List;

public interface ShipService extends Serializable {

    Ship getShipById(Long id);
    List<Ship> getShipByPage(Integer pageNumber, Integer pageSize, List<Ship> ships);
//    ResponseEntity<Ship> findById(Ship ship);

    Ship saveShip(Ship ship);
    Ship editShip(Ship ship, Long id);
    Ship creatShip(Ship ship);
    void deleteShip(Ship ship);

    List<Ship> findAll();

    List<Ship> listShip(String name,
                        String planet,
                        ShipType shipType,
                        Double minCrewSize,
                        Double maxCrewSie,
                        Double minRating,
                        Double maxRating,
                        Long afterDate,
                        Long beforeDate,
                        Double minSpeed,
                        Double maxSpeed,
                        Boolean isUsed);

    List<Ship> listShipFromPlanet(List<Ship> ships, String planet);
    List<Ship> listShipFromMinCrewSize(List<Ship> ships, Double minCrewSize);
    List<Ship> listShipFromMinSpeed(List<Ship> ships, Double minSpeed);
    List<Ship> listShipFromMinRating(List<Ship> ships, Double minRating);
    List<Ship> listShipFromName(List<Ship> ships, String name);
    List<Ship> listShipFromAfterDate(List<Ship> ships, Long afterDate);
    List<Ship> listShipFromMaxRating(List<Ship> ships, Double maxRating);
    List<Ship> listShipFromShipType(List<Ship> list, ShipType shipType);
    List<Ship> listShipFromMaxCrewSize(List<Ship> list, Double maxCrewSize);
    List<Ship> listShipFromBeforeDate(List<Ship> list, Long beforeDate);
    List<Ship> listShipFromMaxSpeed(List<Ship> list, Double maxSpeed);
    List<Ship> listShipFromIsUsed(List<Ship> list, Boolean isUsed);

    List<Ship> listShipSortBy(List<Ship> ships, ShipOrder order);

    List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize);

    boolean isValidId(Long id);
    boolean isValidShip(Ship ship);
    boolean isEmptyShip(Ship ship);
    boolean isContainsId(Long id);
}
