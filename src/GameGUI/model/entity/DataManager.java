package GameGUI.model.entity;

public class DataManager {
    private static HeroData currentData = new BaseHeroData();

    public static HeroData getData() {
        return currentData;
    }

    public static void setData(HeroData newData) {
        currentData = newData;
    }
}