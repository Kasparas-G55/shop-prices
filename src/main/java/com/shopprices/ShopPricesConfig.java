package com.shopprices;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("shopprices")
public interface ShopPricesConfig extends Config {
    @ConfigItem(
        keyName = "displayOverlay",
        name = "Display prices below Items",
        description = "Shows a item value below each item in the shop."
    )
    default boolean displayOverlay() {
        return true;
    }

    @ConfigItem(
        keyName = "displayTooltip",
        name = "Display prices in Tooltip",
        description = "Shows a small tooltip with items total price when hovering over an item."
    )
    default boolean displayTooltip() {
        return true;
    }
}
