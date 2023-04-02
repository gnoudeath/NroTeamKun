package nro.main;

public class ServerMaintenance {
    private static boolean isUnderMaintenance = false;
    
    public static void setIsUnderMaintenance(boolean value) {
        isUnderMaintenance = value;
    }
    
    public static boolean isUnderMaintenance() {
        return isUnderMaintenance;
    }
}
