package cz.muni.fi.gag.web.websocket.endpoint.packet.actions;

public class PlayerActions extends Action {

    public enum ActionsEnum {
        PLAY, PAUSE, CONTINUE, STOP
    }

    protected ActionsEnum action;

    protected Long gestureId;

    public Long getGestureId() {
        return gestureId;
    }

    public void setGestureId(Long gestureId) {
        this.gestureId = gestureId;
    }

    public ActionsEnum getAction() {
        return action;
    }

    public void setAction(Object action) {
        if(action instanceof Integer){
            this.action = ActionsEnum.values()[(Integer) action];
        }else if(action instanceof String){
            this.action = ActionsEnum.valueOf((String) action);
        }
    }

}
