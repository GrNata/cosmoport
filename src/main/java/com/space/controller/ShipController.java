package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipController {

    @Autowired
    private ShipService shipService;

    public ShipController() {
    }

    @RequestMapping(value = "/rest/ships", method = RequestMethod.GET)
//  GetAll
    public List<Ship> allShip(@RequestParam(value = "planet", required = false) String planet,
                              @RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "shipType", required = false) ShipType shipType,
                              @RequestParam(value = "after", required = false) Long afterDate,
                              @RequestParam(value = "before", required = false) Long beforeDate,
                              @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                              @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                              @RequestParam(value = "minCrewSize", required = false) Double minCrewSize,
                              @RequestParam(value = "maxCrewSize", required = false) Double maxCrewSize,
                              @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                              @RequestParam(value = "minRating", required = false) Double minRating,
                              @RequestParam(value = "maxRating", required = false) Double maxRating,
                              @RequestParam(value = "order", required = false) ShipOrder order,
                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false) Integer pageSize
    ){

        List<Ship> ships = shipService.listShip(name, planet, shipType, minCrewSize, maxCrewSize, minRating, maxRating, afterDate,
                beforeDate, minSpeed, maxSpeed, isUsed);
        ships = shipService.listShipSortBy(ships, order);
        ships = shipService.getPage(ships, pageNumber, pageSize);

        return ships;
    }

    @RequestMapping(value = "rest/ships/count", method = RequestMethod.GET)
    //  GetCount
    public int sizeShips(@RequestParam(value = "planet", required = false) String planet,
                         @RequestParam(value = "minRating", required = false) Double minRating,
                         @RequestParam(value = "minCrewSize", required = false) Double minCrewSize,
                         @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                         @RequestParam(value = "name", required = false) String name,
                         @RequestParam(value = "after", required = false) Long afterDate,
                         @RequestParam(value = "maxRating", required = false) Double maxRating,
                         @RequestParam(value = "shipType", required = false) ShipType shipType,
                         @RequestParam(value = "maxCrewSize", required = false) Double maxCrewSize,
                         @RequestParam(value = "before", required = false) Long beforeDate,
                         @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                         @RequestParam(value = "isUsed", required = false) Boolean isUsed) {

        List<Ship> ships = shipService.listShip(name, planet, shipType, minCrewSize, maxCrewSize, minRating, maxRating, afterDate,
                beforeDate, minSpeed, maxSpeed, isUsed);

        return ships.size();
    }


    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.GET)
    // Get Ship
    public ResponseEntity<Ship> findOneShip(@PathVariable(value = "id", required = false) long id) {
        Ship ship = null;
        if (shipService.isValidId(id))
            return new ResponseEntity<Ship>(HttpStatus.BAD_REQUEST);
        if (!shipService.isContainsId(id))
            return new ResponseEntity<Ship>(HttpStatus.NOT_FOUND);
        else {
            ship = shipService.getShipById(id);
            return new ResponseEntity<Ship>(ship, HttpStatus.OK);
        }
    }

    @PostMapping("rest/ships/{id}")
    @ResponseBody
    //  Update
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship,
                                           @PathVariable(value = "id", required = false) Long id) {
        try {
            {   //  блок проверки валидности
                if (shipService.isValidId(id) || !shipService.isValidShip(ship)) {
                    //  если id ноль,  отрицательный, целый, а т.ж. не явл. числом  и некорректные данные
                    return new ResponseEntity<Ship>(HttpStatus.BAD_REQUEST);
                }
                if (!shipService.isContainsId(id)) {
                    // id не существует
                    return new ResponseEntity<Ship>(HttpStatus.NOT_FOUND);
                }
            }

            ship = shipService.editShip(ship, id);

            return new ResponseEntity<>(ship, HttpStatus.OK);

        } catch (NullPointerException e){
            if (shipService.isEmptyShip(ship)) {
                // /rest/ships/{id} с пустым телом запроса, корабль не должен изменяться
                ship = shipService.getShipById(id);
            }
            else {
                // rest/ships/{id} корабль должен обновляться и рейтинг пересчитываться
                // test 11
                ship = shipService.editShip(ship, id);
            }
            return new ResponseEntity<>(ship, HttpStatus.OK);
        }
    }



    @RequestMapping(value = "/rest/ships/", method = RequestMethod.POST)
    // Create
    public ResponseEntity<Ship> creatShip(@RequestBody Ship ship) {
        if (shipService.isEmptyShip(ship) || !shipService.isValidShip(ship))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ship = shipService.creatShip(ship);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping(value = "rest/ships/{id}", method = RequestMethod.DELETE)
    // Delete
    public ResponseEntity<Ship> deleteShip(@PathVariable(value = "id") Long id){
        if (shipService.isValidId(id)) {
            //  если id ноль,  отрицательный, целый, а т.ж. не явл. числом  и некорректные данные
            return new ResponseEntity<Ship>(HttpStatus.BAD_REQUEST);
        }
        if (!shipService.isContainsId(id)) {
            // id не существует
            return new ResponseEntity<Ship>(HttpStatus.NOT_FOUND);
        }
        Ship ship = shipService.getShipById(id);
        if (!shipService.isEmptyShip(ship)) {
            shipService.deleteShip(ship);
            return new ResponseEntity<>(ship, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
