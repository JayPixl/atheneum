package jpxl.atheneum;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

import static net.minecraft.server.command.CommandManager.*;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jpxl.atheneum.blocks.ModBlocks;
import jpxl.atheneum.enchantments.ModEnchantments;
import jpxl.atheneum.util.EnchantLevelData;
import jpxl.atheneum.util.IEntityDataSaver;
import jpxl.atheneum.ench.table.NewEnchantmentScreenHandler;
import jpxl.atheneum.items.ModItems;
import jpxl.atheneum.loot.ApplyRandomTomeLevelLootFunction;
import jpxl.atheneum.loot.ModLoot;
import jpxl.atheneum.recipe.CombineTomeRecipe;
import jpxl.atheneum.config.AtheneumConfig;

import java.util.Collection;

public class Atheneum implements ModInitializer {
    public static final String MOD_ID = "atheneum";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static AtheneumConfig config;

    public static final ScreenHandlerType<NewEnchantmentScreenHandler> NEW_ENCHANTMENT_SCREEN_HANDLER;
    public static final LootFunctionType<ApplyRandomTomeLevelLootFunction> APPLY_RANDOM_TOME_LEVEL = Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier(MOD_ID, "apply_random_tome_level"), new LootFunctionType<>(ApplyRandomTomeLevelLootFunction.CODEC));
    public static final RecipeSerializer<CombineTomeRecipe> COMBINE_TOME_RECIPE_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(MOD_ID, "crafting_special_combine_tome"), new SpecialRecipeSerializer<CombineTomeRecipe>(CombineTomeRecipe::new));

    static {
        NEW_ENCHANTMENT_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "enchantment"), new ScreenHandlerType<NewEnchantmentScreenHandler>(NewEnchantmentScreenHandler::new, FeatureSet.empty()));
    }

    @Override
    public void onInitialize() {

        config = AtheneumConfig.loadConfig();
        config.saveConfig();

        ModEnchantments.registerEnchantments();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("atheneum")
                .then(literal("level").requires(source -> source.hasPermissionLevel(2))
                        .then(literal("set")
                                .then(argument("target", EntityArgumentType.players())
                                        .then(argument("value", IntegerArgumentType.integer(1, 15))
                                                .executes(context -> {
                                                    ServerCommandSource source = context.getSource();
                                                    Collection<ServerPlayerEntity> targetPlayers = EntityArgumentType.getPlayers(context, "target");

                                                    try {
                                                        for (ServerPlayerEntity player : targetPlayers) {
                                                            EnchantLevelData.setEnchantingLevel((IEntityDataSaver) player, IntegerArgumentType.getInteger(context, "value"));
                                                        }
                                                        source.sendFeedback(() -> Text.literal("Enchanting Levels set for players."), false);
                                                        return 1;
                                                    } catch (Exception e) {
                                                        source.sendFeedback(() -> Text.literal("Could not set Enchanting Levels."), false);
                                                        return 0;
                                                    }
                                                })
                                        )
                                )
                        )
                        .then(literal("add")
                                .then(argument("target", EntityArgumentType.players())
                                        .then(argument("value", IntegerArgumentType.integer(-15, 15))
                                                .executes(context -> {
                                                    ServerCommandSource source = context.getSource();
                                                    Collection<ServerPlayerEntity> targetPlayers = EntityArgumentType.getPlayers(context, "target");

                                                    try {
                                                        for (ServerPlayerEntity player : targetPlayers) {
                                                            EnchantLevelData.addEnchantingLevels((IEntityDataSaver) player, IntegerArgumentType.getInteger(context, "value"));
                                                        }
                                                        source.sendFeedback(() -> Text.literal(IntegerArgumentType.getInteger(context, "value") + " Enchanting Levels added for players."), false);
                                                        return 1;
                                                    } catch (Exception e) {
                                                        source.sendFeedback(() -> Text.literal("Could not add Enchanting Levels."), false);
                                                        return 0;
                                                    }
                                                })
                                        )
                                )
                        )
                )
                .then(literal("xp").requires(source -> source.hasPermissionLevel(2))
                        .then(literal("set")
                                .then(argument("target", EntityArgumentType.players())
                                        .then(argument("value", IntegerArgumentType.integer(1, 15))
                                                .executes(context -> {
                                                    ServerCommandSource source = context.getSource();
                                                    Collection<ServerPlayerEntity> targetPlayers = EntityArgumentType.getPlayers(context, "target");

                                                    try {
                                                        for (ServerPlayerEntity player : targetPlayers) {
                                                            EnchantLevelData.setEnchantingXp((IEntityDataSaver) player, IntegerArgumentType.getInteger(context, "value"));
                                                        }
                                                        source.sendFeedback(() -> Text.literal("Enchanting XP set for players."), false);
                                                        return 1;
                                                    } catch (Exception e) {
                                                        source.sendFeedback(() -> Text.literal("Could not set Enchanting XP."), false);
                                                        return 0;
                                                    }
                                                })
                                        )
                                )
                        )
                        .then(literal("add")
                                .then(argument("target", EntityArgumentType.players())
                                        .then(argument("value", IntegerArgumentType.integer(-100, 100))
                                                .executes(context -> {
                                                    ServerCommandSource source = context.getSource();
                                                    Collection<ServerPlayerEntity> targetPlayers = EntityArgumentType.getPlayers(context, "target");

                                                    try {
                                                        for (ServerPlayerEntity player : targetPlayers) {
                                                            EnchantLevelData.addEnchantingXp((IEntityDataSaver) player, IntegerArgumentType.getInteger(context, "value"));
                                                        }
                                                        source.sendFeedback(() -> Text.literal(IntegerArgumentType.getInteger(context, "value") + " Enchanting XP added for players."), false);
                                                        return 1;
                                                    } catch (Exception e) {
                                                        source.sendFeedback(() -> Text.literal("Could not add Enchanting XP."), false);
                                                        return 0;
                                                    }
                                                })
                                        )
                                )
                        )
                )
        ));

        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModLoot.registerLootTables();
    }
}