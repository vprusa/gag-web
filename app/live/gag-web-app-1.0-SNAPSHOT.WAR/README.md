## Notes

### WS onMessage
How does the onMessage works?
in WSTools
```
  ws.setOnMessage = function (f) {
    ws.websocketSession.onmessage = f;
  };

  ws.onSendMessage = function (data) {
    // override
  };
```
### WS sendMessag
To send a json message to server use
```
WSTools.sendMessag(msg);
```
Then WSTools will deal with distributing ot data to appropriate components (BLETools, VisTools)

TODO:
- WSTools does not have implemented handling for multiple onSendMessage (onSendMessage->onSentMessage)
- onReceivedMessage ?

