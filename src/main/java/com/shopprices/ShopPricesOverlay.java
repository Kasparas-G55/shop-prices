package com.shopprices;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Dimension;

@Slf4j
public class ShopPricesOverlay extends Overlay {

    private final Client client;
    private final ItemManager itemManager;

    private static final int PRICE_PADDING = 10;

    @Inject
    ShopPricesOverlay(ShopPricesPlugin plugin, Client client, ItemManager itemManager) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.itemManager = itemManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget shop = client.getWidget(InterfaceID.Shopmain.ITEMS);
        Widget frame = client.getWidget(InterfaceID.Shopmain.FRAME);

        if (shop == null || frame == null) {
            return null;
        }

        Widget[] items = shop.getDynamicChildren();
        Widget[] frameChildren = frame.getDynamicChildren();

        if (items == null || frameChildren == null) {
            return null;
        }

        String shopName = ShopPricesPlugin.formatStoreKey(frameChildren[1].getText());

        for (Widget item : items) {
            if (item.getItemId() == -1 || item.getName().isBlank()) {
                continue;
            }

            ItemComposition composition = itemManager.getItemComposition(item.getItemId());
            ShopPricesPlugin.Store store = ShopPricesPlugin.stores.get(shopName);
            Integer defaultStock = store.items.get(composition.getName());

            if (defaultStock == null) {
                defaultStock = 0;
            }


            int sellPrice = ShopPricesPlugin.getSellPrice(
                composition.getPrice(),
                store.sellMultiplier,
                item.getItemQuantity(),
                defaultStock,
                store.storeDelta
            );

            Rectangle bounds = item.getBounds();
            graphics.drawString(sellPrice + "gp", bounds.x, (int) bounds.getMaxY() + PRICE_PADDING);
        }

        return null;
    }
}
