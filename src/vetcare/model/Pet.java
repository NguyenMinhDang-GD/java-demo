package vetcare.model;

import java.io.Serializable;

public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int ownerId;
    private String name;
    private String species;
    private String breed;
    private int age;
    private String gender;

    public Pet(int id, int ownerId, String name, String species, String breed, int age, String gender) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
    }

    public int getId() { return id; }
    public int getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public int getAge() { return age; }
    public String getGender() { return gender; }

    @Override
    public String toString() {
        return name + " (" + species + (breed != null && !breed.isEmpty() ? " - " + breed : "") + ")";
    }
}
