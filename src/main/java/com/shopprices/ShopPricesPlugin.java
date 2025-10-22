package com.shopprices;

import javax.inject.Inject;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "Shop Prices",
    description = "Display prices for items in NPC stores.",
    tags = { "qol", "shop", "prices" }
)
public class ShopPricesPlugin extends Plugin {
	@Inject
	private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ShopPricesOverlay shopPricesOverlay;

    @Inject
    private Gson gson;

    public static Map<String, Store> stores;
    public final static String STORE_KEY_PATTERN = "[^a-zA-Z ]+";

    public static class Store {
        public int sellMultiplier;
        public float storeDelta;
        public Map<String, Integer> items;
    }

    @Override
    protected void startUp() {
        overlayManager.add(shopPricesOverlay);

        InputStream stream = getClass().getClassLoader().getResourceAsStream("stores.json");

        if (stream == null)
            throw new IllegalArgumentException("File not found.");

        try (InputStreamReader reader = new InputStreamReader(stream)) {
            Type storeMapType = new TypeToken<Map<String, Store>>(){}.getType();
            ShopPricesPlugin.stores = gson.fromJson(reader, storeMapType);
            stream.close();
        } catch (IOException e) {
            log.error("Failed to read JSON file: {}", e.getMessage());
        }

    }

    @Override
    protected void shutDown() {
        overlayManager.remove(shopPricesOverlay);
        stores.clear();
    }

    public static String formatStoreKey(String storeName) {
        return String.join("_", storeName.replaceAll(STORE_KEY_PATTERN, "").toUpperCase().split(" "));
    }

    public static int getSellPrice(int itemValue, int sellMultiplier, int itemStock, int defaultStock, float storeDelta) {
        int stockDelta = defaultStock - itemStock;

        return (int) Math.max(itemValue * (sellMultiplier + (storeDelta * stockDelta)) / 100, storeDelta * itemValue / 100);
    }
}
