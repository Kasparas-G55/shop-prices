package com.shopprices;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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

    private static final int SHOP_SCROLL_HEIGHT = 235;
    private static final String SHOPS_RESOURCE = "shops.json";
    private static final Type SHOP_TYPE = new TypeToken<Map<String, Shop>>(){}.getType();
    public static Map<String, Shop> shopsMap = new HashMap<>();

    @Provides
    ShopPricesConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ShopPricesConfig.class);
    }

    @Override
    protected void startUp() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(SHOPS_RESOURCE);

        if (stream == null) {
            throw new IllegalArgumentException("Resource not found.");
        }

        try (InputStreamReader reader = new InputStreamReader(stream)) {
            ShopPricesPlugin.shopsMap = gson.fromJson(reader, SHOP_TYPE);
            overlayManager.add(shopPricesOverlay);
        } catch (IOException e) {
            log.error("Failed to read JSON file \"{}\": {}", SHOPS_RESOURCE, e.getMessage());
        }
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(shopPricesOverlay);
        shopsMap.clear();
    }

    @Subscribe
    protected void onScriptPreFired(ScriptPreFired event) {
        Widget items = client.getWidget(InterfaceID.Shopmain.ITEMS);

        if (items == null) {
            return;
        }

        if (event.getScriptId() != ScriptID.UPDATE_SCROLLBAR) {
            return;
        }

        Widget scrollbar = client.getWidget(InterfaceID.Shopmain.SCROLLBAR);

        if (scrollbar != null && scrollbar.isHidden()) {
            items.setScrollHeight(0);
            items.revalidateScroll();
            return;
        }

        items.setScrollHeight(SHOP_SCROLL_HEIGHT);
        items.revalidateScroll();
    }
}
