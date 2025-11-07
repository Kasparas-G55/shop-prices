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
import java.util.Set;

@Slf4j
public class ShopPricesOverlay extends WidgetItemOverlay {
    private static final int PRICE_PADDING = 10;
    private static final int WIDTH_CLIP_PADDING = 10;
    private static final int INVENTORY_SIZE = 28;
    private static final Set<MenuAction> SHOP_WIDGET_ACTIONS = ImmutableSet.of(
        MenuAction.CC_OP,
        MenuAction.CC_OP_LOW_PRIORITY
    );
    private static final String INVENTORY_FULL_TEXT =
        ColorUtil.wrapWithColorTag("Warn: ", Color.YELLOW) + "Inventory is full.";

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
        if (plugin.shopName == null) {
            return;
        }

        Shop activeShop = plugin.shopsMap.get(plugin.shopName);

        if (activeShop == null) {
            return;
        }

        if (plugin.getConfig().displayOverlay()) {
            onDisplayOverlay(graphics, activeShop, itemWidget);
        }

        if (plugin.getConfig().displayTooltip()) {
            onDisplayTooltip(activeShop, itemWidget);
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

        graphics.setClip(parentBounds);
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

        if (!SHOP_WIDGET_ACTIONS.contains(type)) {
            return;
        }

        int buyAmount;

        switch (option) {
            case "Buy 50":
                buyAmount = 50;
                break;
            case "Buy 10":
                buyAmount = 10;
                break;
            case "Buy 5":
                buyAmount = 5;
                break;
            case "Buy 1":
            case "Value":
                buyAmount = 1;
                break;
            default:
                return;
        }

        ItemContainer itemContainer = client.getItemContainer(InventoryID.INV);
        int inventorySpace = itemContainer != null ? INVENTORY_SIZE - itemContainer.count() : 0;

        ItemComposition itemComposition = itemManager.getItemComposition(itemId);
        int currentStock = itemWidget.getQuantity();

        Tooltip tooltip;

        if (inventorySpace <= 0) {
            tooltip = new Tooltip(INVENTORY_FULL_TEXT);
            tooltipManager.add(tooltip);
            return;
        }

        if (itemComposition.isStackable() && buyAmount > currentStock) {
            buyAmount = currentStock;
        } else if (buyAmount > inventorySpace) {
            buyAmount = inventorySpace;
        }

        int totalPrice = activeShop.getSellPriceTotal(itemComposition, currentStock, buyAmount);
        int multiplierThreshold = plugin.getConfig().priceThreshold();

        Color priceColor;
        if (plugin.getConfig().priceThresholdEnabled() && activeShop.isPriceAtThreshold(itemComposition, multiplierThreshold, currentStock)) {
            priceColor = plugin.getConfig().thresholdOverlayColor();
        } else {
            priceColor = Color.WHITE;
        }

        String priceText = ColorUtil.wrapWithColorTag(totalPrice + " gp", priceColor);

        tooltip = new Tooltip(
            String.format("Sells at: %s (%d)", priceText, buyAmount)
        );

        tooltipManager.add(tooltip);
    }
}
