package io.omosh.dts;

import io.omosh.dts.services.daraja.DarajaApiServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DonationsApplicationTests {
	@Test
	void contextLoads() {
	}

	@Autowired
	private DarajaApiServiceImpl darajaApiServiceImpl;


	@Test
	void testPrintConfig() {
		darajaApiServiceImpl.printConfig();
	}
}
