package com.shopprices;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Arrays;

@Slf4j
@PluginDescriptor(
	name = "Shop Prices"
)
public class ShopPricesPlugin extends Plugin {
	@Inject
	private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ItemManager itemManager;

    @Subscribe
    public void onGameTick(GameTick event) {
        Widget shop = client.getWidget(InterfaceID.Shopmain.ITEMS);

        if (shop == null)
            return;

        Widget[] items = shop.getChildren();

        if (items == null)
            return;

        ItemComposition composition = itemManager.getItemComposition(items[3].getItemId());
        log.debug("Item {}: {}gp", composition.getName(), ShopPricesPlugin.getBuyPrice(composition.getPrice(), items[3].getItemQuantity()));
    }

    static int getBuyPrice(int itemValue, int itemStock) {
        int stockDelta = 5 - itemStock;

        return Math.max(itemValue * (130 + (3 * stockDelta)) / 100, 30 * itemValue / 100);
    }
}
