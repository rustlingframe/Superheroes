package edu.miracosta.cs134.aespinoza.superheroes.model;
/**
 * Represents a Superhero for the purposes of the SuperheroQuiz, including the superhero's name,
 * superpower,onething and the file name (including path) for its image.
 *
 * @author Alvaro Espinoza
 * @version 1.0
 */
public class Superhero {

    private String mName;
    private String mSuperpower;
    private String mOneThing;
    private String mFileName;

    /**
     * Instantiates a new <code>SuperHero</code> given its name and region.
     * @param name The name of the <code>Country</code>
     * @param superPower The superpower of the <code>Superhero</code>
     * @param oneThing The oneThing of the <code>Superhero</>
     *
     */
    public Superhero(String name, String superPower,String oneThing,String fileName)
    {
        mName = name;
        mSuperpower = superPower;
        mOneThing = oneThing;
        mFileName = fileName;
    }

    /**
     * Gets the name of the <code>Superhero</code>.
     * @return The name of the <code>Superhero</code>
     */
    public String getName() {
        return mName;
    }
    public String getmName() {
        return mName;
    }

    /**
     * Sets the name of the <code>Superhero</code>
     */
    public void setmName(String mName) {
        this.mName = mName;
    }

    /**
     * Gets the superpower of the <code>Superhero</code>.
     * @return The superpower of the <code>Superhero</code>
     */
    public String getmSuperpower() {
        return mSuperpower;
    }

    /**
     * Sets the superpower of the <code>Superhero</code>.
     */
    public void setmSuperpower(String mSuperpower) {
        this.mSuperpower = mSuperpower;
    }

    /**
     * Gets the one thing of the <code>Superhero</code>.
     * @return The one thing of the <code>Superhero</code>
     */
    public String getmOneThing() {
        return mOneThing;
    }

    /**
     * Sets the one thing of the <code>Superhero</code>.
     */
    public void setmOneThing(String mOneThing) {
        this.mOneThing = mOneThing;
    }

    /**
     * Gets the file name of the <code>Superhero</code>.
     * @return The file name of the <code>Superhero</code>
     */
    public String getmFileName() {
        return mFileName;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    /**
     * Compares two Superheros for equality based on name and file name.
     * @param o The other superhero.
     * @return True if the countries are the same, false otherwise.
     */
    @Override
    public boolean equals(Object o)
    {
        if(this == o)return true;

        if(o == null|| getClass() != o.getClass()) return false;

        Superhero superhero = (Superhero)o;
        if(!mName.equals(superhero.mName))return false;
        return mFileName.equals(superhero.mFileName);

    }
    /**
     * Generates an integer based hash code to uniquely represent this <code>Superhero</code>.
     * @return An integer based hash code to represent this <code>Superhero</code>.
     */
    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mOneThing.hashCode();
        result = 31 * result + mSuperpower.hashCode();
        result = 31 * result + mFileName.hashCode();
        return result;
    }

    /**
     * Generates a text based representation of this <code>Superhero</code>.
     * @return A text based representation of this <code>Superhero</code>.
     */
    @Override
    public String toString() {
        return "Superhero{" +
                "Name='" + mName + '\'' +
                ", Superpower ='" + mSuperpower + '\'' +
                ", OneThing ='" + mOneThing + '\'' +
                ", FileName ='" + mFileName + '\'' +
                '}';
    }
}
