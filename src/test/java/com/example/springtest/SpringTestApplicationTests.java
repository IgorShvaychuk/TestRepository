package com.example.springtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SpringTestApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createUserTest() throws Exception {
		String userJson = "{\"email\":\"test@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthDate\":\"1990-01-01\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(jsonPath("$.email").value("test@example.com"));
	}

	@Test
	void updateUserTest() throws Exception {
		String userJson = "{\"email\":\"test@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthDate\":\"1990-01-01\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		String updatedUserJson = "{\"email\":\"updated@example.com\",\"firstName\":\"Updated\",\"lastName\":\"User\",\"birthDate\":\"1990-02-02\"}";
		mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updatedUserJson))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.email").value("updated@example.com"));
	}

	@Test
	void deleteUserTest() throws Exception {
		String userJson = "{\"email\":\"test@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthDate\":\"1990-01-01\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("User deleted successfully"));
	}

	@Test
	void searchUsersByBirthDateRangeTest() throws Exception {
		String userJson1 = "{\"email\":\"test1@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthDate\":\"1990-01-01\"}";
		String userJson2 = "{\"email\":\"test2@example.com\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"birthDate\":\"1991-01-01\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson1))
				.andExpect(MockMvcResultMatchers.status().isCreated());
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson2))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.get("/users/search")
				.param("from", "1990-01-01")
				.param("to", "1991-01-01"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].email").value("test1@example.com"))
				.andExpect(jsonPath("$[1].email").value("test2@example.com"));
	}

}
