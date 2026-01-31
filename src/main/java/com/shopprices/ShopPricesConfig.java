package com.shopprices;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.Color;

@ConfigGroup("shopprices")
public interface ShopPricesConfig extends Config {
    @ConfigItem(
        position = 0,
        keyName = "displayOverlay",
        name = "Display prices below Items",
        description = "Shows a item value below each item in the shop."
    )
    default boolean displayOverlay() {
        return true;
    }

    @ConfigItem(
        position = 1,
        keyName = "displayTooltip",
        name = "Display prices in Tooltip",
        description = "Shows a small tooltip with items total price when hovering over an item."
    )
    default boolean displayTooltip() {
        return true;
    }

    @ConfigItem(
        position = 2,
        keyName = "overlayValueColor",
        name = "Shop price color",
        description = "Sets the default item value color."
    )
    @Alpha default Color defaultOverlayColor() {
        return Color.WHITE;
    }

    @ConfigItem(
        position = 3,
        keyName = "overlayFont",
        name = "Shop price font",
        description = "Sets the default item value font."
    )
    default String defaultOverlayFont() {
        return "RuneScape Small";
    }

    @ConfigItem(
        position = 4,
        keyName = "overlayFontSize",
        name = "Shop price font size",
        description = "Sets the default item value font size."
    )
    default int defaultOverlayFontSize() {
        return 16;
    }

    @ConfigItem(
        position = 5,
        keyName = "enablePriceThreshold",
        name = "Enable price threshold",
        description = "Allows you to set a threshold for when a price reaches a certain percentage."
    )
    default boolean priceThresholdEnabled() {
        return true;
    }

    @ConfigItem(
        position = 6,
        keyName = "priceThresholdPercentage",
        name = "Threshold Percentage",
        description = "Sets the threshold percentage."
    )
    default int priceThreshold() {
        return 15;
    }

    @ConfigItem(
        position = 7,
        keyName = "priceThresholdColor",
        name = "Price threshold color",
        description = "Sets the threshold item value color."
    )
    default Color thresholdOverlayColor() {
        return new Color(221, 69, 69);
    }

    @ConfigItem(
        position = 8,
        keyName = "blockOnThreshold",
        name = "Block buy on threshold",
        description = "Blocks buying when multiplier is past threshold."
    )
    default boolean blockOnThreshold() {
        return false;
    }

    @ConfigItem(
        position = 9,
        keyName = "blockCheckQuantity",
        name = "Check buy quantity on Block",
        description = "Blocks buying if the quantity will go past threshold. (Block on Threshold needs to be ON)"
    )
    default boolean blockCheckQuantity() {
        return false;
    }
}
