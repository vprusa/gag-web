package cz.muni.fi.gag.app.model;

import java.util.Date;

/**
 * @author Nobody Nobody
 *
 */
public class Message {

    private String name;
    private String text;
    private Date date;

    private Message() {
    }

    public Message(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

}
