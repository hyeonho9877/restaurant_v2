package Team2.youngcha;

import Team2.youngcha.restaurant_v2.MainApplication;
import Team2.youngcha.restaurant_v2.controller.ReservationForm;
import Team2.youngcha.restaurant_v2.domain.*;
import Team2.youngcha.restaurant_v2.repository.ManagerRepository;
import Team2.youngcha.restaurant_v2.service.CustomerService;
import Team2.youngcha.restaurant_v2.service.ManagerService;
import Team2.youngcha.restaurant_v2.service.ReservationService;
import Team2.youngcha.restaurant_v2.service.WalkInService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = MainApplication.class)
@Transactional
class YoungchaApplicationTests {

    @Autowired
    EntityManager em;

    @Autowired
    ReservationService reservationService;

    @Autowired
    ManagerService managerService;

    @Autowired
    ManagerRepository managerRepository;

    @Autowired
    WalkInService walkInService;

    @Autowired
    CustomerService customerService;


    @Test
    void compareLocalDateTime() {
        // given
        LocalDateTime parse = LocalDateTime.parse("2020-10-25T13:25:30");

        // when
        boolean before = parse.isBefore(LocalDateTime.now());

        // then
        assertThat(before).isEqualTo(true);
    }

    @Test
    void testDuplicateReservationDate() {

        // given
        Reservation reservation = new Reservation();
        reservation.setReservationDate(LocalDateTime.of(2013, 11, 25, 16, 50));
        reservation.setTableNo("3");
        reservation.setCustomerID("go");

        // when
        reservationService.join("hyeonho9877","hyeonho9877@gmail.com","?????????",new ReservationForm());

        // then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> reservationService.join("hyeonho9877","hyeonho9877@gmail.com","?????????",new ReservationForm()));
        assertThat(e.getMessage()).isEqualTo("??????????????? ??????");
    }

    @Test
    @Commit
    void testTableCreation() {
        int tableCount = 5;

        int result = managerService.setTable(tableCount);

        assertThat(result).isEqualTo(tableCount);
    }

    @Test
    void ????????????????????????() {
        // given, when
        List<Boolean> booleans = walkInService.checkTable(null);

        // then
        assertThat(Collections.frequency(booleans, false)).isEqualTo(1);
    }

    @Test
    void ?????????????????????????????????() {

        // given, when
        Boolean hyeonho9877 = managerService.changeTable("hyeonho9877", "3");

        // then
        assertThat(hyeonho9877).isEqualTo(true);
    }

    @Test
    void ????????????????????????????????????() {
        Boolean hyeonho9877 = managerService.changeTable("hyeonho9877", "2");

        // then
        assertThat(hyeonho9877).isEqualTo(false);
    }

