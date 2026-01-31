package com.shopprices;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Font;
import java.util.Set;

@Slf4j
public class ShopPricesOverlay extends WidgetItemOverlay {
    private static final int PRICE_PADDING = 10;
    private static final int WIDTH_CLIP_PADDING = 10;
    private static final int INVENTORY_SIZE = 28;
    private static final String WARN_TAG = ColorUtil.wrapWithColorTag("Warn: ", Color.YELLOW);
    private static final String INVENTORY_FULL_TEXT = WARN_TAG + "Inventory is full.";
    private static final String THRESHOLD_BLOCK_TEXT = WARN_TAG + "Blocking - at threshold.";
    private static final String QUANTITY_BLOCK_TEXT = WARN_TAG + "Blocking - quantity will pass threshold.";

    // FIXME: Item skip list, band-aid fix for issues like #21 and #22 until a proper solution is found.
    // Add ID's for items that are very inaccurate in price.
    private static final Set<Integer> ITEM_BLACKLIST = ImmutableSet.of(
        ItemID.DISCOFRETURNING
    );

    private final Client client;
    private final ItemManager itemManager;
    private final TooltipManager tooltipManager;
    private final ShopPricesPlugin plugin;

    @Inject
    public ShopPricesOverlay(ShopPricesPlugin plugin, Client client, ItemManager itemManager, TooltipManager tooltipManager) {
        drawAfterLayer(InterfaceID.Shopmain.ITEMS);
        this.plugin = plugin;
        this.client = client;
        this.tooltipManager = tooltipManager;
        this.itemManager = itemManager;
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if (plugin.activeShop == null) {
            return;
        }

        if (ITEM_BLACKLIST.contains(itemWidget.getId())) {
            return;
        }

        if (plugin.getConfig().displayOverlay()) {
            onDisplayOverlay(graphics, plugin.activeShop, itemWidget);
        }

        if (plugin.getConfig().displayTooltip()) {
            onDisplayTooltip(plugin.activeShop, itemWidget);
        }
    }

    private void onDisplayOverlay(Graphics2D graphics, Shop activeShop, WidgetItem itemWidget) {
        ItemComposition itemComposition = itemManager.getItemComposition(itemWidget.getId());
        int currentStock = itemWidget.getQuantity();
        int sellPrice = activeShop.getSellPrice(itemComposition, currentStock);

        int multiplierThreshold = plugin.getConfig().priceThreshold();
        String sellValue = Shop.getPriceValue(sellPrice);
        Rectangle bounds = itemWidget.getCanvasBounds();

        if (plugin.getConfig().priceThresholdEnabled() && activeShop.isPriceAtThreshold(itemComposition, multiplierThreshold, currentStock)) {
            graphics.setColor(plugin.getConfig().thresholdOverlayColor());
        } else {
            graphics.setColor(plugin.getConfig().defaultOverlayColor());
        }

        Rectangle parentBounds = itemWidget.getWidget().getParent().getBounds();

        parentBounds.setSize(parentBounds.width + WIDTH_CLIP_PADDING, parentBounds.height);

        Font font = new Font(
            plugin.getConfig().defaultOverlayFont(),
            Font.PLAIN,
            plugin.getConfig().defaultOverlayFontSize()
        );

        graphics.setClip(parentBounds);
        graphics.setFont(font);
        graphics.drawString(sellValue, bounds.x, (int) bounds.getMaxY() + PRICE_PADDING);
    }

    private void onDisplayTooltip(Shop activeShop, WidgetItem itemWidget) {
        MenuEntry[] menuEntries = client.getMenu().getMenuEntries();
        int lastEntry = menuEntries.length - 1;

        if (client.isMenuOpen()) {
            return;
        }

        if (lastEntry < 0) {
            return;
        }

        MenuEntry menuEntry = menuEntries[lastEntry];
        MenuAction type = menuEntry.getType();
        String option = menuEntry.getOption();
        int itemId = menuEntry.getItemId();

        if (itemId != itemWidget.getId()) {
            return;
        }

        if (!Shop.MENU_ACTIONS.contains(type) || !Shop.MENU_OPTIONS.contains(option)) {
            return;
        }

        ItemContainer itemContainer = client.getItemContainer(InventoryID.INV);
        int inventorySpace = itemContainer != null ? INVENTORY_SIZE - itemContainer.count() : 0;

        Tooltip tooltip;

        if (inventorySpace <= 0) {
            tooltip = new Tooltip(INVENTORY_FULL_TEXT);
            tooltipManager.add(tooltip);
            return;
        }

        ItemComposition itemComposition = itemManager.getItemComposition(itemId);
        int currentStock = itemWidget.getQuantity();
        int multiplierThreshold = plugin.getConfig().priceThreshold();
        boolean atThreshold = activeShop.isPriceAtThreshold(itemComposition, multiplierThreshold, currentStock);

        if (plugin.getConfig().blockOnThreshold()) {
            if (atThreshold) {
                tooltip = new Tooltip(THRESHOLD_BLOCK_TEXT);
                tooltipManager.add(tooltip);
                return;
            }

            if (plugin.getConfig().blockCheckQuantity() && activeShop.isQuantityAtThreshold(itemComposition, multiplierThreshold, currentStock)) {
                tooltip = new Tooltip(QUANTITY_BLOCK_TEXT);
                tooltipManager.add(tooltip);
                return;
            }
        }

        Color priceColor;
        if (plugin.getConfig().priceThresholdEnabled() && atThreshold) {
            priceColor = plugin.getConfig().thresholdOverlayColor();
        } else {
            priceColor = Color.WHITE;
        }

        int buyAmount = plugin.activeShop.quantityOption.getAmount();
        int totalPrice = activeShop.getSellPriceTotal(itemComposition, currentStock, buyAmount);

        if (itemComposition.isStackable() && buyAmount > currentStock) {
            buyAmount = currentStock;
        } else if (buyAmount > inventorySpace) {
            buyAmount = inventorySpace;
        }

        String priceText = ColorUtil.wrapWithColorTag(
            Shop.getExactPriceValue(totalPrice),
            priceColor
        );

        tooltip = new Tooltip(
            String.format("Sells at: %s (%d)", priceText, buyAmount)
        );

        tooltipManager.add(tooltip);
    }
}
