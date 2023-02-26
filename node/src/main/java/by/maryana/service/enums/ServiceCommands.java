package by.maryana.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String cmd;

    ServiceCommands(String cmd) {
        this.cmd = cmd;
    }

    public static ServiceCommands fromValue(String otherCmd) {
        for(ServiceCommands s :ServiceCommands.values()){
            if(s.cmd.equals(otherCmd)){
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        return cmd;
    }

}
