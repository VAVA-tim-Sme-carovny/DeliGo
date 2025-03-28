package com.deligo.DatabaseManager;

/**
 * Jednoduchá trieda, ktorá demonštruje univerzálne (generické)
 * funkcie na čítanie, zápis a update z/do databázy bez použitia ORM.
 */
public class DatabaseManager {

    /**
     * Univerzálna metóda na čítanie údajov z DB.
     * @param <T>   Typ, ktorý očakávame (napr. User, Product, atď.)
     * @param type  Trieda typu, ktorý chceme z DB načítať.
     * @param query Identifikátor, query alebo kľúč, podľa ktorého vieme z DB získať daný objekt.
     * @return Inštancia typu T načítaná z DB alebo null, ak nebola nájdená.
     */
    public <T> T readData(Class<T> type, Object query) {
        // 1. Tu by si použil napr. JDBC a spustil SELECT podľa query/identifikátora.
        // 2. Z ResultSetu by si vyparsoval dáta a vytvoril inštanciu T (cez konštruktor, reflection, atď.)
        // 3. Pre ukážku to len demonštrujeme.
        System.out.println("Reading data for type " + type.getSimpleName() + " with query: " + query);

        // V reálnej implementácii by si vytvoril a vrátil inštanciu T.
        // Tu pre demo vraciame null, aby bolo jasné, že je to len ukážka.
        return null;
    }

    /**
     * Univerzálna metóda na zápis nového objektu do DB.
     * @param <T>  Typ objektu, ktorý chceme zapísať.
     * @param data Inštancia, ktorú chceme uložiť do DB.
     * @return True, ak sa zápis podaril, inak false.
     */
    public <T> boolean writeData(T data) {
        // 1. Napríklad by si použil INSERT do DB podľa typu T a jeho polí.
        // 2. Pre demo iba vypíšeme, že zapisujeme.
        System.out.println("Writing data of type " + data.getClass().getSimpleName() + ": " + data.toString());

        // V reálnej implementácii by si použil PreparedStatement, atď.
        return true;
    }

    /**
     * Univerzálna metóda na update existujúceho objektu v DB.
     * @param <T>  Typ objektu, ktorý chceme updatnúť.
     * @param data Inštancia, ktorú chceme aktualizovať.
     * @return True, ak sa update podaril, inak false.
     */
    public <T> boolean updateData(T data) {
        // 1. Podľa typu T by si vyhodnotil, aké polia a ID sa majú aktualizovať.
        // 2. Pre demo iba vypíšeme, že updatujeme.
        System.out.println("Updating data of type " + data.getClass().getSimpleName() + ": " + data.toString());

        // V reálnej implementácii by si použil UPDATE query s WHERE ID = ... .
        return true;
    }
}



//// V reálnej aplikácii by si mohol mať niečo také:
//DatabaseManager dbManager = new DatabaseManager();
//
//// Príklad: Čítanie používateľa (User) podľa nejakého ID alebo query
//User user = dbManager.readData(User.class, 1234);
//if (user != null) {
//        System.out.println("Načítaný používateľ: " + user);
//}
//
//// Príklad: Zápis nového produktu
//Product product = new Product("Smartphone", 499.99);
//dbManager.writeData(product);
//
//// Príklad: Update existujúceho objektu
//product.setPrice(449.99);
//dbManager.updateData(product);