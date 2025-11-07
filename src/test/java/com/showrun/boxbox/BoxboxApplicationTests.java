package com.showrun.boxbox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(properties = {
		"ncp.papago.client-id=dummy-client-id",
		"ncp.papago.client-secret=dummy-client-secret"
})
class BoxboxApplicationTests {

	@Test
	void contextLoads() {
	}

}
