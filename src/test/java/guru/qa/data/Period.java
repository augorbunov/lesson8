package guru.qa.data;

public enum Period {
    M("месяцев"),
    Y("лет");

    public final String description;

    Period(String description) {
        this.description = description;
    }
}