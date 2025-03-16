package cz.gag.web.services.websocket.endpoint.packet.actions;

/**
 * @author Vojtech Prusa
 */
public class ActionDecoder<T extends Action> extends ActionDecoderBase<T> {

    public ActionDecoder(final Class<T> actionClass, final Action.ActionsTypesEnum typeEnum) {
        super(actionClass, typeEnum);
    }

    public ActionDecoder(Class actionClass) {
        super(actionClass, null);
    }

}