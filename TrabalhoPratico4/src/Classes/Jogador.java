package Classes;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Jogador{
    private boolean lapide;
    private int id;
    private String knownAs;
    private String fullName;
    private byte overall;
    private double value;
    private String bestPosition;
    private String nacionality;
    private byte age;
    private String clubName;
    private Date joinedOn;


    public Jogador(){
        this.lapide = true;
        this.id = -1;
        this.knownAs = "";
        this.fullName = "";
        this.overall = 0;
        this.value = 0;
        this.bestPosition = "";
        this.nacionality = "";
        this.age = 0;
        this.clubName = "";
        //this.joinedOn = null;
    }

    public Jogador(/*boolean lapide,*/ int id, String knownAs, String fullName, byte overall, double value, String bestPosition, String nacionality, byte age, String clubName, String joinedOn) throws Exception{
        //this.lapide = lapide;
        this.id = id;
        this.knownAs = knownAs;
        this.fullName = fullName;
        this.overall = overall;
        this.value = value;
        this.bestPosition = bestPosition;
        this.nacionality = nacionality;
        this.age = age;
        this.clubName = clubName;
        //this.joinedOn = stringToDate(joinedOn); 
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getKnownAs(){
        return this.knownAs;
    }

    public void setKnownAs(String knownAs){
        this.knownAs = knownAs;
    }

    public String getFullName(){
        return this.fullName;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public byte getOverall(){
        return this.overall;
    }

    public void setOverall(byte overall){
        this.overall = overall;
    }

    public double getValue(){
        return this.value;
    }

    public void setValue(double value){
        this.value = value;
    }

    public String getBestPosition(){
        return this.bestPosition;
    }

    public void setBestPosition(String bestPosition){
        this.bestPosition = bestPosition;
    }

    public String getNacionality(){
        return this.nacionality;
    }

    public void setNacionality(String nacionality){
        this.nacionality = nacionality;
    }

    public byte getAge(){
        return this.age;
    }

    public void setAge(byte age){
        this.age = age;
    }

    public String getClubName(){
        return this.clubName;
    }

    public void setClubName(String clubName){
        this.clubName = clubName;
    }

    public boolean getLapide(){
        return this.lapide;
    }

    public void setLapide(boolean lapide){
        this.lapide = lapide;
    }

    public Date getJoinedOn(){
        return joinedOn;
    }

    public void setJoinedOn(String joinedOn) throws Exception{
        Date date = stringToDate(joinedOn);
        this.joinedOn = date;
    }

    public Date stringToDate(String data) throws Exception{
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        date = sdf.parse(data);
        return date;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String string = sdf.format(date);
        return string;
    }

    public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeBoolean(lapide);
        dos.writeInt(id);
        dos.writeInt(knownAs.length());
        dos.writeUTF(knownAs);
        dos.writeInt(fullName.length());
        dos.writeUTF(fullName);
        dos.writeByte(overall);
        dos.writeDouble(value);
        dos.writeInt(bestPosition.length());
        dos.writeUTF(bestPosition);
        dos.writeInt(nacionality.length());
        dos.writeUTF(nacionality);
        dos.writeByte(age);
        dos.writeInt(clubName.length());
        dos.writeUTF(clubName);
        dos.writeInt(dateToString(joinedOn).length());
        dos.writeUTF(dateToString(joinedOn));


        return out.toByteArray();
    }
}