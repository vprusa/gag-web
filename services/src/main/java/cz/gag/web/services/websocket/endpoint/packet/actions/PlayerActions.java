package cz.gag.web.services.websocket.endpoint.packet.actions;

public class PlayerActions extends Action {

    public PlayerActions() {
        super(ActionsTypesEnum.PLAYER);
    }

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
        if(action instanceof ActionsEnum){
            this.action = (ActionsEnum) action;
        } else if(action instanceof Integer){
            this.action = ActionsEnum.values()[(Integer) action];
        }else if(action instanceof String){
            this.action = ActionsEnum.valueOf((String) action);
        }
    }

    @Override
    public String toString() {
        return super.toString() + "PlayerActions{" +
                "action=" + action +
                ", gestureId=" + gestureId +
                '}';
    }
}
