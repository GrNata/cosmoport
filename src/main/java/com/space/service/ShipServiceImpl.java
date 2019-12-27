package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ShipServiceImpl implements ShipService{

    private ShipRepository repository;

    @Autowired
    public void setShipRepository(ShipRepository repository){
        this.repository = repository;
    }

//___________________________________________________________


    @Override
    public Ship saveShip(Ship ship) {
        return repository.saveAndFlush(ship);
    }

    //    @Transactional
    @Override
    public void deleteShip(Ship ship) {
        repository.delete(ship);
    }

//_____________________________________________________________
    @Override
    public Ship getShipById(Long id) {
        Ship ship = findAll().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
//        findById(ship);
        return ship;
    }

    @Override
    public List<Ship> getShipByPage(Integer pageNumber, Integer pageSize, List<Ship> ships) {
        int x = pageNumber * pageSize;
        List<Ship> result = new ArrayList<>();

        for (int i = x; i < Math.min(x + pageSize, ships.size()); i++) {
            result.add(ships.get(i));
        }
        return result;
    }

    //_____________________________________________________________

    @Override
    public Ship creatShip(Ship ship) {
        return editShip(ship, 0l);
    }

    @Override
    public Ship editShip(Ship ship, Long id) {
        Ship ship1;

        if (id == 0) {
            ship1 = new Ship(); //  creat
            List<Ship> ships = findAll();
            ship1.setId(ships.get(ships.size() - 1).getId() + 1);
        }
        else
            ship1 = getShipById(id);    //  update

        if (ship.getName() != null)
            ship1.setName(ship.getName());
        if (ship.getPlanet() != null)
            ship1.setPlanet(ship.getPlanet());
        if (ship.getProdDate() != null)
            ship1.setProdDate(ship.getProdDate());
        if (ship.getCrewSize() != null)
            ship1.setCrewSize(ship.getCrewSize());
        if (ship.getShipType() != null)
            ship1.setShipType(ship.getShipType());
        if (ship.getSpeed() != null)
            ship1.setSpeed(ship.getSpeed());

        if (ship.getUsed() != null) {
            ship1.setUsed(ship.getUsed());
        } else {
            ship1.setUsed((boolean)Boolean.FALSE);
        }

        ship1.setRating(getValidRating(ship1));

        saveShip(ship1);

        return ship1;
    }
    //__________________________________________________________

    @Override
    public List<Ship> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Ship> listShip(String name,
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
                               Boolean isUsed) {
        List<Ship> ships = repository.findAll();

        if (name != null)               ships = listShipFromName(ships, name);
        if (planet != null)             ships = listShipFromPlanet(ships, planet);
        if (shipType != null)           ships = listShipFromShipType(ships, shipType);
        if (minCrewSize != null)        ships = listShipFromMinCrewSize(ships, minCrewSize);
        if (maxCrewSie != null)         ships = listShipFromMaxCrewSize(ships, maxCrewSie);
        if (minRating != null)          ships = listShipFromMinRating(ships, minRating);
        if (maxRating != null)          ships = listShipFromMaxRating(ships, maxRating);
        if (afterDate != null)          ships = listShipFromAfterDate(ships, afterDate);
        if (beforeDate != null)         ships = listShipFromBeforeDate(ships, beforeDate);
        if (minSpeed != null)           ships = listShipFromMinSpeed(ships, minSpeed);
        if (maxSpeed != null)           ships = listShipFromMaxSpeed(ships, maxSpeed);
        if (isUsed != null)             ships = listShipFromIsUsed(ships, isUsed);

        return ships;
    }

    //_________________________________________________________
    @Override
    public List<Ship> listShipFromPlanet(List<Ship> list, String planet) {
        List<Ship> ships = new ArrayList<>();

        for (Ship ship : list) {
            if (ship.getPlanet().contains(planet))
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromMinRating(List<Ship> list, Double minRating) {
        List<Ship> ships = new ArrayList<>();

        for (Ship ship : list){
            if (ship.getRating()>= minRating)
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromMinCrewSize(List<Ship> list, Double minCrewSize) {
        List<Ship> ships = new ArrayList<>();
        for (Ship ship : list){
            if (ship.getCrewSize() >= minCrewSize)
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromMinSpeed(List<Ship> list, Double minSpeed) {
        List<Ship> ships = new ArrayList<>();
        for (Ship ship : list){
            if (ship.getSpeed() >= minSpeed)
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromName(List<Ship> list, String name) {
        List<Ship> ships = new ArrayList<>();
        for (Ship ship : list){
            if (ship.getName().contains(name))
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromAfterDate(List<Ship> list, Long afterDate) {
        List<Ship> ships = new ArrayList<>();
        Date dateAfter = new Date(afterDate);

        for (Ship ship : list){
            if (dateAfter.getYear() < ship.getProdDate().getYear())
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromBeforeDate(List<Ship> list, Long beforeDate) {
        List<Ship> ships = new ArrayList<>();
        Date dateBefore = new Date(beforeDate);

        for (Ship ship : list){
            if (ship.getProdDate().getYear() < dateBefore.getYear())
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromMaxRating(List<Ship> list, Double maxRating) {
        List<Ship> ships = new ArrayList<>();
        for (Ship ship : list){
            if (ship.getRating() <= maxRating) {
                ships.add(ship);
            }
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromShipType(List<Ship> list, ShipType shipType) {
        List<Ship> ships = new ArrayList<>();
        for (Ship ship : list){
            if (ship.getShipType().equals(shipType))
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromMaxCrewSize(List<Ship> list, Double maxCrewSize) {
        List<Ship> ships = new ArrayList<>();
        for (Ship ship : list){
            if (ship.getCrewSize() <= maxCrewSize)
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromMaxSpeed(List<Ship> list, Double maxSpeed) {
        List<Ship> ships = new ArrayList<>();
        for (Ship ship : list){
            if (ship.getSpeed() <= maxSpeed)
                ships.add(ship);
        }
        return ships;
    }

    @Override
    public List<Ship> listShipFromIsUsed(List<Ship> list, Boolean isUsed) {
        List<Ship> ships = new ArrayList<>();
        for (Ship ship : list){
            if (ship.getUsed().equals(isUsed))
                ships.add(ship);
        }
        return ships;
    }


//______________________________________________________________________________
// вспомагательные

    @Override
    public List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        Integer page = pageNumber == null ? 0 : pageNumber;
        Integer size = pageSize == null ? 3 : pageSize;
        int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();

        return ships.subList(from, to);
    }

    private double getValidRating(Ship ship) {
        // расчет рейтинга корабля
        double v = ship.getSpeed();
        double k;
        if (ship.getUsed())
            k = 0.5;
        else k = 1;

        int y0 = 3019;

        String strDate = ship.getProdDate().toString();
        Matcher matcher = Pattern.compile("(\\d){4}").matcher(strDate);
        while (matcher.find()){
            strDate = strDate.substring(matcher.start(), matcher.end());
        }

        int y1 = Integer.parseInt(strDate);
        double rat = 80 * v * k / (y0 - y1 + 1);
        rat = Math.round(rat * 100);
        double rating = rat / 100;

        return rating;
    }

    @Override
    public boolean isValidId(Long id) {
        if (id <= 0l || id % 1 != 0l)
            return true;
        else
            return false;
    }

    @Override
    public boolean isValidShip(Ship ship) {
        //  Проверка валидности корабля - true, проверка прошла удачно
        boolean isValid = true;

        if (ship.getName().isEmpty() || ship.getName().length() > 50)
            isValid = false;
        if (ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50)
            isValid = false;

        String strDate = ship.getProdDate().toString();
        strDate = strDate.substring(strDate.length()-4).trim();
        int intDate = Integer.parseInt(strDate);
        if (ship.getProdDate() == null || intDate < 2800 || intDate > 3019)
            isValid = false;

        if (ship.getSpeed() == null || ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99)
            isValid = false;

        if (ship.getCrewSize() == null || ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
            isValid = false;

        return isValid;
    }

    @Override
    public boolean isEmptyShip(Ship ship) {
        // проверка на пусоту, true, если тело пустое

        if (ship.getRating() == null && ship.getId() == null
                && ship.getName() == null && ship.getSpeed() == null
                && ship.getShipType() == null && ship.getCrewSize() == null
                && ship.getProdDate() == null && ship.getPlanet() == null
                && ship.getUsed() == null)
            return true;
        else
            return false;
    }

    @Override
    public boolean isContainsId(Long id) {
        boolean isResult;
        List<Ship> ship = repository.findAll();
        for (Ship sh : ship){
            if (sh.getId() == id)
                return true;
        }
        return false;
    }
//_____________________________________________

    @Override
    public List<Ship> listShipSortBy(List<Ship> ships, ShipOrder order) {
//  список с сортировкой по speed

        if (order != null) {
            Collections.sort(ships, new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    switch (order) {
                        case RATING:
                            return o1.getRating().compareTo(o2.getRating());
                        case DATE:
                            return o1.getProdDate().compareTo(o2.getProdDate());
                        case ID:
                            return o1.getId().compareTo(o2.getId());
                        case SPEED:
                            return o1.getSpeed().compareTo(o2.getSpeed());
                        default:
                            return 0;
                    }
                }
            });
        }
        return ships;
    }

}
