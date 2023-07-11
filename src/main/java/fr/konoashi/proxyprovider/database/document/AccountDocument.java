package fr.konoashi.proxyprovider.database.document;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.UUID;

public class AccountDocument {

    private List<ObjectId> allowedUsers; //ObjectIds from the website users database that are allowed to use the minecraft account
    private String token;
    private UUID uuid;

    public AccountDocument(List<ObjectId> allowedUsers, String token, UUID uuid) {
        this.allowedUsers = allowedUsers;
        this.token = token;
        this.uuid = uuid;
    }

    public List<ObjectId> getAllowedUsers() {
        return allowedUsers;
    }

    public void setAllowedUsers(List<ObjectId> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void addAllowedUser(ObjectId allowedUser) {
        this.allowedUsers.add(allowedUser);
    }

    public void removeAllowedUser(ObjectId allowedUser) {
        this.allowedUsers.remove(allowedUser);
    }
}
