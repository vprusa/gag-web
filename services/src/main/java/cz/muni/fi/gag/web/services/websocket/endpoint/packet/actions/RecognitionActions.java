package cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions;

public class RecognitionActions extends Action {

    public RecognitionActions() {
        super(ActionsTypesEnum.RECOGNITION);
    }

    public enum ActionsEnum {
        START, PAUSE, CONTINUE, STOP
    }

    protected ActionsEnum action;

    public ActionsEnum getAction() {
        return action;
    }

    public void setAction(Object action) {
        if(action instanceof RecognitionActions.ActionsEnum){
            this.action = (RecognitionActions.ActionsEnum) action;
        } else if (action instanceof Integer) {
            this.action = ActionsEnum.values()[(Integer) action];
        } else if (action instanceof String) {
            this.action = ActionsEnum.valueOf((String) action);
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                "RecognitionActions{" +
                "action=" + action +
                '}';
    }
}
