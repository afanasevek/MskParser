import java.util.ArrayList;
import java.util.List;

public class MskMetro {
    private String station;
    private String line;
    List<String>stations = new ArrayList<>();

    public String getStation() {
        return station;
    }

    public void setStations(String station){
        stations.add(station);
    }

    public void getList(){
        stations.forEach(System.out::println);
    }
    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
