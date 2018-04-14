package com.oybek.ekbts;

import com.oybek.ekbts.entities.Result;
import com.oybek.ekbts.entities.TramInfo;
import com.oybek.ekbts.entities.TramStop;
import com.sun.javafx.geom.Vec2d;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RequestController {

    private Ettu ettu;
    private Engine engine;

    public RequestController(Ettu ettu, Engine engine) {
        this.ettu = ettu;
        this.engine = engine;
    }

    @GetMapping(value = "/test")
    public String getInfo() {
        return "ok";
    }

    @GetMapping(value = "/get")
    public Result get(@RequestParam("latitude") double latitude
            , @RequestParam("longitude") double longitude ) {
        TramStop tramStop = engine.getNearest( new Vec2d( latitude, longitude ) );

        Result result = ettu.getInfo(tramStop);
        result.setTramStopName(tramStop.getName() + " " + tramStop.getDirection());

        return result;
    }
    @GetMapping(value = "/getNearestInRadius")
    public List<Result> getNearestInRadius (@RequestParam("latitude") double latitude
            , @RequestParam("longitude") double longitude, @RequestParam("radius") double radius){
        List<TramStop> tramStops = engine.getNearestInRadius(new Vec2d(latitude,longitude),radius);
        List<Result> results = new ArrayList<>();
        for(TramStop tramStop: tramStops){
            Result result = ettu.getInfo(tramStop);
            result.setTramStopName(tramStop.getName() + " " + tramStop.getDirection());
            results.add(result);
        }
        return results;
    }
}
