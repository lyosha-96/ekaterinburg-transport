package com.oybek.bridgevk.Entities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

public class Message {
    private long id;
    private long date;
    private long uid;
    private long readState;
    private String text;
    private Geo geo;
    private String attachment;

    public Message() {
    }

    public Message(long id, long date, long uid, long readState, String text, Geo geo, String attachment) {
        this.id = id;
        this.date = date;
        this.uid = uid;
        this.readState = readState;
        this.text = text;
        this.geo = geo;
        this.attachment = attachment;
    }

    public Message(JsonObject jObj) {
        if (jObj == null) {
            throw new JsonParseException("Null json object");
        } else if (!jObj.has("mid")) {
            throw new JsonParseException("There is no 'mid' field");
        } else if (!jObj.has("uid")) {
            throw new JsonParseException("There is no 'uid' field");
        } else if (!jObj.has("read_state")) {
            throw new JsonParseException("There is no 'read_state' field");
        }

        this.id = jObj.get("mid").getAsLong();
        this.uid = jObj.get("uid").getAsLong();
        this.date = jObj.get("date").getAsLong();
        this.readState = jObj.get("read_state").getAsLong();

        if (jObj.has("body")) {
            this.text = jObj.get("body").getAsString();
            if (this.text.length() > 30)
                this.text = this.text.substring(0, 30);
        } else {
            this.text = null;
        }

        if (jObj.has("geo")) {
            String coord = jObj.get("geo").getAsJsonObject().get("coordinates").getAsString();
            String[] splitted = coord.split("\\s+");
            this.geo = new Geo(Double.parseDouble(splitted[0]), Double.parseDouble(splitted[1]));
        } else {
            this.geo = null;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getUid() {
        return uid;
    }

    public Message setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public long getReadState() {
        return readState;
    }

    public void setReadState(long readState) {
        this.readState = readState;
    }

    public String getText() {
        return text;
    }

    public Message setText(String text) {
        this.text = text;
        return this;
    }

    public Boolean hasText() {
        return text != null;
    }

    public Message appendText(String text) {
        this.text = (this.text == null ? text : this.text.concat(text));
        return this;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public Boolean hasGeo() {
        return geo != null;
    }

    public String getAttachment() {
        return attachment;
    }

    public Message setAttachment(String attachment) {
        this.attachment = attachment;
        return this;
    }

    @Override
    public Message clone() {
        return new Message(
            id
            , date
            , uid
            , readState
            , text
            , geo
            , attachment
        );
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", date=" + date +
                ", uid=" + uid +
                ", readState=" + readState +
                ", text='" + text + '\'' +
                ", geo=" + ( geo == null ? geo : geo.toString() ) +
                '}';
    }
}
