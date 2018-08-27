package pl.piomin.services.trip;

import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.SimulationSource;
import io.specto.hoverfly.junit.dsl.HoverflyDsl;
import io.specto.hoverfly.junit.dsl.HttpBodyConverter;
import io.specto.hoverfly.junit.dsl.ResponseCreators;
import io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import pl.piomin.services.trip.client.PassengerManagementClient;
import pl.piomin.services.trip.model.Passenger;

@SpringBootTest(properties = "eureka.client.registerWithEureka=false")
@RunWith(SpringRunner.class)
public class TripDiffIntegrationTests {

    @Autowired
    PassengerManagementClient passengerManagementClient;

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inDiffMode(SimulationSource.dsl(
            HoverflyDsl.service(HoverflyMatchers.startsWith("minkowp-l.p4.org"))
                    .get(HoverflyMatchers.startsWith("/passengers/"))
                    .willReturn(ResponseCreators.success(HttpBodyConverter.jsonWithSingleQuotes("{'id':1,'name':'John Smith'}")).header("Content-Type", "application/json;charset=UTF-8"))
    ), HoverflyConfig.localConfigs().proxyLocalHost()).printSimulationData();

    @Test
    public void testGetPassengerByLogin() {
        Passenger passenger = passengerManagementClient.getPassenger("walker");
        Assert.notNull(passenger, "No passenger");
        hoverflyRule.assertThatNoDiffIsReported(true);
    }

}
