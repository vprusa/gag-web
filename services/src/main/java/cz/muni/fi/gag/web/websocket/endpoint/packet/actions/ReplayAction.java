package cz.muni.fi.gag.web.websocket.endpoint.packet.actions;

public class ReplayAction extends Action {

    protected Long gestureId;

    public Long getGestureId() {
        return gestureId;
    }

    public void setGestureId(Long gestureId) {
        this.gestureId = gestureId;
    }

}
