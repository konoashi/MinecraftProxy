package fr.konoashi.proxyprovider.database.access;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import fr.konoashi.proxyprovider.database.Connector;
import fr.konoashi.proxyprovider.database.document.AccountDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AccountAccess {

    private Connector connector;

    public AccountAccess(Connector connector) {
        this.connector = connector;
    }

    public AccountDocument getAccount(UUID uuid) {

        Bson filter = Filters.eq("uuid", uuid.toString());
        FindIterable<Document> documents = this.connector.getCollection().find(filter);

        MongoCursor<Document> cursor = documents.iterator();
        Document document = cursor.tryNext();

        if (document != null) {
            return new AccountDocument(document.getList("allowedUsers", ObjectId.class), document.getString("token"), UUID.fromString(document.get("uuid", String.class)));
        } else {
            return null;
        }
    }

    public void setAccount(AccountDocument accountDocument) {

        Document document = new Document();
        document.put("allowedUsers", accountDocument.getAllowedUsers());
        document.put("token", accountDocument.getToken());
        document.put("uuid", accountDocument.getUuid().toString());
        this.connector.getCollection().insertOne(document);

    }

    /*public void changeToken(UUID uuid, String token) {

        Bson filter = Filters.eq("uuid", uuid);
        FindIterable<Document> documents = getCollection().find(filter);

        MongoCursor<Document> cursor = documents.iterator();

        assert cursor.hasNext();
        cursor.tryNext().replace("token", token);
    }*/

    public void addAllowedUsers(UUID uuid, ObjectId allowedUser) {

        Bson filter = Filters.eq("uuid", uuid.toString());
        FindIterable<Document> documents = this.connector.getCollection().find(filter);

        MongoCursor<Document> cursor = documents.iterator();

        assert cursor.hasNext();
        List<ObjectId> allowedUsers = new ArrayList<>();
        Document document = cursor.tryNext();
        allowedUsers = document.getList("allowedUsers", ObjectId.class);
        allowedUsers.add(allowedUser);
        document.replace("allowedUsers", allowedUsers);
    }




}
