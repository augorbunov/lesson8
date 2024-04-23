package guru.qa;

import guru.qa.data.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class LoanCalcTest extends TestBase {

    @BeforeEach
    void openPage() {
        open("/kalkulyator-ipoteki");
    }

    @ValueSource(strings = {
            "17.5", "17,5"
    })
    @DisplayName("Проверка работы любых разделителей десятичной части числа в поле Процентная ставка. Источник данных: ValueSource")
    @ParameterizedTest
    void checkCalcForCorrectInputsWithValueSource(String percent) {
        $("[name=\"cost\"]").setValue("3000000");
        $("[name=\"start_sum\"]").setValue("1500000");
        $("[name=\"period\"]").setValue("10");
        $("[name=\"percent\"]").setValue(percent);
        $(".calc-submit").click();

        $(".calc-result-value.result-placeholder-monthlyPayment").shouldHave(text("26 546,81"));
    }

    @CsvSource(value = {
            "3000000| 1500000| 10| 17| 26 069,65",
            "12000000| 8000000| 30| 6| 23 982,02"
    }, delimiter = '|')
    @DisplayName("Проверка результата расчета калькулятора ипотеки для заданных параметров. Источник данных: CsvSource")
    @ParameterizedTest(name = "Ежемесячный платеж для ипотеки стоимостью {0}, с первоначальным взносом {1} " +
            "по ставке {3} на {2} лет должен быть {4}")
    void checkCalcForCorrectInputsWithCsvSource(String cost, String startSum, String period, String percent, String monthlyPayment) {
        $("[name=\"cost\"]").setValue(cost);
        $("[name=\"start_sum\"]").setValue(startSum);
        $("[name=\"period\"]").setValue(period);
        $("[name=\"percent\"]").setValue(percent);
        $(".calc-submit").click();

        $(".calc-result-value.result-placeholder-monthlyPayment").shouldHave(text(monthlyPayment));
    }


    static Stream<Arguments> checkCalculationForDifferentTypesOfPeriod() {
        return Stream.of(
                Arguments.of(
                        Period.M,
                        List.of("3000000", "1500000", "120", "17", "26 069,65")
                ),
                Arguments.of(
                        Period.Y,
                        List.of("3000000", "1500000", "10", "17", "26 069,65")
                )
        );
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("Проверка расчета при задании срока кредита в месяцах или годах. Источник данных: MethodSource")
    void checkCalculationForDifferentTypesOfPeriod(Period period, List<String> loanParameters) {

        $("[name=\"cost\"]").setValue(loanParameters.get(0));
        $("[name=\"start_sum\"]").setValue(loanParameters.get(1));
        $("[name=\"period\"]").setValue(loanParameters.get(2));
        $("[name=\"percent\"]").setValue(loanParameters.get(3));

        //$(("[name=\"period_type\"] [value=\"M\"]").)
        $(String.format("[name=\"period_type\"] [value=\"%s\"]", period)).click();
        $(".calc-submit").click();

        $(".calc-result-value.result-placeholder-monthlyPayment").shouldHave(text(loanParameters.get(4)));
    }
}