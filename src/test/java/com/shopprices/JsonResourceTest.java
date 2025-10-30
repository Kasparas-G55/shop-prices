package com.shopprices;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonResourceTest {
    private static final String SHOPS_RESOURCE = "shops.json";
    private static final Type SHOP_TYPE = new TypeToken<Map<String, Shop>>(){}.getType();
    private static final Gson gson = new Gson();
    private static Map<String, Shop> shops;
    private static Reader reader;

    @BeforeAll
    public static void start()  {
        InputStream stream = JsonResourceTest.class.getClassLoader().getResourceAsStream(SHOPS_RESOURCE);
        assertNotNull(stream, "Resource not found.");
        reader = new InputStreamReader(stream);
        assertDoesNotThrow(() -> {
            shops = gson.fromJson(reader, SHOP_TYPE);
            assertNotNull(shops);
        });
    }

    @Test
    void testJsonMatchesExpectedKeys() {
        assertTrue(shops.entrySet().stream().allMatch(
            shop -> shop.getKey().matches("[A-Z_]+")
        ));
    }

    @Test
    void testJsonMatchesExpectedValues() {
        Shop shop = shops.get("VARROCK_GENERAL_STORE");

        assertEquals(3.0f, shop.shopDelta);
        assertEquals(130, shop.sellMultiplier);
        assertNotNull(shop.itemStocks.get("Pot"));
        assertEquals(5, shop.itemStocks.get("Pot"));
    }

    @AfterAll
    public static void close() throws IOException {
        reader.close();
    }
}
