package joe;

public class ModuleResponse {
    private String message = null;
    private long event_request = 0;

    public ModuleResponse(String message) {
        this.message = message;
    }

    public ModuleResponse(long millis) {
        event_request = millis;
    }

    public ModuleResponse(String message, long millis) {
        this.message = message;
        event_request = millis;
    }

    public String response() {
        return message;
    }

    public long eventRequest() {
        return event_request;
    }
}
