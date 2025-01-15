/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.remote;

import bisq.remote.notification.recipient.PairingToken;
import bisq.remote.notification.recipient.Recipient;
import bisq.remote.notification.recipient.RecipientFactory;
import com.google.gson.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.tools.Platform;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Getter
@Slf4j
public class AppData {
    private static final String APP_DATA_FILENAME = "application.data";
    private static final Gson GSON;

    private static AppData instance;

    private final List<Recipient> recipients = new ArrayList<>();

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Recipient.class, new RecipientSerialization());
        GSON = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();
    }

    public static synchronized AppData getInstance() {
        if (instance == null) {
            instance = loadFromFile();
        }
        return instance;
    }

    public void addRecipient(@NotNull final Recipient recipient) {
        this.recipients.add(recipient);
        writeToFile();
    }

    public void removeRecipients(@NotNull final List<Recipient> recipients) {
        this.recipients.removeAll(recipients);
        writeToFile();
    }

    private static AppData loadFromFile() {
        final File configFile = new File(APP_DATA_FILENAME);

        if (configFile.exists()) {
            try (Reader reader = new FileReader(configFile)) {
                final AppData configuration = GSON.fromJson(reader, AppData.class);

                if (configuration != null) {
                    return configuration;
                }
            } catch (IOException | JsonSyntaxException e) {
                log.error("Unable to load config from file: " + configFile.getAbsolutePath(), e);
            }
        }

        return new AppData();
    }

    private synchronized void writeToFile() {
        final File configFile = new File(APP_DATA_FILENAME);

        try (Writer writer = new FileWriter(configFile)) {
            if (!configFile.exists()) {
                FileUtil.createOwnerOnlyFile(configFile);
            }
            GSON.toJson(this, writer);
        } catch (IOException e) {
            log.error("Unable to write config to file: " + configFile.getAbsolutePath(), e);
        }
    }

    private static class RecipientSerialization implements JsonSerializer<Recipient>, JsonDeserializer<Recipient> {
        @Override
        public JsonElement serialize(Recipient recipient, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("pairingToken", recipient.getPairingToken().asString());
            return jsonObject;
        }

        @Override
        public Recipient deserialize(
                JsonElement jsonElement,
                Type type,
                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new RecipientFactory().createFromPairingToken(
                    PairingToken.fromString(jsonElement.getAsJsonObject().get("pairingToken").getAsString()));
        }
    }

    private static class FileUtil {
        private FileUtil() {
            throw new AssertionError("Utility class should not be instantiated");
        }

        public static void createOwnerOnlyFile(@NotNull final File file) throws IOException {
            if (Platform.getCurrent() == Platform.WINDOWS) {
                Files.createFile(file.toPath());
                return;
            }
            Files.createFile(file.toPath(), PosixFilePermissions.asFileAttribute(getFileOwnerOnlyPosixFilePermissions()));
        }

        private static Set<PosixFilePermission> getFileOwnerOnlyPosixFilePermissions() {
            Set<PosixFilePermission> ownerOnly = EnumSet.noneOf(PosixFilePermission.class);
            ownerOnly.add(PosixFilePermission.OWNER_READ);
            ownerOnly.add(PosixFilePermission.OWNER_WRITE);
            return ownerOnly;
        }
    }
}
