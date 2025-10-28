package com.shopprices;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@PluginDescriptor(
    name = "Shop Prices",
    description = "Display prices for items in NPC shops.",
    tags = {"qol", "shop", "prices"}
)
public class ShopPricesPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private TooltipManager tooltipManager;

    @Inject
    private ShopPricesOverlay shopPricesOverlay;

    @Inject
    private Gson gson;

    public static Map<String, Shop> shopsMap = new HashMap<>();
    public static final String SHOP_KEY_PATTERN = "[^a-zA-Z ]+";

    @Override
    protected void startUp() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("shops.json");

        if (stream == null) {
            throw new IllegalArgumentException("Resource not found.");
        }

        try (InputStreamReader reader = new InputStreamReader(stream)) {
            Type shopMapType = new TypeToken<Map<String, Shop>>(){}.getType();
            ShopPricesPlugin.shopsMap = gson.fromJson(reader, shopMapType);
            overlayManager.add(shopPricesOverlay);
        } catch (IOException e) {
            log.error("Failed to read JSON file: {}", e.getMessage());
        }
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(shopPricesOverlay);
        shopsMap.clear();
    }

    public static String formatShopKey(String shopName) {
        return String.join("_", shopName.replaceAll(SHOP_KEY_PATTERN, "").toUpperCase().split(" "));
    }

    public static String formatValue(double value) {
        DecimalFormat formatter = new DecimalFormat("#,###,###,###gp");
        return formatter.format(value);
    }

    public static int getSellPrice(int itemValue, int sellMult, int itemStock, int defaultStock, float shopDelta) {
        int stockDelta = defaultStock - itemStock;

        return (int) Math.max(
            itemValue * (sellMult + (shopDelta * stockDelta)) / 100,
            shopDelta * itemValue / 100
        );
    }

    public static int getTotalSellPrice(int itemValue, int sellMult, int itemStock, int defaultStock, float shopDelta, int buyAmount) {
        int totalCost = 0;

        for (int x = 0; x < buyAmount; x++) {
            int currentStock = itemStock - x;
            totalCost += getSellPrice(itemValue, sellMult, currentStock, defaultStock, shopDelta);
        }

        return totalCost;
    }
}
