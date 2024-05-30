package jpxl.atheneum.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.annotation.Serializer;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;
import jpxl.atheneum.Atheneum;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class AtheneumConfig {

    public List<LibraryComponent> libraryComponents = new ArrayList<>(List.of(
            new LibraryComponent(new HashSet<>(Set.of("minecraft:bookshelf")), new LibraryBonuses().put(TableBonuses.maxPower, 1), ComponentCategories.bookshelf),

            new LibraryComponent(new HashSet<>(Set.of("minecraft:lantern")), new LibraryBonuses().put(TableBonuses.insight, 3), ComponentCategories.artifact),
            new LibraryComponent(new HashSet<>(Set.of("minecraft:soul_lantern")), new LibraryBonuses().put(TableBonuses.insight, 7), ComponentCategories.artifact),
            new LibraryComponent(new HashSet<>(Set.of("minecraft:wither_skeleton_skull", "minecraft:wither_skeleton_wall_skull")), new LibraryBonuses().put(TableBonuses.chaos, 12), ComponentCategories.artifact),
            new LibraryComponent(new HashSet<>(Set.of("minecraft:candle", "minecraft:white_candle", "minecraft:orange_candle", "minecraft:magenta_candle", "minecraft:light_blue_candle", "minecraft:yellow_candle", "minecraft:lime_candle", "minecraft:pink_candle", "minecraft:gray_candle", "minecraft:ligt_gray_candle", "minecraft:cyan_candle", "minecraft:purple_candle", "minecraft:blue_candle", "minecraft:brown_candle", "minecraft:green_candle", "minecraft:red_candle", "minecraft:black_candle")), new LibraryBonuses().put(TableBonuses.synergy, 3), ComponentCategories.artifact),

            new LibraryComponent(new HashSet<>(Set.of("atheneum:oak_tomeshelf")), ComponentTypes.data, new LibraryBonuses(), ComponentCategories.tomeshelf)
    ));

    public List<TomeBonus> tomeBonuses = new ArrayList<>(List.of(
            new TomeBonus(new HashSet<>(Set.of("atheneum:ancient_tome")), new LibraryBonuses().put(TableBonuses.power, 3)),
            new TomeBonus(new HashSet<>(Set.of("atheneum:dreamers_tome")), new LibraryBonuses().put(TableBonuses.insight, 3)),
            new TomeBonus(new HashSet<>(Set.of("atheneum:verdant_tome")), new LibraryBonuses().put(TableBonuses.synergy, 3)),
            new TomeBonus(new HashSet<>(Set.of("atheneum:shadowborne_tome")), new LibraryBonuses().put(TableBonuses.chaos, 3))
    ));

    public List<Catalyst> catalysts = new ArrayList<>(List.of(
            new Catalyst(new HashSet<>(Set.of("minecraft:lapis_lazuli")), new LibraryBonuses()),
            new Catalyst(new HashSet<>(Set.of("atheneum:lapis_tenebris")), new LibraryBonuses().put(TableBonuses.canCurselift, true))
    ));

    public static class TomeBonus {

        public Set<String> ids;

        public LibraryBonuses bonusesPerLevel;

        public TomeBonus() {
        }

        public TomeBonus(Set<String> ids, LibraryBonuses bonuses) {
            this.ids = ids;
            this.bonusesPerLevel = bonuses;
        }
    }

    public static class Catalyst {

        public Set<String> ids;

        public LibraryBonuses bonuses;

        public Catalyst() {
        }

        public Catalyst(Set<String> ids, LibraryBonuses bonuses) {
            this.ids = ids;
            this.bonuses = bonuses;
        }
    }

    public static class LibraryComponent {
        public Set<String> ids;

        public ComponentTypes type;

        public ComponentCategories category;

        public LibraryBonuses bonuses;

        public LibraryComponent() {
        }

        public LibraryComponent(Set<String> ids, ComponentTypes type) {
            this(ids, type, new LibraryBonuses(), ComponentCategories.artifact);
        }

        public LibraryComponent(Set<String> ids, LibraryBonuses bonuses, ComponentCategories category) {
            this(ids, ComponentTypes.simple, bonuses, category);
        }

        public LibraryComponent(Set<String> ids, ComponentTypes type, LibraryBonuses bonuses, ComponentCategories category) {
            this.ids = ids;
            this.type = type;
            this.bonuses = bonuses;
            this.category = category;
        }
    }

    public static class LibraryBonuses implements Cloneable {
        public int power = 0;
        public int maxPower = 0;
        public int insight = 0;
        public int maxInsight = 0;
        public int synergy = 0;
        public int maxSynergy = 0;
        public int chaos = 0;
        public int minQuantity = 0;
        public int rarityBonus = 0;
        public boolean allowTreasure = false;
        public boolean canCurselift = false;

        public LibraryBonuses() {
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public LibraryBonuses put(TableBonuses bonusType, int level) {
            switch (bonusType) {
                case power -> power = level;
                case maxPower -> maxPower = level;
                case insight -> insight = level;
                case maxInsight -> maxInsight = level;
                case synergy -> synergy = level;
                case maxSynergy -> maxSynergy = level;
                case chaos -> chaos = level;
                case minQuantity -> minQuantity = level;
                case rarityBonus -> rarityBonus = level;
            }
            return this;
        }

        public LibraryBonuses multiply(int factor) throws CloneNotSupportedException {
            return ((LibraryBonuses) this.clone()).doMultiply(factor);
        }

        private LibraryBonuses doMultiply(int factor) {
            power *= factor;
            maxPower *= factor;
            insight *= factor;
            maxInsight *= factor;
            synergy *= factor;
            maxSynergy *= factor;
            chaos *= factor;
            minQuantity *= factor;
            rarityBonus *= factor;

            return this;
        }

        public LibraryBonuses put(TableBonuses bonusType, boolean bool) {
            if (bonusType == TableBonuses.allowTreasure) {
                allowTreasure = bool;
            } else if (bonusType == TableBonuses.canCurselift) {
                canCurselift = bool;
            }
            return this;
        }

        public boolean isActive() {
            return this.power != 0 ||
                    this.maxPower != 0 ||
                    this.insight != 0 ||
                    this.maxInsight != 0 ||
                    this.synergy != 0 ||
                    this.maxSynergy != 0 ||
                    this.rarityBonus != 0 ||
                    this.minQuantity != 0 ||
                    this.chaos != 0 ||
                    this.canCurselift ||
                    this.allowTreasure;
        }

        public int totalBonus() {
            return this.power +
                    this.maxPower +
                    this.insight +
                    this.maxInsight +
                    this.synergy +
                    this.maxSynergy +
                    this.rarityBonus +
                    this.minQuantity +
                    this.chaos +
                    (this.canCurselift ? 3 : 0) +
                    (this.allowTreasure ? 3 : 0);
        }

        @Serializer
        public JsonElement toJson() {
            JsonObject json = new JsonObject();

            if (power != 0) json.put("power", new JsonPrimitive(power));
            if (maxPower != 0) json.put("maxPower", new JsonPrimitive(maxPower));
            if (insight != 0) json.put("insight", new JsonPrimitive(insight));
            if (maxInsight != 0) json.put("maxInsight", new JsonPrimitive(maxInsight));
            if (synergy != 0) json.put("synergy", new JsonPrimitive(synergy));
            if (maxSynergy != 0) json.put("maxSynergy", new JsonPrimitive(maxSynergy));
            if (chaos != 0) json.put("chaos", new JsonPrimitive(chaos));
            if (minQuantity != 0) json.put("minQuantity", new JsonPrimitive(minQuantity));
            if (rarityBonus != 0) json.put("rarityBonus", new JsonPrimitive(rarityBonus));
            if (allowTreasure) json.put("allowTreasure", new JsonPrimitive(allowTreasure));
            if (canCurselift) json.put("canCurselift", new JsonPrimitive(canCurselift));

            return json;
        }
    }

    public enum TableBonuses {
        power,
        maxPower,
        insight,
        maxInsight,
        synergy,
        maxSynergy,
        chaos,
        minQuantity,
        rarityBonus,
        allowTreasure,
        canCurselift;
    }

    public enum ComponentTypes {
        simple,
        data;
    }

    public enum ComponentCategories {
        bookshelf,
        tomeshelf,
        artifact;
    }


    public static AtheneumConfig loadConfig() {
        // Create a new Jankson instance
        // (This can also be a static instance, defined outside the function)
        var jankson = Jankson.builder()
                // You can register adapters here to customize deserialization
                //.registerTypeAdapter(...)
                // Likewise, you can customize serializer behavior
                //.registerSerializer(...)
                // In most cases, the default Jankson is all you need.
                .build();
        // Parse the config file into a JSON Object
        try {
            File configFile = new File(FabricLoader.getInstance().getConfigDir().resolve(Atheneum.MOD_ID + ".json5").toString());
            JsonObject configJson = jankson.load(configFile);
            // Convert the raw object into your POJO type
            var normalized = configJson.toJson(false, false);
            //atheneumConfig config = jankson.fromJsonCarefully(configJson, atheneumConfig.class);
            AtheneumConfig config = new Gson()
                    // Use it to convert the string to an instance of your POJO
                    .fromJson(normalized, AtheneumConfig.class);
            //System.out.println(config.list.get(0));
            return config;
        } catch (IOException | SyntaxError e) {
            e.printStackTrace();
            if (e instanceof IOException) {
                return new AtheneumConfig(); // You could also throw a RuntimeException instead
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public File saveConfig() {
        var configFile = new File(FabricLoader.getInstance().getConfigDir().resolve(Atheneum.MOD_ID + ".json5").toString());
        var jankson = Jankson.builder().build();
        var result = jankson
                .toJson(this)        // The first call makes a JsonObject
                .toJson(true, true); // The second turns the JsonObject into a String

        try {
            var fileIsUsable = configFile.exists() || configFile.createNewFile();
            if (!fileIsUsable) return null;
            var out = new FileOutputStream(configFile, false);

            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return configFile;
    }

    public AtheneumConfig() {
    }
}
