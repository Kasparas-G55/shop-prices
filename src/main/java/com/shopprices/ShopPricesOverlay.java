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
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

import javax.inject.Inject;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Color;
import java.util.Set;

@Slf4j
public class ShopPricesOverlay extends Overlay {
    private static final int PRICE_PADDING = 10;
    private static final int INVENTORY_SIZE = 28;
    private static final Set<MenuAction> SHOP_WIDGET_ACTIONS = ImmutableSet.of(
        MenuAction.CC_OP,
        MenuAction.CC_OP_LOW_PRIORITY
    );
    private final Client client;
    private final ItemManager itemManager;
    private final TooltipManager tooltipManager;
    private final ShopPricesPlugin plugin;

    @Inject
    public ShopPricesOverlay(ShopPricesPlugin plugin, Client client, ItemManager itemManager, TooltipManager tooltipManager) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.plugin = plugin;
        this.client = client;
        this.itemManager = itemManager;
        this.tooltipManager = tooltipManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget shopWidget = client.getWidget(InterfaceID.Shopmain.ITEMS);
        Widget frameWidget = client.getWidget(InterfaceID.Shopmain.FRAME);
        if (shopWidget == null || frameWidget == null) {
            return null;
        }

        Widget[] shopItems = shopWidget.getDynamicChildren();
        Widget[] frameChildren = frameWidget.getDynamicChildren();

        if (shopItems == null || frameChildren == null) {
            return null;
        }

        String shopName = Shop.formatShopName(frameChildren[1].getText());

        for (Widget itemWidget : shopItems) {
            if (itemWidget.getItemId() == -1 || itemWidget.getName().isBlank()) {
                continue;
            }

            Shop activeShop = ShopPricesPlugin.shopsMap.get(shopName);

            if (plugin.getConfig().displayOverlay()) {
                onDisplayOverlay(graphics, activeShop, itemWidget);
            }

            if (plugin.getConfig().displayTooltip()) {
                onDisplayTooltip(activeShop, itemWidget);
            }
        }

        return null;
    }

    private void onDisplayOverlay(Graphics2D graphics, Shop activeShop, Widget itemWidget) {
        ItemComposition itemComposition = itemManager.getItemComposition(itemWidget.getItemId());
        int currentStock = itemWidget.getItemQuantity();
        int sellPrice = activeShop.getSellPrice(itemComposition, currentStock);

        int multiplierThreshold = plugin.getConfig().priceThreshold();
        String sellValue = Shop.getPriceValue(sellPrice);
        Rectangle bounds = itemWidget.getBounds();


        if (plugin.getConfig().priceThresholdEnabled() && activeShop.isPriceAtThreshold(itemComposition, multiplierThreshold, currentStock)) {
            graphics.setColor(plugin.getConfig().thresholdOverlayColor());
        } else {
            graphics.setColor(plugin.getConfig().defaultOverlayColor());
        }

        graphics.drawString(sellValue, bounds.x, (int) bounds.getMaxY() + PRICE_PADDING);
    }

    private void onDisplayTooltip(Shop activeShop, Widget itemWidget) {
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

        if (itemId != itemWidget.getItemId()) {
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

        ItemComposition itemComposition = itemManager.getItemComposition(itemWidget.getItemId());
        int currentStock = itemWidget.getItemQuantity();

        if (!itemComposition.isStackable() && inventorySpace > 0 && currentStock > inventorySpace) {
            buyAmount = inventorySpace;
        } else if (currentStock > 0 && buyAmount > currentStock) {
            buyAmount = currentStock;
        }

        int totalPrice = activeShop.getSellPriceTotal(itemComposition, currentStock, buyAmount);
        int multiplierThreshold = plugin.getConfig().priceThreshold();

        String color = "ffffff";
        if (plugin.getConfig().priceThresholdEnabled() && activeShop.isPriceAtThreshold(itemComposition, multiplierThreshold, currentStock)) {
            Color thresholdColor = plugin.getConfig().thresholdOverlayColor();
            color = Integer.toHexString(thresholdColor.getRGB()).substring(2);
        }

        Tooltip tooltip = new Tooltip(
            String.format(
                "Sells at: <col=%s>%s</col> (%d)",
                color,
                Shop.getExactPriceValue(totalPrice),
                buyAmount
            )
        );

        tooltipManager.add(tooltip);
    }
}
