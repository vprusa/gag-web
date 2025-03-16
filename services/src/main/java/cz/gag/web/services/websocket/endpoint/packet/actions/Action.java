package cz.gag.web.services.websocket.endpoint.packet.actions;

import cz.gag.web.services.websocket.endpoint.packet.WSMsgBase;

/**
 * {@link RecognitionActions}
 * {@link PlayerActions}
 */
public class Action implements WSMsgBase {

    public enum ActionsTypesEnum {
        PLAYER, RECOGNITION
    }

    public Action(ActionsTypesEnum type) {
        this.type = type;
    }

    protected ActionsTypesEnum type;

    public ActionsTypesEnum getType() {
        return type;
    }

    public void setType(Object type) {
        if(type instanceof ActionsTypesEnum){
            this.type = (ActionsTypesEnum) type;
        } else if(type instanceof Integer){
            this.type = ActionsTypesEnum.values()[(Integer) type];
        }else if(type instanceof String){
            this.type = ActionsTypesEnum.valueOf((String) type);
        }
    }

    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                '}';
    }
}
