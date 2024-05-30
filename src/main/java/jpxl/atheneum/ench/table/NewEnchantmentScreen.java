package jpxl.atheneum.ench.table;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewEnchantmentScreen extends HandledScreen<NewEnchantmentScreenHandler> {
    private static final Identifier[] LEVEL_TEXTURES = new Identifier[]{new Identifier("container/enchanting_table/level_1"), new Identifier("container/enchanting_table/level_2"), new Identifier("container/enchanting_table/level_3")};
    private static final Identifier[] LEVEL_DISABLED_TEXTURES = new Identifier[]{new Identifier("container/enchanting_table/level_1_disabled"), new Identifier("container/enchanting_table/level_2_disabled"), new Identifier("container/enchanting_table/level_3_disabled")};
    private static final Identifier ENCHANTMENT_SLOT_DISABLED_TEXTURE = new Identifier("container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE = new Identifier("container/enchanting_table/enchantment_slot_highlighted");
    private static final Identifier ENCHANTMENT_SLOT_TEXTURE = new Identifier("container/enchanting_table/enchantment_slot");
    private static final Identifier TEXTURE = new Identifier("atheneum:textures/gui/enchanting_table.png");
    private static final Identifier BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
    private final Random random = Random.create();
    private BookModel BOOK_MODEL;
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack = ItemStack.EMPTY;
    private int indexStartOffset = 0;
    private boolean scrolling;

    public NewEnchantmentScreen(NewEnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 197;
        this.playerInventoryTitleY = 104;
    }

    @Override
    protected void init() {
        super.init();
        this.BOOK_MODEL = new BookModel(this.client.getEntityModelLoader().getModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        this.doTick();
    }


    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawBook(context, i, j, delta);
    }

    private void drawBook(DrawContext context, int x, int y, float delta) {
        x = x + 3;
        float f = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        float g = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle);
        DiffuseLighting.method_34742();
        context.getMatrices().push();
        context.getMatrices().translate((float) x + 33.0f, (float) y + 31.0f, 100.0f);
        float h = 40.0f;
        context.getMatrices().scale(-40.0f, 40.0f, 40.0f);
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(25.0f));
        context.getMatrices().translate((1.0f - f) * 0.2f, (1.0f - f) * 0.1f, (1.0f - f) * 0.25f);
        float i = -(1.0f - f) * 90.0f - 90.0f;
        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i));
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
        float j = MathHelper.clamp(MathHelper.fractionalPart(g + 0.25f) * 1.6f - 0.3f, 0.0f, 1.0f);
        float k = MathHelper.clamp(MathHelper.fractionalPart(g + 0.75f) * 1.6f - 0.3f, 0.0f, 1.0f);
        this.BOOK_MODEL.setPageAngles(0.0f, j, k, f);
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(this.BOOK_MODEL.getLayer(BOOK_TEXTURE));
        this.BOOK_MODEL.render(context.getMatrices(), vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        context.draw();
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    private void renderScrollbar(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int i = (int) Arrays.stream(handler.clueAccuracy).filter(n -> n != -1).count();
        int j = (this.height - this.backgroundHeight) / 2;
        if (i > 3) {
            int m = Math.min(17, (indexStartOffset * 17) / (i - 3));
            if (mouseX > (double) (i + 160) && mouseX < (double) (i + 169) && mouseY > (double) (j + 19) && mouseY < (double) (j + 50)) {
                context.drawTexture(TEXTURE, x + 160, y + 19 + m, 42, 243, 8, 13);
            } else {
                context.drawTexture(TEXTURE, x + 160, y + 19 + m, 26, 243, 8, 13);
            }
        } else {
            context.drawTexture(TEXTURE, x + 160, y + 19, 34, 243, 8, 13);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        delta = this.client.getTickDelta();
        super.render(context, mouseX, mouseY, delta);
        //boolean bl = this.client.player.getAbilities().creativeMode;

        int q = (this.width - this.backgroundWidth) / 2;
        int r = (this.height - this.backgroundHeight) / 2;
        int k = r + 19;

        EnchantingPhrases.getInstance().setSeed(this.handler.getSeed());

        // get all enchantment outputs
        List<EnchantmentClueEntry> enchantments = new ArrayList<>(10);
        int c = 0;
        for (int enchantmentId : ((NewEnchantmentScreenHandler) this.handler).outputIds) {
            if (handler.clueAccuracy[c] != -1) {
                enchantments.add(new EnchantmentClueEntry(enchantmentId < 0 ? null : Enchantment.byRawId(enchantmentId), handler.outputLevels[c], handler.clueAccuracy[c]));
                c++;
            } else break;
        }

        this.renderScrollbar(context, q, r, mouseX, mouseY);

        // max power
        context.drawTexture(TEXTURE, q + 19, r + 76, 1, 213, ((NewEnchantmentScreenHandler) this.handler).maxPower * 150 / 100, 4);
        // max stability
        context.drawTexture(TEXTURE, q + 19, r + 86, 1, 218, ((NewEnchantmentScreenHandler) this.handler).maxSynergy * 150 / 100, 4);
        // max insight
        context.drawTexture(TEXTURE, q + 19, r + 96, 1, 223, ((NewEnchantmentScreenHandler) this.handler).maxInsight * 150 / 100, 4);

        // power
        context.drawTexture(TEXTURE, q + 19, r + 76, 1, 198, ((NewEnchantmentScreenHandler) this.handler).power * 150 / 100, 4);
        // stability
        context.drawTexture(TEXTURE, q + 19, r + 86, 1, 203, ((NewEnchantmentScreenHandler) this.handler).synergy * 150 / 100, 4);
        // insight
        context.drawTexture(TEXTURE, q + 19, r + 96, 1, 208, ((NewEnchantmentScreenHandler) this.handler).insight * 150 / 100, 4);

        // Enchant Level
        context.drawText(this.textRenderer, Text.literal("Level " + handler.enchantingLevel), 82 + q, 7 + r, 0x3E3839, false);
        context.drawTexture(TEXTURE, q + 129, r + 8, 0, 227, ((NewEnchantmentScreenHandler) this.handler).enchantingXP * 40 / NewEnchantmentHelper.getEnchLevelXpPerLevel(handler.enchantingLevel), 5);

        // button
        if (canSubmit()) {
            if (mouseX < q + 155 && mouseX > q + 82 && mouseY > r + 51 && mouseY < r + 64) {
                context.drawTexture(TEXTURE, 82 + q, r + 51, 183, 200, 73, 13);
            } else {
                context.drawTexture(TEXTURE, 82 + q, r + 51, 183, 187, 73, 13);
            }
        } else {
            context.drawTexture(TEXTURE, 82 + q, r + 51, 183, 213, 73, 13);
        }

        // reroll button
        if (mouseX < q + 169 && mouseX > q + 156 && mouseY > r + 51 && mouseY < r + 65) {
            context.drawTexture(TEXTURE, 156 + q, r + 51, 13, 243, 13, 13);
        } else {
            context.drawTexture(TEXTURE, 156 + q, r + 51, 0, 243, 13, 13);
        }

        if (handler.xpCost > 0 && handler.minLevel > 0 && handler.clueAccuracy[0] != -1) {
            context.drawTexture(TEXTURE, 84 + q, r + 53, 50, 247, 9, 9);
            context.drawText(this.textRenderer, Text.literal(String.valueOf(handler.xpCost)), 95 + q, 54 + r, 0xC8FF8F, true);
            context.drawText(this.textRenderer, Text.literal("Lv " + handler.minLevel), 95 + 25 + q, 54 + r, 0xF5FF8F, true);
        }

        // render outputs
        int m = 0;
        for (EnchantmentClueEntry entry : enchantments) {
            if (enchantments.size() > 3 && (m < this.indexStartOffset || m >= 3 + this.indexStartOffset)) {
                ++m;
                continue;
            }

            String string = entry.enchantment == null ? "ENCHANTMENT" : entry.enchantment.getName(entry.level).getString();
            int p = 86 - this.textRenderer.getWidth(string);
            StringVisitable randomString = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, p);

            if (
                    (handler.screenIndex == NewEnchantmentHelper.TableScreens.TRANSFER.ordinal() &&
                            !NewEnchantmentHelper.canApplyToItemStack(handler.slots.get(2).getStack(), new NewEnchantmentLevelEntry(entry.enchantment, entry.level))) ||
                            handler.screenIndex == NewEnchantmentHelper.TableScreens.CURSED.ordinal() &&
                                    !entry.enchantment.isCursed()
            ) {
                context.drawTexture(TEXTURE, 83 + q, k, 182, 177, 74, 10);
            } else {
                if (handler.selectedEnchants[m] >= 0) {
                    context.drawTexture(TEXTURE, 83 + q, k, 182, 236, 74, 10);
                } else {
                    if (mouseX < q + 158 && mouseX > q + 83 && mouseY > k && mouseY < k + 10) {
                        context.drawTexture(TEXTURE, 83 + q, k, 182, 226, 74, 10);
                    } else {
                        context.drawTexture(TEXTURE, 83 + q, k, 182, 246, 74, 10);
                    }
                }
            }

            context.getMatrices().push();
            context.getMatrices().scale(0.8f, 0.8f, 0.8f);

            float z = 1.25f;

            if (handler.screenIndex == NewEnchantmentHelper.TableScreens.GENERATE.ordinal()) {
                if (entry.accuracy < 25) {
                    context.drawTextWrapped(this.textRenderer, randomString, (int) ((85 + q + 1) * z), (int) ((k + 2 + 1) * z), p, 0xFFFFFFFF);
                } else {
                    String enchName = Text.literal(entry.enchantment == null ? "LOST" : entry.enchantment.getName(entry.level).getString()).asTruncatedString(10);
                    context.drawText(this.textRenderer, enchName, (int) ((85 + q + 1) * z), (int) ((k + 2 + 1) * z), 0xFFFFFFFF, false);
                }
                context.drawText(this.textRenderer, Text.literal(entry.accuracy + "%"), (int) ((85 + 72 + 1 + q - this.textRenderer.getWidth(Text.literal(entry.accuracy + "%"))) * z), (int) ((k + 2 + 1) * z), 0xFFFFFFFF, false);
            } else if (handler.screenIndex == NewEnchantmentHelper.TableScreens.TRANSFER.ordinal()) {
                String enchName = Text.literal(entry.enchantment == null ? "LOST" : entry.enchantment.getName(entry.level).getString()).asTruncatedString(10);

                context.drawText(this.textRenderer, enchName, (int) ((85 + q + 1) * z), (int) ((k + 2 + 1) * z), !NewEnchantmentHelper.canApplyToItemStack(handler.slots.get(2).getStack(), new NewEnchantmentLevelEntry(entry.enchantment, entry.level)) ? 0x9D9D9D : (handler.selectedEnchants[m] == -1 || handler.selectedEnchants[m] == 1) ? 0x83FF8C : 0xFFA0A0, false);
                if (NewEnchantmentHelper.canApplyToItemStack(handler.slots.get(2).getStack(), new NewEnchantmentLevelEntry(entry.enchantment, entry.level))) {
                    context.drawText(this.textRenderer, Text.literal(entry.accuracy + "%"), (int) ((85 + 72 + 1 + q - this.textRenderer.getWidth(Text.literal(entry.accuracy + "%"))) * z), (int) ((k + 2 + 1) * z), handler.selectedEnchants[m] == -1 || handler.selectedEnchants[m] == 1 ? 0x83FF8C : 0xFFA0A0, false);
                }
            } else if (handler.screenIndex == NewEnchantmentHelper.TableScreens.CURSED.ordinal()) {
                String enchName = Text.literal(entry.enchantment == null ? "LOST" : entry.enchantment.getName(entry.level).getString()).asTruncatedString(10);

                context.drawText(this.textRenderer, enchName, (int) ((85 + q + 1) * z), (int) ((k + 2 + 1) * z), !entry.enchantment.isCursed() ? 0x9D9D9D : (handler.selectedEnchants[m] == -1 || handler.selectedEnchants[m] == 1) ? 0x83FF8C : 0xFFA0A0, false);
                if (entry.enchantment.isCursed()) {
                    context.drawText(this.textRenderer, Text.literal(entry.accuracy + "%"), (int) ((85 + 72 + 1 + q - this.textRenderer.getWidth(Text.literal(entry.accuracy + "%"))) * z), (int) ((k + 2 + 1) * z), handler.selectedEnchants[m] == -1 || handler.selectedEnchants[m] == 1 ? 0x83FF8C : 0xFFA0A0, false);
                }
            }
            context.getMatrices().pop();

            k += 10;
            m++;
        }

        // render tooltips
        this.drawMouseoverTooltip(context, mouseX, mouseY);

        // Enchant button
        if (mouseX < q + 155 && mouseX > q + 82 && mouseY > r + 51 && mouseY < r + 64) {
            List<Text> tooltipList = new ArrayList<>();
            if (handler.screenIndex == NewEnchantmentHelper.TableScreens.TRANSFER.ordinal()) {
                tooltipList.add(Text.literal("Transfer Enchantments").formatted(Formatting.LIGHT_PURPLE));
            } else if (handler.screenIndex == NewEnchantmentHelper.TableScreens.CURSED.ordinal()) {
                tooltipList.add(Text.literal("Lift Curses").formatted(Formatting.LIGHT_PURPLE));
            } else {
                tooltipList.add(Text.literal("Generate Enchantments").formatted(Formatting.LIGHT_PURPLE));
            }

            tooltipList.add(Text.literal("soenchanting").formatted(Formatting.AQUA, Formatting.OBFUSCATED));

            if (handler.xpCost >= 0 && handler.minLevel >= 0) {
                tooltipList.add(Text.literal("Min Level: ").formatted(Formatting.GREEN).append(
                        Text.literal(String.valueOf(handler.minLevel)).formatted(
                                this.client.player.experienceLevel >= handler.minLevel ? Formatting.GREEN : Formatting.RED
                        )
                ));
                tooltipList.add(Text.literal("XP Cost: ").formatted(Formatting.DARK_GREEN).append(
                        Text.literal(String.valueOf(handler.xpCost)).formatted(
                                this.client.player.experienceLevel >= handler.xpCost ? Formatting.DARK_GREEN : Formatting.RED
                        )
                ));
            }
            int maxQuantity = Math.min(
                    NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_LAPIS, handler.enchantingLevel),
                    handler.getLapisCount()
            );
            tooltipList.add(handler.getLapisCount() > 0 ?
                    (
                            (handler.screenIndex == NewEnchantmentHelper.TableScreens.TRANSFER.ordinal() || handler.screenIndex == NewEnchantmentHelper.TableScreens.CURSED.ordinal()) ?
                                    (handler.getLapisCount() < selectedCount() ? Text.literal("Quantity: " + handler.getLapisCount()).formatted(Formatting.RED) : Text.literal("Quantity: " + maxQuantity).formatted(Formatting.BLUE)) :
                                    Text.literal("Quantity: " +
                                            (handler.minQuantity == maxQuantity ?
                                                    handler.minQuantity :
                                                    Math.max(1, handler.minQuantity) +
                                                            " - " +
                                                            maxQuantity)
                                    ).formatted(Formatting.BLUE)
                    ) : Text.literal("Insert Catalyst").formatted(Formatting.RED)
            );

            if (handler.screenIndex == NewEnchantmentHelper.TableScreens.CURSED.ordinal() && !handler.canCurselift) {
                tooltipList.add(Text.literal("Cannot Curselift!").formatted(Formatting.RED));
            }

            if (handler.screenIndex == NewEnchantmentHelper.TableScreens.TRANSFER.ordinal() || handler.screenIndex == NewEnchantmentHelper.TableScreens.CURSED.ordinal()) {
                tooltipList.add(Text.literal(""));
                tooltipList.add(Text.literal("Selected:"));
                for (int i = 0; i < 10; i++) {
                    if (handler.selectedEnchants[i] < 0 || Enchantment.byRawId(handler.outputIds[i]) == null) {
                        continue;
                    }
                    EnchantmentClueEntry entry = new EnchantmentClueEntry(Enchantment.byRawId(handler.outputIds[i]), handler.outputLevels[i], handler.clueAccuracy[i]);
                    tooltipList.add(
                            Text.literal(" " + entry.enchantment.getName(entry.level).getString())
                                    .append((handler.screenIndex == NewEnchantmentHelper.TableScreens.TRANSFER.ordinal() && NewEnchantmentHelper.canCombineToUpgrade(handler.slots.get(2).getStack(), new NewEnchantmentLevelEntry(entry.enchantment, entry.level)) ?
                                            Text.literal(" -> ").append(Text.translatable("enchantment.level." + (entry.level + 1))) :
                                            Text.literal(""))
                                    )
                                    .formatted(NewEnchantmentHelper.getRarityColor(entry.enchantment))
                    );
                    tooltipList.add(
                            (handler.selectedEnchants[i] == 0 ?
                                    Text.literal("  UNSTABLE").formatted(Formatting.RED) :
                                    Text.literal("  STABLE").formatted(Formatting.DARK_GREEN))
                                    .append(Text.literal(" - ").formatted(Formatting.RESET))
                                    .append(Text.literal(entry.accuracy + "%").formatted(
                                            entry.accuracy < 25 ? Formatting.YELLOW :
                                                    entry.accuracy < 50 ? Formatting.GOLD :
                                                            entry.accuracy < 75 ? Formatting.GREEN :
                                                                    Formatting.DARK_GREEN))
                    );


                }
            } else {
                tooltipList.add(Text.literal("Rarity Bonus: " + handler.rarityBonus).formatted(Formatting.DARK_AQUA));
                if (handler.treasureAllowed) {
                    tooltipList.add(Text.literal("Treasure Available!").formatted(Formatting.GOLD));
                }
                tooltipList.add(Text.literal(""));
                tooltipList.add(Text.literal("Clues:"));
                for (EnchantmentClueEntry entry : enchantments) {
                    tooltipList.add(
                            (entry.accuracy < 25 ?
                                    Text.literal(" enchant").formatted(Formatting.YELLOW, Formatting.OBFUSCATED) :
                                    (entry.enchantment == null ?
                                            Text.literal(" LOST").formatted(Formatting.YELLOW) :
                                            Text.literal(" " + entry.enchantment.getName(entry.level).getString())
                                                    .formatted(NewEnchantmentHelper.getRarityColor(entry.enchantment)))
                            )
                    );
                    tooltipList.add(
                            Text.literal("  Accuracy: ").formatted(Formatting.RESET)
                                    .append(Text.literal(entry.accuracy + "%").formatted(
                                            entry.accuracy < 25 ? Formatting.YELLOW :
                                                    entry.accuracy < 50 ? Formatting.GOLD :
                                                            entry.accuracy < 75 ? Formatting.GREEN :
                                                                    Formatting.DARK_GREEN))
                    );
                }
            }

            context.drawTooltip(this.textRenderer, tooltipList, mouseX, mouseY);
        } else if (mouseX < q + 169 && mouseX > q + 156 && mouseY > r + 51 && mouseY < r + 65) {
            context.drawTooltip(this.textRenderer, List.of(
                    Text.literal("Reroll Enchantment Seed"),
                    Text.literal("Cost: ").formatted(Formatting.YELLOW).append(Text.literal("1XP").formatted(Formatting.GREEN))
            ), mouseX, mouseY);
        } /* else if (mouseX < q + 78 && mouseX > q + 67 && mouseY > r + 21 && mouseY < r + 33) {
            context.drawTooltip(this.textRenderer, List.of(
                    Text.literal("Luck - " + handler.synergy + "% / 100%").formatted(Formatting.DARK_RED),
                    Text.literal("Lowers the chance of").formatted(Formatting.GRAY, Formatting.ITALIC),
                    Text.literal("harsh penalties on corrupted").formatted(Formatting.GRAY, Formatting.ITALIC),
                    Text.literal("enchants.").formatted(Formatting.GRAY, Formatting.ITALIC)
            ), mouseX, mouseY);
        } */ else if (mouseX < q + 78 && mouseX > q + 57 && mouseY > r + 21 && mouseY < r + 42) {
            context.drawTooltip(this.textRenderer, List.of(
                    Text.literal("Chaos - " + handler.chaos + "% / 100%").formatted(Formatting.DARK_PURPLE),
                    Text.literal("Raises the chance of").formatted(Formatting.GRAY, Formatting.ITALIC),
                    Text.literal("generating corrupted").formatted(Formatting.GRAY, Formatting.ITALIC),
                    Text.literal("enchants.").formatted(Formatting.GRAY, Formatting.ITALIC)
            ), mouseX, mouseY);
        } else if (mouseX < q + 170 && mouseX > q + 5 && mouseY > r + 71 && mouseY < r + 82) {
            context.drawTooltip(this.textRenderer, List.of(
                    Text.literal("Power - " + handler.power + " / " + handler.maxPower).formatted(Formatting.AQUA),
                    Text.literal("Raises the power of").formatted(Formatting.GRAY, Formatting.ITALIC),
                    Text.literal("output enchantments.").formatted(Formatting.GRAY, Formatting.ITALIC)
            ), mouseX, mouseY);
        } else if (mouseX < q + 170 && mouseX > q + 5 && mouseY > r + 82 && mouseY < r + 93) {
            context.drawTooltip(this.textRenderer, List.of(
                    Text.literal("Synergy - " + handler.synergy + " / " + handler.maxSynergy).formatted(Formatting.YELLOW),
                    Text.literal("Lowers the chance of").formatted(Formatting.GRAY, Formatting.ITALIC),
                    Text.literal("harsh penalties on corrupted").formatted(Formatting.GRAY, Formatting.ITALIC),
                    Text.literal("enchants.").formatted(Formatting.GRAY, Formatting.ITALIC)
            ), mouseX, mouseY);
        } else if (mouseX < q + 170 && mouseX > q + 5 && mouseY > r + 93 && mouseY < r + 104) {
            context.drawTooltip(this.textRenderer, List.of(
                    Text.literal("Insight - " + handler.insight + " / " + handler.maxInsight).formatted(Formatting.RED),
                    Text.literal("Improves the accuracy of").formatted(Formatting.GRAY, Formatting.ITALIC),
                    Text.literal("enchantment clues.").formatted(Formatting.GRAY, Formatting.ITALIC)
            ), mouseX, mouseY);
        } else if (mouseX < q + 171 && mouseX > q + 78 && mouseY > r + 5 && mouseY < r + 15) {
            int maxBookshelves = NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_BOOKSHELVES, handler.enchantingLevel);
            int maxArtifacts = NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_ARTIFACTS, handler.enchantingLevel);
            int maxTomes = NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_TOMES, handler.enchantingLevel);
            context.drawTooltip(this.textRenderer, List.of(
                    Text.literal("Enchanting Level " + handler.enchantingLevel).formatted(Formatting.LIGHT_PURPLE),
                    Text.literal("XP: " + handler.enchantingXP + " / " + NewEnchantmentHelper.getEnchLevelXpPerLevel(handler.enchantingLevel)).formatted(Formatting.WHITE),
                    Text.literal(""),
                    Text.literal("Bookshelves: ").append(
                            handler.bookshelfCount > maxBookshelves ?
                                    Text.literal(String.valueOf(handler.bookshelfCount)).formatted(Formatting.RED) :
                                    Text.literal(String.valueOf(handler.bookshelfCount))
                    ).append(Text.literal(" / " + maxBookshelves)).formatted(Formatting.GOLD),
                    Text.literal("Artifacts: ").append(
                            handler.artifactCount > maxArtifacts ?
                                    Text.literal(String.valueOf(handler.artifactCount)).formatted(Formatting.RED) :
                                    Text.literal(String.valueOf(handler.artifactCount))
                    ).append(Text.literal(" / " + maxArtifacts)).formatted(Formatting.DARK_AQUA),
                    Text.literal("Tomes: ").append(
                            handler.tomeCount > maxTomes ?
                                    Text.literal(String.valueOf(handler.tomeCount)).formatted(Formatting.RED) :
                                    Text.literal(String.valueOf(handler.tomeCount))
                    ).append(Text.literal(" / " + maxTomes)).formatted(Formatting.BLUE)
            ), mouseX, mouseY);
        }

        // Clues
        m = 0;
        k = r + 19;
        for (EnchantmentClueEntry entry : enchantments) {
            if (enchantments.size() > 3 && (m < this.indexStartOffset || m >= 3 + this.indexStartOffset)) {
                ++m;
                continue;
            }

            if (mouseX < q + 158 && mouseX > q + 83 && mouseY > k && mouseY < k + 10) {
                List<Text> tooltipList = new ArrayList<>();

                if (handler.screenIndex == NewEnchantmentHelper.TableScreens.TRANSFER.ordinal()) {
                    tooltipList.add(
                            Text.literal(entry.enchantment.getName(entry.level).getString())
                                    .append((NewEnchantmentHelper.canCombineToUpgrade(handler.slots.get(2).getStack(), new NewEnchantmentLevelEntry(entry.enchantment, entry.level)) ?
                                            Text.literal(" -> ").append(Text.translatable("enchantment.level." + (entry.level + 1))) :
                                            Text.literal(""))
                                    )
                                    .formatted(NewEnchantmentHelper.getRarityColor(entry.enchantment))
                    );
                    tooltipList.add(
                            !NewEnchantmentHelper.canApplyToItemStack(handler.slots.get(2).getStack(), new NewEnchantmentLevelEntry(entry.enchantment, entry.level)) ?
                                    Text.literal("Incompatible with target item").formatted(Formatting.DARK_RED, Formatting.ITALIC) :
                                    ((handler.selectedEnchants[m] == 0 || handler.selectedEnchants[m] == -2 ?
                                            Text.literal("UNSTABLE").formatted(Formatting.RED) :
                                            Text.literal("STABLE").formatted(Formatting.DARK_GREEN))
                                            .append(Text.literal(" - ").formatted(Formatting.RESET))
                                            .append(Text.literal(entry.accuracy + "%").formatted(
                                                    entry.accuracy < 25 ? Formatting.YELLOW :
                                                            entry.accuracy < 50 ? Formatting.GOLD :
                                                                    entry.accuracy < 75 ? Formatting.GREEN :
                                                                            Formatting.DARK_GREEN)))
                    );
                } else if (handler.screenIndex == NewEnchantmentHelper.TableScreens.CURSED.ordinal()) {
                    tooltipList.add(
                            Text.literal(entry.enchantment.getName(entry.level).getString())
                                    .formatted(NewEnchantmentHelper.getRarityColor(entry.enchantment))
                    );
                    tooltipList.add(
                            !entry.enchantment.isCursed() ?
                                    Text.literal("Must remove curses!").formatted(Formatting.DARK_RED, Formatting.ITALIC) :
                                    ((handler.selectedEnchants[m] == 0 || handler.selectedEnchants[m] == -2 ?
                                            Text.literal("UNSTABLE").formatted(Formatting.RED) :
                                            Text.literal("STABLE").formatted(Formatting.DARK_GREEN))
                                            .append(Text.literal(" - ").formatted(Formatting.RESET))
                                            .append(Text.literal(entry.accuracy + "%").formatted(
                                                    entry.accuracy < 25 ? Formatting.YELLOW :
                                                            entry.accuracy < 50 ? Formatting.GOLD :
                                                                    entry.accuracy < 75 ? Formatting.GREEN :
                                                                            Formatting.DARK_GREEN)))
                    );
                } else {
                    tooltipList.add(
                            (entry.accuracy < 25 ?
                                    Text.literal("enchant").formatted(Formatting.YELLOW, Formatting.OBFUSCATED) :
                                    (entry.enchantment == null ?
                                            Text.literal("LOST").formatted(Formatting.YELLOW) :
                                            Text.literal(entry.enchantment.getName(entry.level).getString())
                                                    .formatted(NewEnchantmentHelper.getRarityColor(entry.enchantment)))
                            )
                    );
                    tooltipList.add(
                            Text.literal("Accuracy: ").formatted(Formatting.RESET)
                                    .append(Text.literal(entry.accuracy + "%").formatted(
                                            entry.accuracy < 25 ? Formatting.YELLOW :
                                                    entry.accuracy < 50 ? Formatting.GOLD :
                                                            entry.accuracy < 75 ? Formatting.GREEN :
                                                                    Formatting.DARK_GREEN))
                    );
                }
                context.drawTooltip(this.textRenderer, tooltipList, mouseX, mouseY);
            }

            k += 10;
            m++;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int i = (int) Arrays.stream(handler.clueAccuracy).filter(n -> n != -1).count();
        if (i > 3) {
            int j = i - 3;
            this.indexStartOffset = MathHelper.clamp((int) ((double) this.indexStartOffset - verticalAmount), 0, j);
//            System.out.println(this.indexStartOffset);
        }
        return true;
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        if (this.scrolling) {
            // Value from 26 to 43 (0 to 17)
            double k = MathHelper.clamp(mouseY - j, 26, 43) - 26;
            // k / 17 = y / l - 3

            // Value from 0 to l - 3
            int v = (int) Math.round((float) k * (Arrays.stream(handler.clueAccuracy).filter(n -> n != -1).count() - 3) / 17);

            this.indexStartOffset = v;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        if (Arrays.stream(handler.clueAccuracy).filter(n -> n != -1).count() > 3 && mouseX > (double) (i + 160) && mouseX < (double) (i + 169) && mouseY > (double) (j + 19) && mouseY < (double) (j + 50)) {
            this.scrolling = true;
        } else if (mouseX > (i + 83) && mouseX < (i + 155) && mouseY > (j + 52) && mouseY < (j + 64)) {
            if (((NewEnchantmentScreenHandler) this.handler).onButtonClick(this.client.player, 10)) {
                this.client.interactionManager.clickButton(((NewEnchantmentScreenHandler) this.handler).syncId, 10);
                return true;
            }
            return false;
        } else if (mouseX < i + 169 && mouseX > i + 156 && mouseY > j + 51 && mouseY < j + 65) {
            if (((NewEnchantmentScreenHandler) this.handler).onButtonClick(this.client.player, 11)) {
                this.client.interactionManager.clickButton(((NewEnchantmentScreenHandler) this.handler).syncId, 11);
                return true;
            }
            return false;
        } else {
            List<EnchantmentClueEntry> enchantments = new ArrayList<>(10);
            int c = 0;
            for (int enchantmentId : ((NewEnchantmentScreenHandler) this.handler).outputIds) {
                if (handler.clueAccuracy[c] != -1) {
                    enchantments.add(new EnchantmentClueEntry(enchantmentId < 0 ? null : Enchantment.byRawId(enchantmentId), handler.outputLevels[c], handler.clueAccuracy[c]));
                    c++;
                } else break;
            }

            int m = 0;
            int k = j + 19;
            for (EnchantmentClueEntry entry : enchantments) {
                if (enchantments.size() > 3 && (m < this.indexStartOffset || m >= 3 + this.indexStartOffset)) {
                    ++m;
                    continue;
                }

                if (mouseX < i + 158 && mouseX > i + 83 && mouseY > k && mouseY < k + 10) {
                    if (((NewEnchantmentScreenHandler) this.handler).onButtonClick(this.client.player, m)) {
                        this.client.interactionManager.clickButton(((NewEnchantmentScreenHandler) this.handler).syncId, m);
                        return true;
                    }
                    return false;
                }

                k += 10;
                m++;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void doTick() {
        ItemStack itemStack = ((NewEnchantmentScreenHandler) this.handler).getSlot(0).getStack();
        if (!ItemStack.areEqual(itemStack, this.stack)) {
            this.stack = itemStack;
            do {
                this.approximatePageAngle += (float) (this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.nextPageAngle <= this.approximatePageAngle + 1.0f && this.nextPageAngle >= this.approximatePageAngle - 1.0f);
        }
        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = handler.screenIndex != NewEnchantmentHelper.TableScreens.NONE.ordinal();
//        for (int i = 0; i < 3; ++i) {
//            if (((NewEnchantmentScreenHandler)this.handler).enchantmentPower == 0) continue;
//            bl = true;
//        }
        this.nextPageTurningSpeed = bl ? (this.nextPageTurningSpeed += 0.2f) : (this.nextPageTurningSpeed -= 0.2f);
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4f;
        float g = 0.2f;
        f = MathHelper.clamp(f, -0.2f, 0.2f);
        this.pageRotationSpeed += (f - this.pageRotationSpeed) * 0.9f;
        this.nextPageAngle += this.pageRotationSpeed;
    }

    private boolean canSubmit() {
        return switch (handler.screenIndex) {
            // NONE
            case 0 -> false;
            // GENERATE
            case 1 -> (handler.getLapisCount() > 0 &&
                    handler.clueAccuracy[0] >= 0 &&
                    this.client.player.experienceLevel >= handler.xpCost &&
                    this.client.player.experienceLevel >= handler.minLevel);
            // TRANSFER
            case 2 -> (handler.getLapisCount() >= selectedCount() &&
                    selectedCount() > 0 &&
                    this.client.player.experienceLevel >= handler.xpCost &&
                    this.client.player.experienceLevel >= handler.minLevel);
            // CURSED
            case 3 -> (handler.getLapisCount() > 0 &&
                    selectedCount() > 0 &&
                    this.client.player.experienceLevel >= handler.xpCost &&
                    this.client.player.experienceLevel >= handler.minLevel);
            // INVALID
            default -> false;
        };
    }

    private int selectedCount() {
        return (int) Arrays.stream(handler.selectedEnchants).filter(n -> n >= 0).count();
    }
}
