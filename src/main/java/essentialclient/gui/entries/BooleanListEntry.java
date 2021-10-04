package essentialclient.gui.entries;

import carpet.settings.ParsedRule;
import essentialclient.gui.clientrule.ClientRuleHelper;
import essentialclient.gui.clientrule.ClientRules;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import essentialclient.gui.ConfigListWidget;
import essentialclient.gui.rulescreen.ServerRulesScreen;
import com.google.common.collect.ImmutableList;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import essentialclient.utils.render.ITooltipEntry;
import essentialclient.utils.render.RenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BooleanListEntry extends ConfigListWidget.Entry implements ITooltipEntry {
    private final ParsedRule<?> settings;
    private final ClientRules clientSettings;
    private final String rule;
    private final ServerRulesScreen gui;
    private final ClientRulesScreen clientGui;
    private final ButtonWidget infoButton;
    private final ButtonWidget editButton;
    private final ButtonWidget resetButton;
    private final MinecraftClient client;


    public BooleanListEntry(final ParsedRule<?> settings, MinecraftClient client, ServerRulesScreen gui) {
        this.settings = settings;
        this.clientSettings = null;
        this.client = client;
        this.gui = gui;
        this.clientGui = null;
        this.rule = settings.name;
        this.infoButton = new ButtonWidget(0, 0, 14, 20, new LiteralText("i"), (button -> button.active = false));
        this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(settings.getAsString().equals("true") ? "§2true" : "§4false"), (buttonWidget) -> {
            String invertedBoolean = buttonWidget.getMessage().getString().equals("§2true") ? "false" : "true";
            CarpetSettingsServerNetworkHandler.ruleChange(settings.name, invertedBoolean, client);
            buttonWidget.setMessage(new LiteralText(invertedBoolean.equals("true") ? "§2true" : "§4false"));
        });
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            CarpetSettingsServerNetworkHandler.ruleChange(settings.name, settings.defaultAsString, client);
            this.editButton.setMessage(new LiteralText(settings.defaultAsString.equals("true") ? "§2true" : "§4false"));
        });
    }

    public BooleanListEntry(final ClientRules settings, MinecraftClient client, ClientRulesScreen gui) {
        this.settings = null;
        this.clientSettings = settings;
        this.client = client;
        this.gui = null;
        this.clientGui = gui;
        this.rule = settings.name;
        this.infoButton = new ButtonWidget(0, 0, 14, 20, new LiteralText("i"), (button -> button.active = false));
        this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(settings.getString().equals("true") ? "§2true" : "§4false"), (buttonWidget) -> {
            settings.invertBoolean();
            buttonWidget.setMessage(new LiteralText(settings.getString().equals("true") ? "§2true" : "§4false"));
            ClientRuleHelper.writeSaveFile();
            ClientRuleHelper.executeOnChange(client, settings, gui);
        });
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            settings.setValue(settings.defaultValue);
            ClientRuleHelper.writeSaveFile();
            this.editButton.setMessage(new LiteralText(settings.defaultValue));
        });
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        TextRenderer font = client.textRenderer;
        float fontX = (float)(x + 90 - ConfigListWidget.length);
        float fontY = (float)(y + height / 2 - 9 / 2);
        font.draw(matrices, this.rule, fontX, fontY, 16777215);

        this.resetButton.x = x + 290;
        this.resetButton.y = y;
        if (this.settings != null)
            this.resetButton.active = !this.settings.getAsString().equals(this.settings.defaultAsString);
        else
            this.resetButton.active = !this.clientSettings.getString().equals(this.clientSettings.defaultValue);

        this.editButton.x = x + 180;
        this.editButton.y = y;

        this.infoButton.x = x + 156;
        this.infoButton.y = y;

        this.infoButton.render(matrices, mouseX, mouseY, delta);
        this.editButton.render(matrices, mouseX, mouseY, delta);
        this.resetButton.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(this.infoButton ,this.editButton, this.resetButton);
    }

    @Override
    public void drawTooltip(int slotIndex, int x, int y, int mouseX, int mouseY, int listWidth, int listHeight, int slotWidth, int slotHeight, float partialTicks) {
        if (this.infoButton.isHovered() && !this.infoButton.active) {
            String description;
            if (this.settings != null)
                description = this.settings.description;
            else
                description = this.clientSettings.description;
            RenderHelper.drawGuiInfoBox(client.textRenderer, description, mouseY + 5, listWidth, slotWidth, listHeight, 48);
        }
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of(this.infoButton ,this.editButton, this.resetButton);
    }
}