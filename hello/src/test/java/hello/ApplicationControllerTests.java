package hello;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void noParamApplicationShouldReturnDefaultMessage() throws Exception {

		this.mockMvc.perform(get("/flag")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").value("Wrong Flag! flag{Flag}"));
	}

	@Test
	public void paramApplicationShouldReturnTailoredMessage1() throws Exception {

		this.mockMvc.perform(get("/flag").param("flag", "XYZ"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").value("Wrong Flag! flag{XYZ}"));
	}

        @Test
        public void paramApplicationShouldReturnTailoredMessage2() throws Exception {

                this.mockMvc.perform(get("/flag").param("flag", "3858FDF6-E53A-47AF-86FD-8CB3830B518F"))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").value("Congratulations, here is the flag! flag{C4reFu!withEnV}"));
        }

}
