package camilog.adminapp.elections;

/**
 * Created by stefano on 02-09-15.
 */
public class Candidate {
    /**
     * Class wrapper for candidates
     */

    private String _name;

    public Candidate(String name){
        _name = name;
    }
    public String toString(){return _name;}
    public String getName(){return _name;}
}
