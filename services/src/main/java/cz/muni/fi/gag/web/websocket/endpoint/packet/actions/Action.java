package cz.muni.fi.gag.web.websocket.endpoint.packet.actions;

import cz.muni.fi.gag.web.websocket.endpoint.packet.WSMsgBase;

public class Action implements WSMsgBase {
    //extends BaseEntity {


    public enum ActionsTypesEnum {
        PLAYER
    }

    protected ActionsTypesEnum type;

    public ActionsTypesEnum getType() {
        return type;
    }

    public void setType(Object type) {
        if(type instanceof Integer){
            this.type = ActionsTypesEnum.values()[(Integer) type];
        }else if(type instanceof String){
            this.type = ActionsTypesEnum.valueOf((String) type);
        }
    }
}
