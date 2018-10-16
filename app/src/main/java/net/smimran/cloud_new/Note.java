package net.smimran.cloud_new;

public class Note {
    String category,description,created_at,update_at;

    public Note() {
    }


    public Note(String category, String description, String created_at,String update_at) {
        this.category = category;
        this.description = description;
        this.created_at = created_at;
        this.update_at = update_at;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdate_at() {
        return update_at;
    }
}
