# Changelog


## v1.2.1

[compare changes](https://github.com/Kasparas-G55/shop-prices/compare/03b214d7082adc80cf4f7d60e60f302abf8a9238...c410df90f99c81efd157b3f6cd90af551e31b92d)

### 🩹 Fixes

- Remove duplicate gp string ([c410df9](https://github.com/Kasparas-G55/shop-prices/commit/c410df9))

### ❤️ Contributors

- Kasparas Galdikas ([@Kasparas-G55](https://github.com/Kasparas-G55))


## v1.2.0

[compare changes](https://github.com/Kasparas-G55/shop-prices/compare/c65e6df54e7ff16d9c2cc9e3cda3f46fd79c0b1e...799cfab1e3d5f036b453e9819cb219edf13806e0)

### 🚀 Enhancements

- Extend Shops.ITEMS interface scroll height ([#15](https://github.com/Kasparas-G55/shop-prices/pull/15))
- Blocking buying when at threshold ([799cfab](https://github.com/Kasparas-G55/shop-prices/commit/799cfab))

### 🩹 Fixes

- Display exact price in tooltip ([7582ec4](https://github.com/Kasparas-G55/shop-prices/commit/7582ec4))
- Greedily split spaces and update shop-scrapper module ([#20](https://github.com/Kasparas-G55/shop-prices/pull/20))

### 💅 Refactors

- Set shopName using shop_main_init clientscript ([1dba4eb](https://github.com/Kasparas-G55/shop-prices/commit/1dba4eb))
- Move from Overlay to WidgetItemOverlay ([#17](https://github.com/Kasparas-G55/shop-prices/pull/17))
- Get shop quantity option from varp value ([ebd6afa](https://github.com/Kasparas-G55/shop-prices/commit/ebd6afa))
- Move activeShop field to plugin class ([4bcb124](https://github.com/Kasparas-G55/shop-prices/commit/4bcb124))

### 📖 Documentation

- Small documentation changes ([5719edd](https://github.com/Kasparas-G55/shop-prices/commit/5719edd))

### ❤️ Contributors

- Kasparas Galdikas ([@Kasparas-G55](https://github.com/Kasparas-G55))


## v1.1.2

[compare changes](https://github.com/Kasparas-G55/shop-prices/compare/a3516db...63e3b4b)

### 🩹 Fixes

- Skip shops not in json resource ([63e3b4b](https://github.com/Kasparas-G55/shop-prices/commit/63e3b4b))

### ❤️ Contributors

- Kasparas-G55 ([@Kasparas-G55](https://github.com/Kasparas-G55))


## v1.1.1

[compare changes](https://github.com/Kasparas-G55/shop-prices/compare/b029dec4de9b66f7af10823f4b093981c8c53580...a68d248375dcf9eac3d31061e4409fb5ffb981ea)

### 🩹 Fixes

- Format shop overlay price values ([#6](https://github.com/Kasparas-G55/shop-prices/pull/6))
- Buy amount showing max inventory space for unstackable items ([#7](https://github.com/Kasparas-G55/shop-prices/pull/7))

### 💅 Refactors

- Future proofing code ([#4](https://github.com/Kasparas-G55/shop-prices/pull/4))
- Utilize ColorUtil class ([#8](https://github.com/Kasparas-G55/shop-prices/pull/8))
- Constants for resource name and type ([#11](https://github.com/Kasparas-G55/shop-prices/pull/11))

### ✅ Tests

- Basic JsonResourceTest class ([#10](https://github.com/Kasparas-G55/shop-prices/pull/10))

### 🤖 CI

- Setup gradle build ([#9](https://github.com/Kasparas-G55/shop-prices/pull/9))

### ❤️ Contributors

- Kasparas Galdikas ([@Kasparas-G55](https://github.com/Kasparas-G55))


## v1.1.0

[compare changes](https://github.com/Kasparas-G55/shop-prices/compare/a524175...13b30be)

### 🚀 Enhancements

- Shop item value large integer formatting ([771820e](https://github.com/Kasparas-G55/shop-prices/commit/771820e))
- Tooltip for showing total sell price ([a10f7b1](https://github.com/Kasparas-G55/shop-prices/commit/a10f7b1))
- Simple ShopPricesConfig class ([c3898cc](https://github.com/Kasparas-G55/shop-prices/commit/c3898cc))
- Multiplier threshold warning ([94ac7b9](https://github.com/Kasparas-G55/shop-prices/commit/94ac7b9))
- Add threshold color for tooltip ([fb35df8](https://github.com/Kasparas-G55/shop-prices/commit/fb35df8))

### 🩹 Fixes

- Field items changed to itemStocks ([36bc489](https://github.com/Kasparas-G55/shop-prices/commit/36bc489))
- Tooltip displaying wrong amount on Buy 50 operation ([13b30be](https://github.com/Kasparas-G55/shop-prices/commit/13b30be))

### 💅 Refactors

- Redundant call, initialize static map ([01088a6](https://github.com/Kasparas-G55/shop-prices/commit/01088a6))
- Use consistent naming of store to shop ([d44de90](https://github.com/Kasparas-G55/shop-prices/commit/d44de90))

### 🏡 Chore

- Update submodule link ([bc868e0](https://github.com/Kasparas-G55/shop-prices/commit/bc868e0))
- Init CHANGELOG ([483e740](https://github.com/Kasparas-G55/shop-prices/commit/483e740))
- Update checkstyle.xml ([46c2f8d](https://github.com/Kasparas-G55/shop-prices/commit/46c2f8d))
- Reduce magic numbers ([bbe05a7](https://github.com/Kasparas-G55/shop-prices/commit/bbe05a7))

### ❤️ Contributors

- Kasparas-G55 ([@Kasparas-G55](https://github.com/Kasparas-G55))


## v1.0.0

### 🚀 Enhancements

- Display dynamic prices below shop items ([b95e750](https://github.com/Kasparas-G55/shop-prices/commit/b95e750))

### 🏡 Chore

- Create submodule for shop-scrapper ([5f2e4a9](https://github.com/Kasparas-G55/shop-prices/commit/5f2e4a9))
- Update submodule link ([c64e25e](https://github.com/Kasparas-G55/shop-prices/commit/c64e25e))

### ❤️ Contributors

- Kasparas-G55 ([@Kasparas-G55](https://github.com/Kasparas-G55))
