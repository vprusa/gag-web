package cz.muni.fi.gag.web.websocket.endpoint.packet.actions;

import cz.muni.fi.gag.web.websocket.endpoint.packet.WSMsgBase;

public class Action implements WSMsgBase {
    //extends BaseEntity {
    protected String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
