package com.oybek.ekbts;

import com.google.gson.Gson;
import com.oybek.ekbts.entities.TramStop;
import com.sun.javafx.geom.Vec2d;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hashimov
 * @author abelyants
 */

@Component
public class Engine {
    ArrayList<TramStop> tramStops;

    public Engine() {
        Gson gson = new Gson();

        try (Reader reader = new FileReader(getResourceFile("json/tram-stops.json"))) {
            // Convert JSON to Java Object
            TramStop[] tramStopsRaw = gson.fromJson(reader, TramStop[].class);
            tramStops = new ArrayList<>(Arrays.asList(tramStopsRaw));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TramStop getNearest(Vec2d coord) {
        if (tramStops.size() == 0) {
            System.out.println("No single tram stop given");
            return null;
        }

        TramStop nearestTramStop = tramStops.get(0);
        for (TramStop currentTramStop : tramStops) {
            if (coord.distanceSq(currentTramStop.getLatitude(), currentTramStop.getLongitude())
                    < coord.distanceSq(nearestTramStop.getLatitude(), nearestTramStop.getLongitude())) {
                nearestTramStop = currentTramStop;
            }
        }

        return nearestTramStop;
    }

    private double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = 0;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }


    public List<TramStop> getNearestInRadius(Vec2d coord, double radius) {
        if (tramStops.size() == 0) {
            System.out.println("No single tram stop given");
            return null;
        }
        TramStop nearestTramStop = tramStops.get(0);
        for (TramStop currentTramStop : tramStops) {
            if (coord.distanceSq(currentTramStop.getLatitude(), currentTramStop.getLongitude())
                    < coord.distanceSq(nearestTramStop.getLatitude(), nearestTramStop.getLongitude())) {
                nearestTramStop = currentTramStop;
            }
        }
        List<TramStop> nearestInRadius = new ArrayList<>();
        nearestInRadius.add(nearestTramStop);
        for (TramStop currentTramStop : tramStops) {
            if (currentTramStop.equals(nearestTramStop))
                continue;
            if (distance(currentTramStop.getLatitude(), nearestTramStop.getLatitude(), currentTramStop.getLongitude(), nearestTramStop.getLongitude()) < radius) {
                nearestInRadius.add(currentTramStop);
            }
        }
        return nearestInRadius;
    }


    public File getResourceFile(String fileName) {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }
}
