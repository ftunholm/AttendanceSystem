package data;

import entities.Classes;
import util.Event;

import java.util.ArrayList;

/**
 * Created by LogiX on 2015-12-29.
 */
public interface ClassDAO {
    ArrayList<Classes> fetchAllClasses();
    Classes fetchClassById(int id);
    Event createClass(Classes klass);
    Event deleteClass(Classes klass);
}
