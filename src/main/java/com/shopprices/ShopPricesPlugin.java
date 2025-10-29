package com.shopprices;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
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
    tags = {"qol", "shop", "prices", "overlay"}
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

    @Getter
    @Inject
    private ShopPricesConfig config;

    @Inject
    private Gson gson;

    public static Map<String, Shop> shopsMap = new HashMap<>();
    public static final String SHOP_KEY_PATTERN = "[^a-zA-Z ]+";
    public static final float MIN_SELL_MULTIPLIER = 30.0f;

    @Provides
    ShopPricesConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ShopPricesConfig.class);
    }

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

    public static float getSellMultiplier(int sellMult, int defaultStock, int currentStock, float shopDelta) {
        int stockDelta = defaultStock - currentStock;
        return sellMult + (shopDelta * stockDelta);
    }

    public static int getSellPrice(int itemValue, int sellMult, int currentStock, int defaultStock, float shopDelta) {
        return (int) Math.max(
            itemValue * getSellMultiplier(sellMult, defaultStock, currentStock, shopDelta) / 100,
            Math.max(MIN_SELL_MULTIPLIER * itemValue / 100, 1)
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
