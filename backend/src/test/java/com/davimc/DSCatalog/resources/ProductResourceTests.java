package com.davimc.DSCatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.davimc.DSCatalog.DTO.ProductDTO;
import com.davimc.DSCatalog.services.ProductService;
import com.davimc.DSCatalog.services.exceptions.DatabaseException;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import com.davimc.DSCatalog.tests.Factory;
import com.davimc.DSCatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TokenUtil tokenUtil;
    @MockBean
    private ProductService service;
    private PageImpl<ProductDTO> page;

    private ProductDTO productDTO;
    private long existingId;
    private long nonExistingId;
    private long dependentId;

    private String adminUsername;
    private String adminPassword;
    @BeforeEach
    public void setup() {
        adminUsername="maria@gmail.com";
        adminPassword="123456";

        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        productDTO = Factory.createProductDTO();

        page = new PageImpl(List.of(productDTO));

        when(service.findAllPaged(any(),any(),any())).thenReturn(page);
        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(nonExistingId)).thenThrow(ObjectNotFoundException.class);
        when(service.update(eq(existingId),any())).thenReturn(productDTO);
        when(service.update(eq(nonExistingId),any())).thenThrow(ObjectNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(ObjectNotFoundException.class).when(service).delete(nonExistingId);
        doThrow(DatabaseException.class).when(service).delete(dependentId);

        when(service.insert(any())).thenReturn(productDTO);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        ResultActions result = mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }
    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/{id}",nonExistingId)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
    @Test
    public void insertShouldReturnProductDto() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.imgUrl").exists());
        result.andExpect(jsonPath("$.date").exists());
    }
    @Test
    public void updateShouldReturnProductDtoWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}",existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }
    @Test
    public void updateShouldThrowObjectNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}",nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception{
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        ResultActions result =
                mockMvc.perform(delete("/products/{id}",existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }
    @Test
    public void deleteShouldThrowObjectNotFoundExceptionWhenIdDoesNotExist() throws Exception{
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        ResultActions result =
                mockMvc.perform(delete("/products/{id}",nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
    @Test
    public void deleteShouldThrowDataIntegrityExceptionWhenIdHaveDependency() throws Exception{
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        ResultActions result =
                mockMvc.perform(delete("/products/{id}",dependentId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
    }
}
