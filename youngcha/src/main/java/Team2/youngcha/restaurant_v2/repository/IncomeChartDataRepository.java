package Team2.youngcha.restaurant_v2.repository;

import Team2.youngcha.restaurant_v2.domain.Income;
import Team2.youngcha.restaurant_v2.responseBody.IncomeChartData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface IncomeChartDataRepository extends CrudRepository<Income, String> {

    @Query(value="SELECT dish, SUM(dish_count) AS sumDishCount, SUM(profit) AS sumProfit FROM income \r\n" + "GROUP BY dish" , nativeQuery=true)
    Collection<IncomeChartData> getChartData();

    @Query(value = "SELECT dish, SUM(dish_count) AS sumDishCount FROM income \r\n" + "GROUP BY dish" , nativeQuery=true)
    Collection<IncomeChartData> getDishCount();
}