    @Test
    void ???????????????????????????() {

        // given, when
        List<Boolean> hyeonho9877 = reservationService.findValidTables(LocalDateTime.parse("2021-05-29 13:20",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "3");

        // then
        assertThat(Collections.frequency(hyeonho9877,true)).isEqualTo(4);
    }


    @Test
    void ???????????????????????????() {

        // given, when
        List<Boolean> hyeonho9877 = reservationService.findValidTables(LocalDateTime.parse("2021-05-29 13:20",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "3");

        // then
        assertThat(hyeonho9877.get(0)).isEqualTo(false);
    }

    @Test
    void ????????????????????????(){

        // given
        reservationService.updateReservation("hyeonho9877", "2021-05-29-13:20", "2021-05-29-17:20", "3", "1");
        LocalDateTime parse = LocalDateTime.parse("2021-05-29-17:20", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm"));

        // when
        try {
            Reservation result = em.createQuery("select r from Reservation r where r.reservationDate=:resDate", Reservation.class)
                    .setParameter("resDate", parse)
                    .getSingleResult();

            // then
            assertThat(result.getTableNo()).isEqualTo("1");
        }catch (Exception e){
            fail();
        }
    }

    @Test
    void ?????????????????????(){

        //given
        List<String> tableList = new ArrayList<String>();
        tableList.add("2");
        tableList.add("4");
        tableList.add("4");
        tableList.add("4");
        tableList.add("2");
        tableList.add("3");
        tableList.add("3");
        tableList.add("4");
        tableList.add("4");

        managerService.joinTable(tableList);
        List<TableInfo> result = em.createQuery("select t from TableInfo t", TableInfo.class).getResultList();

        assertThat(result.size()).isEqualTo(tableList.size());
    }


    @Test
    void ??????????????????(){
        Map<String, String> testMap = new HashMap<>();
        testMap.put("Seafood Platter","18000");
        testMap.put("Soup of the Day","8000");
        testMap.put("Grilled Salmon","13000");
        testMap.put("Mixed Green Salad","10000");
        testMap.put("Lobster Cocktail","18000");

        managerService.editDishes(testMap);
        List<Menu> menus = managerRepository.listMenus();
        assertThat(menus.size()).isEqualTo(5);
    }

    @Test
    void ?????????????????????(){

        // given
        Map<String, String> testMap = new HashMap<>();
        Optional<Menu> result = managerRepository.getMenuByDish("Seafood Platter");
        int beforeCount = result.get().getSalesCount();
        testMap.put("Seafood Platter","18000");
        testMap.put("Soup of the Day","8000");
        testMap.put("Grilled Salmon","13000");
        testMap.put("Mixed Green Salad","10000");
        testMap.put("Seafood Platter","15000");

        // when
        managerService.editDishes(testMap);
        result = managerRepository.getMenuByDish("Seafood Platter");
        int afterCount = result.get().getSalesCount();

        //then
        assertThat(beforeCount).isEqualTo(afterCount).isNotEqualTo(0);
    }

    @Test
    void ?????????????????????(){

        List<Income> incomeList = managerService.getIncome();
        int sum = 0;
        for (Income income : incomeList) {
            sum+=income.getProfit();
        }

        assertThat(sum).isEqualTo(164000);
    }

    @Test
    void ?????????????????????(){
        List<Income> incomeList = managerService.getIncome();
        Map<String, Integer> dishWithProfit = managerService.getDishWithProfit(incomeList);

        assertThat(dishWithProfit.get("Soup of the Day")).isEqualTo(64000);
    }

    @Test
    void ???????????????????????????(){
        List<Income> incomeList = managerService.getIncome();
        Map<String, Integer> dishWithCount = managerService.getDishWithCount(incomeList);

        assertThat(dishWithCount.get("Lobster Cocktail")).isEqualTo(1);
    }

    @Test
    void ?????????????????????(){
        List<Boolean> hyeonho9877 = reservationService.findValidTables(LocalDateTime.parse("2021-05-29 13:20",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "3");
        assertThat(Collections.frequency(hyeonho9877, false)).isEqualTo(3);
    }

    @Test
    void ????????????(){
        LocalDateTime target = LocalDateTime.of(2021, 6, 3, 18, 45);
        managerService.setArrivalTime("hyeonho9877",target);
        Reservation result = em.createQuery("select r from Reservation r where r.customerID=:cid and r.reservationDate=:resDate", Reservation.class)
                .setParameter("cid", "hyeonho9877")
                .setParameter("resDate", target)
                .getSingleResult();
        assertThat(result.getArrivalTime().isBefore(LocalDateTime.now().plusMinutes(1)) && result.getArrivalTime().isAfter(LocalDateTime.now().minusMinutes(1))).isEqualTo(true);
    }

    @Test
    void ???????????????????????????????????????(){
        LocalDateTime now = LocalDateTime.of(2021,6,4,18,45);
        managerService.enrollIncome("hyeonho9877","Seafood Platter,Soup of the Day","3,2",now);

        List<Income> result = em.createQuery("select i from Income i where i.incomeDate=:argIncomeDate", Income.class)
                .setParameter("argIncomeDate", now.toLocalDate())
                .getResultList();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void ???????????????????????????????????????(){
        LocalDateTime now = LocalDateTime.of(2021,6,4,18,45);
        managerService.enrollIncome("hyeonho9877","Seafood Platter,Soup of the Day","3,2",now);

        List<Income> result = em.createQuery("select i from Income i where i.incomeDate=:argIncomeDate", Income.class)
                .setParameter("argIncomeDate", now.toLocalDate())
                .getResultList();

        int sum = 0;
        for(Income income : result){
            sum+=income.getProfit();
        }

        assertThat(sum).isEqualTo(54000+16000);
    }

    @Test
    void ???????????????(){
        // given, when
        String result = customerService.isAlreadyJoined("?????????", "01086133952");

        // then
        assertThat(result).isEqualTo("hyeonho9877");
    }

    @Test
    void ??????????????????(){
        // given, when
        Boolean result = customerService.findUserByPhoneNoAndNameAndCid("01086133952", "?????????", "hyeonho9877");

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    void ??????????????????(){
        // given, when
        customerService.changePSW("hyeonho9877", "changedPSW");
        Customer customer = em.find(Customer.class, "hyeonho9877");

        // then
        assertThat(customer.getPsw()).isEqualTo("changedPSW");
    }

    @Test
    void ???????????????(){

        // given, when
        managerService.editSaleCount("hyeonho9877",LocalDateTime.of(2021,6,3,18,45));
        Menu result = em.find(Menu.class,"Seafood Platter");
        System.out.println(result.getSalesCount()+result.getDish());
        // then
        assertThat(result.getSalesCount()).isEqualTo(5);
    }

    @Test
    void ???????????????(){

        // given, when
        managerService.editSaleCount("hyeonho9877",LocalDateTime.of(2021,6,3,18,45));
        Menu result = em.find(Menu.class,"Seafood Platter");
        System.out.println(result.getSalesCount()+result.getDish());
        // then
        assertThat(result.getStock()).isEqualTo(3);
    }

    @Test
    void ??????????????????(){

        // given
        LocalDateTime now = LocalDateTime.now();
        managerService.enrollIncome("hyeonho9877","Seafood Platter,","1,",now);

        // when
        Income result = em.createQuery("select i from Income i where i.incomeDate=:argIncomeDate", Income.class)
                .setParameter("argIncomeDate", now.toLocalDate())
                .getSingleResult();
        Menu seafood_platter = em.find(Menu.class, "Seafood Platter");
        System.out.println(result.getProfit());

        // then
        assertThat(result.getProfit()).isEqualTo((int)Math.round(seafood_platter.getPrice()*1*(1-5.0/100)));
    }
}
