package com.oybek.bridgevk;

import com.google.gson.*;
import com.oybek.bridgevk.Entities.Message;
import com.oybek.bridgevk.Entities.TramInfo;
import com.oybek.bridgevk.Entities.TramStopInfo;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class SuperBot {
    private QueueController queueController;

    // TODO: very bad need to refactor
    private String url = "http://localhost:8888/get_nearest?latitude=%f&longitude=%f";
    private String urlGetNearestToNearest = "http://localhost:8888/get_nearest_to_nearest?latitude=%f&longitude=%f";
    private String urlGetDistance = "http://localhost:8888/get_distance?lat1=%f&lon1=%f&lat2=%f&lon2=%f";

    public SuperBot(QueueController queueController) {
        this.queueController = queueController;

        new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        }).start();
    }

    // soon it will become class
    private String getReaction(Message msg) {
        Gson gson = new Gson();

        JsonParser parser = new JsonParser();

        StringBuilder answer = new StringBuilder();

        if( msg.getGeo() == null ) {
            answer.append("Для того чтобы я мог найти ближайшую остановку, отправьте мне свои координаты, вот как это делается:");
            msg.setAttachment("doc-163915852_464149858");
        } else {
            String response = Courier.get(String.format(url, msg.getGeo().getLatitude(), msg.getGeo().getLongitude()));
            TramStopInfo tramStopInfo = gson.fromJson(response, TramStopInfo.class);

            if (tramStopInfo == null) {
                answer.append("Извините, не удалось найти информацию о трамваях 😞");
            } else {
                answer.append(String.format("🚋 Ближайшая остановка: %s\n", tramStopInfo.getTramStopName()));

                for (TramInfo tramInfo : tramStopInfo.getTramInfoList()) {
                    long timeToReach = Long.parseLong(tramInfo.getTimeReach());
                    answer.append(
                            timeToReach == 0
                                    ? tramInfo.getRoute() + "-й трамвай уже подъезжает\n"
                                    : tramInfo.getRoute() + "-й трамвай будет через " + tramInfo.getTimeReach() + " мин.\n"
                    );
                }

                double nearestTramStopLatitude = tramStopInfo.getLatitude();
                double nearestTramStopLongitude = tramStopInfo.getLongitude();

                String requestResult = Courier.get(String.format(urlGetDistance, msg.getGeo().getLatitude(), msg.getGeo().getLongitude(), nearestTramStopLatitude, nearestTramStopLongitude));
                final double farValue = 25.0;
                if( Double.parseDouble(requestResult) > farValue ) {
                    response = Courier.get(String.format(urlGetNearestToNearest, msg.getGeo().getLatitude(), msg.getGeo().getLongitude()));
                    TramStopInfo tramStop2Info = gson.fromJson(response, TramStopInfo.class);

                    answer.append(String.format("🚋 Другое направление: %s\n", tramStop2Info.getTramStopName()));

                    for (TramInfo tramInfo : tramStop2Info.getTramInfoList()) {
                        long timeToReach = Long.parseLong(tramInfo.getTimeReach());
                        answer.append(
                                timeToReach == 0
                                        ? tramInfo.getRoute() + "-й трамвай уже подъезжает\n"
                                        : tramInfo.getRoute() + "-й трамвай будет через " + tramInfo.getTimeReach() + " мин.\n"
                        );
                    }
                }
            }
        }

        return answer.toString();
    }

    // TODO: refactor this function, deserialize json before working with message
    // very bad bad ... bad code
    public void work() {
        while( true ) {
            // if no work ...
            if (queueController.getQueueToBot().isEmpty()) {
                // ... sleep 0.5 second
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                // ... get message from queue
                Message msg = queueController.getQueueToBot().poll();

                // get reaction of bot to message
                String answer = getReaction(msg);

                // url encode bot's response
                try {
                    msg.setText(URLEncoder.encode(answer.toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                // put bot's reply to outgoing queue
                queueController.getQueueFromBot().add(msg);
            }
        }
    }
}
