package entities;

import java.sql.Date;

/**
 * Created by LogiX on 2015-11-19.
 */
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int level;
    private String personNumber;
    private int gender;
    private String image;
    private String address;
    private String phoneNumber;
    private int classId;
    private Date registerDate;
    private boolean active;

    public User() {

    }

    public User(int id, String firstName, String lastName, String email, String password, int level, String personNumber, int gender, String image, String address, String phoneNumber, int classId, Date registerDate, boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.level = level;
        this.personNumber = personNumber;
        this.gender = gender;
        this.image = image;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.classId = classId;
        this.registerDate = registerDate;
        this.active = active;
    }

    public User(String firstName, String lastName, String email, String password, int level, String personNumber, int gender, String image, String address, String phoneNumber, int classId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.level = level;
        this.personNumber = personNumber;
        this.gender = gender;
        this.image = image;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.classId = classId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
