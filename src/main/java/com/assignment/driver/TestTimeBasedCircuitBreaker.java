package com.assignment.driver;

import com.assignment.service.ServiceWithTimeBasedCircuitBreaker;
import lombok.SneakyThrows;

import java.util.Set;

public class TestTimeBasedCircuitBreaker {
    @SneakyThrows
    public static void main(String[] args) {


        // Simple Service is integrated with CustomCircuitBreaker
        /*  This Service Instance Uses Time Based Circuit Breaker that opens after failing for 3 seconds
                and after 2000 millis goes into half open and allows 4 requests,
                if 2 out of 4 are successful, then closes else opens.
         */
        ServiceWithTimeBasedCircuitBreaker service = new ServiceWithTimeBasedCircuitBreaker(Set.of(3, 5, 7, 9, 11, 13));


        run(service, 6, "Basic case !");
        run(service, 3, "First failure case !");
        Thread.sleep(2000);
        run(service, -3, "Basic case resetting failure times!");
        Thread.sleep(1000);
        run(service, 3, "First failure case !");
        Thread.sleep(1000);
        run(service, 7, "Second failure case !");
        Thread.sleep(1000);
        run(service, 5, "Third failure case to open CB !");
        run(service, 14, "Should get fallback response 1");
        Thread.sleep(2000);
        run(service, 3, "Half Open 1.1 !");
        run(service, 5, "Half Open 1.2 !");
        run(service, 7, "Half Open 1.3 !");
        run(service, 8, "Half Open 1.4 ! : Should Open the CB");
        run(service, 14, "Should get fallback response 2");

        Thread.sleep(2000);
        run(service, 10, "Half Open 2.1 !");
        run(service, 20, "Half Open 2.2 !");
        run(service, 30, "Half Open 2.3 !");
        run(service, 40, "Half Open 2.4 ! : Should close the CB");


        service.shutDown();
    }

    private static void run(ServiceWithTimeBasedCircuitBreaker service, Integer input, String message) {
        System.out.println(message);
        try {
            System.out.println(service.getSquare(input));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("============");
    }

}
