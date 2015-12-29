package space.ankan.popularmovies.data;

/**
 * Created by anurag on 20-Dec-15.
 */
public class Trailer {

    private String name;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }
}
