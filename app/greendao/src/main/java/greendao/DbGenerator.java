package greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class DbGenerator {

    /**
     * Current database version.
     * Increment this when making changes to the database.
     * Make sure to alter the table accordingly in the DbOpenHelper.onUpgrade() method
     */
    public static final int DB_VERSION = 1;

    /**
     * The java root path where the package will be generated.
     */
    public static final String DB_PATH = "app/todo/src/main/java";

    /**
     * The package structure in which the code will be generated.
     */
    public static final String DB_PACKAGE = "com.example.todo.db.beans";

    public static void main(String[] args) throws Exception {

        // Creating the schema
        // This is the "root" model class to which we can add entities to
        Schema schema = new Schema(DB_VERSION, DB_PACKAGE);

        // Creating entities
        // An entity is basically the template for a DAO (Data access object)
        // Each entity will have its own SQLite table in the database
        // A DAO will be generated from each entity with the same name
        Entity section = schema.addEntity("Section");
        Entity toDoItem = schema.addEntity("ToDoItem");

        // Certain things can be configured, such different table / dao class names for entities. Eg:
        // toDoItem.setClassNameDao("ToDoItemDAO");
        // toDoItem.setTableName("todo_items");

        // Configuring entities
        // Each property will become a table column for that entity && Data access object field with its own getter / setter
        section.addIdProperty();
        section.addIntProperty("position");
        section.addStringProperty("name");
        section.addBooleanProperty("isSelected");

        toDoItem.addIdProperty();
        toDoItem.addIntProperty("position");
        toDoItem.addStringProperty("title");
        toDoItem.addStringProperty("description");

        Property sectionId = toDoItem.addLongProperty("sectionId").notNull().getProperty();

        // Special modifiers can also be added (Below is an example to showcase modifiers, not a valid case)
        // section.addProperty("someProperty").unique().autoincrement().primaryKey().notNull()

        // Adding etra relations
        toDoItem.addToOne(section, sectionId); // Each toDoItem will have a getSection() method that will return the section to which it belongs
        section.addToMany(toDoItem, sectionId, "toDoItems"); // Each section will have a getToDoItems() method that will return a list with all its toDoItems

        // Running this will generate all DAO's under the provided path
        // Run this by right clicking in this class and selecting Run DbGenerator.main() in Android Studio
        new DaoGenerator().generateAll(schema, DB_PATH);
    }
}
