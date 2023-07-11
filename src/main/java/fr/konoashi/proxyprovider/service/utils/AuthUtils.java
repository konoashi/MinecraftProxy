package fr.konoashi.proxyprovider.service.utils;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AuthUtils {

    private static final URI SESSION_INFORMATION = URI.create("https://sessionserver.mojang.com/session/minecraft/profile/");
    private static final URI UUID_INFORMATION = URI.create("https://api.mojang.com/users/profiles/minecraft/");

    private static final HttpClient httpClient = HttpClient.newBuilder().build();

    private AuthUtils() {}


    public static UUID parseMojangUuid(String uuid) {
        if(uuid.length() != 32)
            throw new IllegalStateException("Unexpected length: " + uuid.length() + " (should be 34)");

        return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" +
                uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
    }

    public static String minifyUuid(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static String calculateServerHash(String serverId, PublicKey publicKey, SecretKey secretKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(serverId.getBytes(StandardCharsets.ISO_8859_1));
            digest.update(secretKey.getEncoded());
            digest.update(publicKey.getEncoded());
            return new BigInteger(digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("SHA-1 hashing algorithm not available", ex);
        }
    }



    public static CompletableFuture<String> nameFromUuid(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SESSION_INFORMATION + uuid.toString()))
                    .build();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject json = JsonParser.object().from(response.body());

                return json.getString("name");
            } catch (IOException | InterruptedException | JsonParserException ex) {
                ex.getStackTrace();
            }
            return null;
        });
    }

    public static CompletableFuture<UUID> uuidFromName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(UUID_INFORMATION + name))
                    .build();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject json = JsonParser.object().from(response.body());

                return parseMojangUuid(json.getString("id"));
            } catch (IOException | InterruptedException | JsonParserException ex) {
                ex.getStackTrace();
            }
            return null;
        });
    }

}
