package com.oybek.bridgevk;

import com.oybek.bridgevk.Entities.Geo;
import com.oybek.bridgevk.Entities.Message;
import com.oybek.bridgevk.Entities.StopInfo;

import java.util.List;
import java.util.stream.Collectors;

public class Bot {
    private enum State { TRAM, WAIT_TROLL_APPROVE }

    private Ettu ettu;
    private State state = State.TRAM;
    private Geo lastGeo = null;

    //
    Bot(Ettu ettu) {
        this.ettu = ettu;
    }

    //
    public Message getReaction(Message msg) {
        switch (state) {
            default: {
                break;
            }

            case WAIT_TROLL_APPROVE: {
                state = State.TRAM;
                if (msg.getText() != null
                        && ( msg.getText().toLowerCase().equals("да") || msg.getText().toLowerCase().equals("lf") ) ) {
                    msg.setGeo(lastGeo);

                    // TODO: override clone method and work with clone
                    Message replyMsg = msg;

                    // get info about troll stop
                    List<StopInfo> stopInfos = ettu.getNearestTrollStops(msg.getGeo(), 2);

                    if (stopInfos == null) {
                        replyMsg.setText("Извините, не удалось найти информацию о троллейбусах 😞");
                        return replyMsg;
                    }

                    // provide information
                    replyMsg.setText(
                        stopInfos
                            .stream()
                            .map( stopInfo -> "🚎 Остановка: " + stopInfo.getTextInfo() )
                            .collect(Collectors.joining("\n"))
                    );

                    return replyMsg;
                } else {
                    return getReaction(msg);
                }
            }

            case TRAM: {
                // TODO: override clone method and work with clone
                Message replyMsg = msg;

                // no geolocation provided
                if (msg.getGeo() == null) {
                    replyMsg.setText("Для того чтобы я мог найти ближайшую остановку, отправьте мне свои координаты, вот как это делается:");
                    replyMsg.setAttachment("doc-163915852_464149858");
                    return replyMsg;
                }

                // get info about tram stop
                List<StopInfo> stopInfos = ettu.getNearestTramStops(msg.getGeo(), 2);

                if (stopInfos == null) {
                    replyMsg.setText("Извините, не удалось найти информацию о трамваях 😞");
                    return replyMsg;
                }

                // provide information
                replyMsg.setText(
                        stopInfos
                                .stream()
                                .map( stopInfo -> "🚋 Остановка: " + stopInfo.getTextInfo() )
                                .collect(Collectors.joining("\n"))
                );

                replyMsg.appendText("\nПоказать информацию по троллейбусам?");
                state = State.WAIT_TROLL_APPROVE;
                lastGeo = msg.getGeo();
                return replyMsg;
            }
        }

        return null;
    }
}
